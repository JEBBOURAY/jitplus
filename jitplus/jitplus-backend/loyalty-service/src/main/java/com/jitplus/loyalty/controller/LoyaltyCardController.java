package com.jitplus.loyalty.controller;

import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.model.LoyaltyProgram;
import com.jitplus.loyalty.service.LoyaltyCardService;
import com.jitplus.loyalty.service.LoyaltyProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loyalty/cards")
public class LoyaltyCardController {

    @Autowired
    private LoyaltyCardService cardService;

    @PostMapping
    public LoyaltyCard createCard(@RequestParam String merchantId, @RequestParam Long customerId) {
        return cardService.createCardIfNotExists(merchantId, customerId);
    }

    @GetMapping
    public LoyaltyCard getCard(@RequestParam String merchantId, @RequestParam Long customerId) {
        return cardService.getCard(merchantId, customerId).orElse(null);
    }
}
