package com.jitplus.loyalty.controller;

import com.jitplus.loyalty.dto.RedemptionRequest;
import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty/redemptions")
public class RedemptionController {

    @Autowired
    private LoyaltyService service;

    @PostMapping
    public LoyaltyCard redeemReward(@RequestBody RedemptionRequest request) {
        return service.redeemReward(request);
    }
}
