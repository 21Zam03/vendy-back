package com.zam.vendy.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.pestana.PestanaRequest;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Pestana;
import com.zam.vendy.entities.Seccion;
import com.zam.vendy.entities.enums.Plantilla;
import com.zam.vendy.exceptions.PestanaNoVaciaException;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.PestanaRepository;
import com.zam.vendy.repositories.SeccionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PestanaService {

    private final PestanaRepository pestanaRepository;
    private final SeccionRepository seccionRepository;
    private final NegocioService negocioService;

    @Transactional
    public List<Pestana> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        asegurarSinHuerfanas(negocio);
        return pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    @Transactional
    public Pestana crear(Integer idUsuario, PestanaRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        int siguienteOrden = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId()).size();

        Pestana pestana = Pestana.builder()
                .negocio(negocio)
                .nombre(request.getNombre())
                .orden(siguienteOrden)
                .esHome(Boolean.TRUE.equals(request.getEsHome()))
                .build();

        return pestanaRepository.save(pestana);
    }

    @Transactional
    public Pestana actualizar(Integer idUsuario, Long pestanaId, PestanaRequest request) {
        Pestana pestana = obtenerPropia(idUsuario, pestanaId);
        pestana.setNombre(request.getNombre());
        return pestana;
    }

    @Transactional
    public void eliminar(Integer idUsuario, Long pestanaId) {
        Pestana pestana = obtenerPropia(idUsuario, pestanaId);

        if (!seccionRepository.findByPestana_IdOrderByOrdenAscIdAsc(pestana.getId()).isEmpty()) {
            throw new PestanaNoVaciaException(pestana.getNombre());
        }

        pestanaRepository.delete(pestana);
    }

    @Transactional
    public List<Pestana> reordenar(Integer idUsuario, List<Long> idsEnOrden) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());

        for (int i = 0; i < idsEnOrden.size(); i++) {
            final Long id = idsEnOrden.get(i);
            final int orden = i;
            propias.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .ifPresent(p -> p.setOrden(orden));
        }

        return pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    public Pestana obtenerPropia(Integer idUsuario, Long pestanaId) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        return pestanaRepository.findByIdAndNegocio_Id(pestanaId, negocio.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pestaña no encontrada"));
    }

    // Autocura negocios de antes de que existieran las pestañas: cualquier sección sin
    // pestaña se agrupa en una "General" recién creada, para que la jerarquía pestaña →
    // sección quede completa sin tener que migrar datos a mano.
    @Transactional
    public void asegurarSinHuerfanas(Negocio negocio) {
        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
        List<Seccion> huerfanas = seccionRepository.findByNegocio_IdAndPestanaIsNull(negocio.getId());

        if (propias.isEmpty()) {
            // Negocios de antes de que el alta armara la "General" automáticamente (ver
            // NegocioService.guardar): sin esto, se quedarían para siempre sin ninguna
            // pestaña y el catálogo caía en la grilla genérica de siempre, con los colores
            // de Apariencia — nunca en la vista neutra del catálogo general.
            Pestana general = pestanaRepository.save(Pestana.builder()
                    .negocio(negocio)
                    .nombre("General")
                    .orden(0)
                    .esGeneral(true)
                    .build());
            huerfanas.forEach(seccion -> seccion.setPestana(general));
        } else if (!huerfanas.isEmpty()) {
            int siguienteOrden = propias.size();
            Pestana general = pestanaRepository.save(Pestana.builder()
                    .negocio(negocio)
                    .nombre("General")
                    .orden(siguienteOrden)
                    .esGeneral(true)
                    .build());
            huerfanas.forEach(seccion -> seccion.setPestana(general));
        }

        asegurarEsHomeMarcado(negocio);
        asegurarEsGeneralMarcado(negocio);
    }

    // Autocura negocios de Moda de antes de que existiera el campo esHome (incluye a los
    // que ya habían renombrado su pestaña "Home" a otra cosa, así que buscar por nombre no
    // alcanza): si ninguna de sus pestañas tiene la marca todavía, se la pone a la primera
    // por orden — que es la que la plantilla siempre crea primero. Al ser "ninguna tiene la
    // marca todavía" la condición, esto corre una sola vez por negocio; nunca pisa una
    // elección ya hecha (a mano o por esta misma autocuración).
    private void asegurarEsHomeMarcado(Negocio negocio) {
        if (negocio.getApariencia().getPlantilla() != Plantilla.MODA) {
            return;
        }

        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
        boolean yaHayHome = propias.stream().anyMatch(p -> Boolean.TRUE.equals(p.getEsHome()));
        if (!yaHayHome && !propias.isEmpty()) {
            propias.get(0).setEsHome(true);
        }
    }

    // Autocura pestañas "General" creadas antes de que existiera el campo esGeneral: si
    // hay una pestaña llamada literalmente "General" sin la marca, se la pone. A
    // diferencia de esHome, acá alcanza con el nombre porque "General" siempre lo pone
    // esta misma autocuración (el negocio no la crea ni la nombra a mano).
    private void asegurarEsGeneralMarcado(Negocio negocio) {
        pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId()).stream()
                .filter(p -> !Boolean.TRUE.equals(p.getEsGeneral()))
                .filter(p -> "general".equalsIgnoreCase(p.getNombre() == null ? "" : p.getNombre().trim()))
                .findFirst()
                .ifPresent(p -> p.setEsGeneral(true));
    }
}
