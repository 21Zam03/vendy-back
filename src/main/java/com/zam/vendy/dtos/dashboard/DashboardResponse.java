package com.zam.vendy.dtos.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private long catalogVisits;
    private long whatsappInquiries;
    private long publishedProducts;
    private List<VisitaPorDiaResponse> visitsByDay;
    private List<ConsultaRecienteResponse> recentInquiries;
}
