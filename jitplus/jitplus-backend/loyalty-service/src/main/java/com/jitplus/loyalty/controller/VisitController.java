package com.jitplus.loyalty.controller;

import com.jitplus.loyalty.dto.VisitRequest;
import com.jitplus.loyalty.dto.VisitResponse;
import com.jitplus.loyalty.service.LoyaltyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty/visits")
public class VisitController {

    private final LoyaltyService service;

    public VisitController(LoyaltyService service) {
        this.service = service;
    }

    @PostMapping
    public VisitResponse recordVisit(@Valid @RequestBody VisitRequest request) {
        return service.recordVisit(request);
    }
}
