package com.jitplus.loyalty.controller;

import com.jitplus.loyalty.model.LoyaltyProgram;
import com.jitplus.loyalty.service.LoyaltyProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty/programs")
public class LoyaltyProgramController {

    @Autowired
    private LoyaltyProgramService service;

    @PostMapping
    public LoyaltyProgram createProgram(@RequestBody LoyaltyProgram program) {
        return service.createOrUpdateProgram(program);
    }

    @GetMapping("/{merchantId}")
    public LoyaltyProgram getProgram(@PathVariable String merchantId) {
        return service.getProgramByMerchantId(merchantId).orElse(null);
    }
}
