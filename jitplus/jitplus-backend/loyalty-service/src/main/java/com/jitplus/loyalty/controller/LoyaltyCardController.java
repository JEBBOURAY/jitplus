package com.jitplus.loyalty.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jitplus.loyalty.model.LoyaltyCard;
import com.jitplus.loyalty.service.LoyaltyCardService;

@RestController
@RequestMapping("/loyalty/cards")
public class LoyaltyCardController {

    private final LoyaltyCardService cardService;

    public LoyaltyCardController(LoyaltyCardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public LoyaltyCard createCard(@RequestParam String merchantId, 
                                @RequestParam Long customerId,
                                @RequestParam(required = false) String customerName,
                                @RequestParam(required = false) String customerPhone) {
        return cardService.createCardIfNotExists(merchantId, customerId, customerName, customerPhone);
    }

    @GetMapping
    public LoyaltyCard getCard(@RequestParam String merchantId, @RequestParam Long customerId) {
        return cardService.getCard(merchantId, customerId).orElse(null);
    }
}
