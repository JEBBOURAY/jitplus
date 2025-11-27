package com.jitplus.loyalty.controller;

import com.jitplus.loyalty.dto.RedemptionRequest;
import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.service.LoyaltyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty/redemptions")
public class RedemptionController {

    private final LoyaltyService service;

    public RedemptionController(LoyaltyService service) {
        this.service = service;
    }

    @PostMapping
    public LoyaltyCard redeemReward(@Valid @RequestBody RedemptionRequest request) {
        return service.redeemReward(request);
    }
}
