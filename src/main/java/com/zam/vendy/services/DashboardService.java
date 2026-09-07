package com.zam.vendy.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zam.vendy.dtos.dashboard.ConsultaRecienteResponse;
import com.zam.vendy.dtos.dashboard.DashboardResponse;
import com.zam.vendy.dtos.dashboard.VisitaPorDiaResponse;
import com.zam.vendy.entities.ConsultaWhatsapp;
import com.zam.vendy.entities.Negocio;
import com.zam.vendy.entities.VisitaCatalogo;
import com.zam.vendy.entities.enums.Plan;
import com.zam.vendy.repositories.ConsultaWhatsappRepository;
import com.zam.vendy.repositories.ProductoRepository;
import com.zam.vendy.repositories.VisitaCatalogoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int DIAS_HISTORIAL = 7;

    private final NegocioService negocioService;
    private final SuscripcionService suscripcionService;
    private final VisitaCatalogoRepository visitaCatalogoRepository;
    private final ConsultaWhatsappRepository consultaWhatsappRepository;
    private final ProductoRepository productoRepository;

    // Sin readOnly: requiereNivel puede autocurar una Suscripcion faltante (ver
    // SuscripcionService), que necesita poder escribir.
    @Transactional
    public DashboardResponse obtener(Integer idUsuario) {
        Negocio negocio = negocioService.obtenerPorUsuario(idUsuario);
        suscripcionService.requiereNivel(negocio, Plan.GO.getNivel());
        Long negocioId = negocio.getId();

        long catalogVisits = visitaCatalogoRepository.sumCantidadByNegocioId(negocioId);
        long whatsappInquiries = consultaWhatsappRepository.countByNegocio_Id(negocioId);
        long publishedProducts = productoRepository.countByNegocio_IdAndActivoTrue(negocioId);

        List<VisitaPorDiaResponse> visitsByDay = construirVisitasPorDia(negocioId);

        List<ConsultaRecienteResponse> recentInquiries = consultaWhatsappRepository
                .findTop10ByNegocio_IdOrderByCreadoEnDesc(negocioId).stream()
                .map(this::mapearConsulta)
                .toList();

        return DashboardResponse.builder()
                .catalogVisits(catalogVisits)
                .whatsappInquiries(whatsappInquiries)
                .publishedProducts(publishedProducts)
                .visitsByDay(visitsByDay)
                .recentInquiries(recentInquiries)
                .build();
    }

    private List<VisitaPorDiaResponse> construirVisitasPorDia(Long negocioId) {
        LocalDate hoy = LocalDate.now();
        LocalDate desde = hoy.minusDays(DIAS_HISTORIAL - 1L);

        Map<LocalDate, Integer> cantidadPorFecha = visitaCatalogoRepository
                .findByNegocio_IdAndFechaBetweenOrderByFechaAsc(negocioId, desde, hoy).stream()
                .collect(Collectors.toMap(VisitaCatalogo::getFecha, VisitaCatalogo::getCantidad));

        List<VisitaPorDiaResponse> resultado = new ArrayList<>();
        for (LocalDate fecha = desde; !fecha.isAfter(hoy); fecha = fecha.plusDays(1)) {
            resultado.add(new VisitaPorDiaResponse(fecha, cantidadPorFecha.getOrDefault(fecha, 0)));
        }
        return resultado;
    }

    private ConsultaRecienteResponse mapearConsulta(ConsultaWhatsapp consulta) {
        var producto = consulta.getProducto();
        return new ConsultaRecienteResponse(
                consulta.getId(),
                producto != null ? producto.getId() : null,
                producto != null ? producto.getNombre() : null,
                consulta.getCreadoEn());
    }
}
