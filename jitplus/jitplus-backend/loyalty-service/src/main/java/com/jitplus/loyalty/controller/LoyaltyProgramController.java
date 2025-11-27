package com.jitplus.loyalty.controller;

import com.jitplus.loyalty.model.LoyaltyProgram;
import com.jitplus.loyalty.service.LoyaltyProgramService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty/programs")
public class LoyaltyProgramController {

    private final LoyaltyProgramService service;

    public LoyaltyProgramController(LoyaltyProgramService service) {
        this.service = service;
    }

    @PostMapping
    public LoyaltyProgram createProgram(@Valid @RequestBody LoyaltyProgram program) {
        return service.createOrUpdateProgram(program);
    }

    @GetMapping("/{merchantId}")
    public LoyaltyProgram getProgram(@PathVariable String merchantId) {
        return service.getProgramByMerchantId(merchantId).orElse(null);
    }
}
