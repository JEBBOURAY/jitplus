package com.jitplus.loyalty.controller;

import com.jitplus.loyalty.dto.VisitRequest;
import com.jitplus.loyalty.dto.VisitResponse;
import com.jitplus.loyalty.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty/visits")
public class VisitController {

    @Autowired
    private LoyaltyService service;

    @PostMapping
    public VisitResponse recordVisit(@RequestBody VisitRequest request) {
        return service.recordVisit(request);
    }
}
