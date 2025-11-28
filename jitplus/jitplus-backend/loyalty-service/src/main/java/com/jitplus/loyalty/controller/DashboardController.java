package com.jitplus.loyalty.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jitplus.loyalty.dto.DashboardStats;
import com.jitplus.loyalty.service.DashboardService;

@RestController
@RequestMapping("/loyalty/dashboard")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping("/{merchantId}/stats")
    public DashboardStats getStats(@PathVariable String merchantId) {
        return dashboardService.getMerchantStats(merchantId);
    }
}
