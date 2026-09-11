package com.zam.vendy.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.pestana.PestanaRequest;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.Pestana;
import com.zam.vendy.entities.Seccion;
import com.zam.vendy.exceptions.PestanaEstructuraFijaException;
import com.zam.vendy.exceptions.ResourceNotFoundException;
import com.zam.vendy.repositories.NegocioRepository;
import com.zam.vendy.repositories.PestanaRepository;
import com.zam.vendy.repositories.ProductoRepository;
import com.zam.vendy.repositories.SeccionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PestanaService {

    // Estructura fija por ahora: todo negocio tiene EXACTAMENTE estas 2 pestañas, con estos
    // nombres — ver crear()/actualizar()/eliminar() más abajo, que la hacen cumplir, y
    // asegurarSinHuerfanas(), que la garantiza aunque el negocio nunca haya llamado a estos
    // endpoints (altas viejas, plantilla recién elegida, etc.).
    private static final String NOMBRE_INICIO = "Inicio";
    private static final String NOMBRE_GENERAL = "General";

    // La pestaña "General" tiene siempre exactamente esta única Sección, para todas las
    // plantillas — ver asegurarSeccionGeneralUnica más abajo.
    private static final String NOMBRE_SECCION_GENERAL = "Catálogo general";

    private final PestanaRepository pestanaRepository;
    private final SeccionRepository seccionRepository;
    private final ProductoRepository productoRepository;
    private final NegocioService negocioService;
    private final NegocioRepository negocioRepository;

    @Transactional
    public List<Pestana> listar(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        asegurarSinHuerfanas(negocio);
        return pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
    }

    // Solo puede crear la pestaña que todavía le falte al negocio para completar el par fijo
    // "Inicio"/"General" (esto es lo único que la usa hoy: BusinessProfileView.applyTemplate,
    // al elegir una plantilla por primera vez). El nombre define el tipo — no confía en lo
    // que mande el cliente para esHome/esGeneral — y siempre se guarda con la capitalización
    // canónica, sin importar cómo la haya escrito quien llama.
    @Transactional
    public Pestana crear(Integer idUsuario, PestanaRequest request) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        String nombre = request.getNombre() == null ? "" : request.getNombre().trim();
        boolean esGeneral = NOMBRE_GENERAL.equalsIgnoreCase(nombre);
        boolean esHome = NOMBRE_INICIO.equalsIgnoreCase(nombre);
        if (!esGeneral && !esHome) {
            throw new PestanaEstructuraFijaException(
                    "Por ahora cada negocio tiene exactamente 2 pestañas fijas: \"Inicio\" y \"General\" — no se pueden crear otras.");
        }

        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
        boolean yaExisteEsteTipo = propias.stream()
                .anyMatch(p -> esGeneral ? Boolean.TRUE.equals(p.getEsGeneral()) : Boolean.TRUE.equals(p.getEsHome()));
        if (yaExisteEsteTipo || propias.size() >= 2) {
            throw new PestanaEstructuraFijaException(
                    "La pestaña \"" + (esGeneral ? NOMBRE_GENERAL : NOMBRE_INICIO) + "\" ya existe.");
        }

        Pestana pestana = Pestana.builder()
                .negocio(negocio)
                .nombre(esGeneral ? NOMBRE_GENERAL : NOMBRE_INICIO)
                .orden(propias.size())
                .esHome(esHome)
                .esGeneral(esGeneral)
                .build();

        return pestanaRepository.save(pestana);
    }

    // Los nombres "Inicio"/"General" son fijos por ahora — ninguna pestaña se puede
    // renombrar (ver la constante NOMBRE_INICIO/NOMBRE_GENERAL arriba). obtenerPropia()
    // sigue corriendo antes para devolver 404 si el id no es del negocio, en vez de un 409
    // engañoso.
    @Transactional
    public Pestana actualizar(Integer idUsuario, Long pestanaId, PestanaRequest request) {
        obtenerPropia(idUsuario, pestanaId);
        throw new PestanaEstructuraFijaException(
                "Por ahora los nombres \"Inicio\" y \"General\" son fijos y no se pueden cambiar.");
    }

    // "Inicio" y "General" son las únicas pestañas que un negocio puede tener por ahora —
    // ninguna de las dos se puede eliminar (si se permitiera, asegurarSinHuerfanas la
    // volvería a crear vacía en el siguiente listar() de todas formas).
    @Transactional
    public void eliminar(Integer idUsuario, Long pestanaId) {
        obtenerPropia(idUsuario, pestanaId);
        throw new PestanaEstructuraFijaException(
                "\"Inicio\" y \"General\" son las únicas pestañas del negocio — no se pueden eliminar.");
    }

    // A diferencia de crear/renombrar/eliminar, esto sí está permitido: no cambia la
    // estructura fija del negocio, solo si esa pestaña se ve o no en el catálogo público
    // (sus secciones/productos siguen intactos, solo se pausa). Nunca se puede desactivar
    // la única pestaña que le quedaría activa al negocio — el catálogo siempre necesita
    // al menos una visible.
    @Transactional
    public Pestana cambiarActiva(Integer idUsuario, Long pestanaId, boolean activa) {
        Pestana pestana = obtenerPropia(idUsuario, pestanaId);

        if (!activa) {
            List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(pestana.getNegocio().getId());
            boolean quedaOtraActiva = propias.stream()
                    .anyMatch(p -> !p.getId().equals(pestanaId)
                            && (Boolean.TRUE.equals(p.getEsHome()) || Boolean.TRUE.equals(p.getEsGeneral()))
                            && !Boolean.FALSE.equals(p.getActiva()));
            if (!quedaOtraActiva) {
                throw new PestanaEstructuraFijaException(
                        "El negocio debe tener al menos una pestaña activa en su catálogo.");
            }
        }

        pestana.setActiva(activa);
        return pestana;
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
    //
    // Además garantiza el modelo actual, "solo 2 pestañas: Inicio y General" (ver
    // visiblePestanas en el frontend) — sin importar cómo haya quedado la estructura real
    // en la base de datos (pestañas viejas con otros nombres, negocios que aplicaron una
    // plantilla antes de que tuviera esta regla, etc.): esas pestañas viejas se absorben
    // (ver absorberPestanasExtra) — nunca se pierden sus secciones/productos, quedan
    // accesibles igual desde "General" (que siempre muestra el catálogo completo, sin
    // filtrar por pestaña), solo la pestaña vieja en sí, ya vacía, deja de existir.
    @Transactional
    public void asegurarSinHuerfanas(Negocio negocio) {
        // Bloquea la fila del negocio hasta el final de esta transacción: dos requests
        // concurrentes del mismo negocio (dos pestañas del navegador, un doble clic, etc.)
        // se ejecutan una detrás de la otra en vez de leer ambas "todavía sin Inicio" al
        // mismo tiempo y crear cada una la suya — sin este bloqueo eso duplicaba la
        // pestaña "Inicio" (o "General").
        negocioRepository.lockById(negocio.getId());

        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
        List<Seccion> huerfanas = seccionRepository.findByNegocio_IdAndPestanaIsNull(negocio.getId());

        if (propias.isEmpty()) {
            // Negocios de antes de que el alta armara la "General" automáticamente (ver
            // NegocioService.guardar): sin esto, se quedarían para siempre sin ninguna
            // pestaña y el catálogo caía en la grilla genérica de siempre, con los colores
            // de Apariencia — nunca en la vista neutra del catálogo general.
            Pestana general = pestanaRepository.save(Pestana.builder()
                    .negocio(negocio)
                    .nombre(NOMBRE_GENERAL)
                    .orden(0)
                    .esGeneral(true)
                    .build());
            huerfanas.forEach(seccion -> seccion.setPestana(general));
        } else if (!huerfanas.isEmpty()) {
            int siguienteOrden = propias.size();
            Pestana general = pestanaRepository.save(Pestana.builder()
                    .negocio(negocio)
                    .nombre(NOMBRE_GENERAL)
                    .orden(siguienteOrden)
                    .esGeneral(true)
                    .build());
            huerfanas.forEach(seccion -> seccion.setPestana(general));
        }

        asegurarEsGeneralMarcado(negocio);
        asegurarEsHomeMarcado(negocio);
        asegurarInicioAntesQueGeneral(negocio);
        absorberPestanasExtra(negocio);
        asegurarSeccionGeneralUnica(negocio);
    }

    // "General" tiene siempre EXACTAMENTE una Sección, "Catálogo general" — nunca varias
    // con nombres distintos (ej. "Destacados" que traía alguna plantilla vieja, o las que
    // absorberPestanasExtra fue juntando ahí). El catálogo público de "General" ya muestra
    // todos los productos sin agrupar por sección (ver isGeneralTab en
    // CatalogTemplateRenderer.vue), así que tener más de una ahí nunca aportaba nada — solo
    // confundía en "Pestañas y secciones" del editor. Si hay productos en otras secciones
    // de "General", se mueven a la única que queda antes de borrar las demás — nunca se
    // pierde un producto, ni de otra plantilla ni de un rubro sin definir todavía.
    private void asegurarSeccionGeneralUnica(Negocio negocio) {
        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
        Pestana general = propias.stream().filter(p -> Boolean.TRUE.equals(p.getEsGeneral())).findFirst().orElse(null);
        if (general == null) {
            return;
        }

        List<Seccion> secciones = seccionRepository.findByPestana_IdOrderByOrdenAscIdAsc(general.getId());
        Seccion canonica = secciones.stream()
                .filter(s -> NOMBRE_SECCION_GENERAL.equalsIgnoreCase(s.getNombre() == null ? "" : s.getNombre().trim()))
                .findFirst()
                .orElse(null);

        if (canonica == null) {
            if (secciones.isEmpty()) {
                canonica = seccionRepository.save(Seccion.builder()
                        .negocio(negocio)
                        .pestana(general)
                        .nombre(NOMBRE_SECCION_GENERAL)
                        .orden(0)
                        .build());
            } else {
                canonica = secciones.get(0);
                canonica.setNombre(NOMBRE_SECCION_GENERAL);
            }
        }

        Long idCanonica = canonica.getId();
        List<Seccion> sobrantes = secciones.stream().filter(s -> !s.getId().equals(idCanonica)).toList();
        for (Seccion sobrante : sobrantes) {
            Seccion destino = canonica;
            productoRepository.findByNegocio_IdAndSeccion_Id(negocio.getId(), sobrante.getId())
                    .forEach(producto -> producto.setSeccion(destino));
        }
        seccionRepository.deleteAll(sobrantes);
    }

    // Cualquier pestaña que no sea "Inicio" ni "General" (nombres viejos como
    // "Promociones", "Varones"/"Mujeres" de una Moda anterior, etc. — de antes de que
    // existiera esta regla) se absorbe: sus Secciones pasan a "General" (ahí se siguen
    // viendo, ese catálogo muestra TODOS los productos sin filtrar por pestaña) y la
    // pestaña vieja, ya vacía, se elimina. A diferencia de eliminar() (que el negocio no
    // puede invocar), esto es autocuración interna — nunca hay motivo para dejarlas dando
    // vueltas ocultas y sin dueño en "Pestañas y secciones" del editor.
    private void absorberPestanasExtra(Negocio negocio) {
        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
        List<Pestana> extra = propias.stream()
                .filter(p -> !Boolean.TRUE.equals(p.getEsHome()) && !Boolean.TRUE.equals(p.getEsGeneral()))
                .toList();
        if (extra.isEmpty()) {
            return;
        }

        Pestana general = propias.stream().filter(p -> Boolean.TRUE.equals(p.getEsGeneral())).findFirst().orElse(null);
        if (general == null) {
            // No debería pasar (asegurarEsGeneralMarcado ya corrió antes) — sin "General"
            // no hay dónde mover las secciones, así que no se toca nada por ahora.
            return;
        }

        for (Pestana pestana : extra) {
            seccionRepository.findByPestana_IdOrderByOrdenAscIdAsc(pestana.getId())
                    .forEach(seccion -> seccion.setPestana(general));
        }
        pestanaRepository.deleteAll(extra);
    }

    // "Inicio" siempre antes que "General" en la barra de pestañas, sin importar cuál se
    // creó primero: "General" nace en el alta del negocio (orden 0) y "Inicio" recién al
    // elegir una plantilla (un orden mayor, ver asegurarEsHomeMarcado), así que por
    // creación siempre quedaría al revés. Solo intercambia sus "orden" entre sí — no toca
    // el de ninguna otra pestaña que haya quedado oculta (ver visiblePestanas en el
    // frontend), así que nunca puede desordenarlas entre ellas.
    private void asegurarInicioAntesQueGeneral(Negocio negocio) {
        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());
        Pestana inicio = propias.stream().filter(p -> Boolean.TRUE.equals(p.getEsHome())).findFirst().orElse(null);
        Pestana general = propias.stream().filter(p -> Boolean.TRUE.equals(p.getEsGeneral())).findFirst().orElse(null);

        if (inicio != null && general != null && inicio.getOrden() >= general.getOrden()) {
            int ordenGeneral = general.getOrden();
            general.setOrden(inicio.getOrden());
            inicio.setOrden(ordenGeneral);
        }
    }

    // Toda pestaña llamada literalmente "General" (a mano o por esta misma autocuración en
    // el pasado) queda marcada aunque la creación no lo haya hecho todavía; si el negocio
    // ya eligió una plantilla y no tiene ninguna marcada ni ninguna llamada así, se crea de
    // cero — nunca queda sin su catálogo general.
    private void asegurarEsGeneralMarcado(Negocio negocio) {
        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());

        List<Pestana> marcadas = propias.stream().filter(p -> Boolean.TRUE.equals(p.getEsGeneral())).toList();
        if (marcadas.size() > 1) {
            // Quedó más de una por una carrera entre dos requests concurrentes de antes de
            // este bloqueo (ver asegurarSinHuerfanas) — se deja solo la más antigua marcada;
            // las demás no se borran, solo dejan de listarse en la barra.
            desmarcarTodasMenosLaPrimera(marcadas, Pestana::setEsGeneral);
            return;
        }
        if (marcadas.size() == 1) {
            return;
        }

        Optional<Pestana> porNombre = propias.stream()
                .filter(p -> "general".equalsIgnoreCase(p.getNombre() == null ? "" : p.getNombre().trim()))
                .findFirst();
        if (porNombre.isPresent()) {
            porNombre.get().setEsGeneral(true);
            return;
        }

        if (tienePlantillaElegida(negocio)) {
            pestanaRepository.save(Pestana.builder()
                    .negocio(negocio)
                    .nombre(NOMBRE_GENERAL)
                    .orden(propias.size())
                    .esGeneral(true)
                    .build());
        }
    }

    // Todo negocio con una plantilla elegida necesita una pestaña "Inicio" (ahí vive la
    // estructura definida de esa plantilla, o el aviso de "por definir" si todavía no la
    // tiene — ver CatalogTemplateRenderer.vue). Si ya tiene una marcada esHome, no toca
    // nada (nunca pisa una elección ya hecha); si no, crea "Inicio" de cero — nunca reusa
    // una pestaña existente con otro nombre, para no renombrarle algo que el negocio armó
    // a mano.
    private void asegurarEsHomeMarcado(Negocio negocio) {
        if (!tienePlantillaElegida(negocio)) {
            return;
        }

        List<Pestana> propias = pestanaRepository.findByNegocio_IdOrderByOrdenAscIdAsc(negocio.getId());

        List<Pestana> marcadas = propias.stream().filter(p -> Boolean.TRUE.equals(p.getEsHome())).toList();
        if (marcadas.size() > 1) {
            // Misma carrera que en asegurarEsGeneralMarcado: se deja solo la más antigua
            // marcada como "Inicio", las demás no se borran, solo dejan de listarse.
            desmarcarTodasMenosLaPrimera(marcadas, Pestana::setEsHome);
            return;
        }
        if (marcadas.size() == 1) {
            return;
        }

        pestanaRepository.save(Pestana.builder()
                .negocio(negocio)
                .nombre(NOMBRE_INICIO)
                .orden(propias.size())
                .esHome(true)
                .build());
    }

    private void desmarcarTodasMenosLaPrimera(List<Pestana> marcadas, java.util.function.BiConsumer<Pestana, Boolean> setter) {
        for (int i = 1; i < marcadas.size(); i++) {
            setter.accept(marcadas.get(i), false);
        }
    }

    private boolean tienePlantillaElegida(Negocio negocio) {
        return negocio.getApariencia() != null && negocio.getApariencia().getPlantilla() != null;
    }
}
