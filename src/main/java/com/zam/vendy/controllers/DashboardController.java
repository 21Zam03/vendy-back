package com.zam.vendy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zam.vendy.dtos.dashboard.DashboardResponse;
import com.zam.vendy.security.userdetails.UserDetailsImpl;
import com.zam.vendy.services.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> obtener(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(dashboardService.obtener(principal.getIdUsuario()));
    }
}
