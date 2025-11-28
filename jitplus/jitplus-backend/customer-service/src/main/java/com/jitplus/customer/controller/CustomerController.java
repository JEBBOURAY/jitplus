package com.jitplus.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jitplus.customer.model.Customer;
import com.jitplus.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public Customer registerCustomer(@Valid @RequestBody Customer customer) {
        return service.registerCustomer(customer);
    }

    @GetMapping("/by-phone")
    public ResponseEntity<Customer> getCustomerByPhone(@RequestParam String phone) {
        return service.getCustomerByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/qr-token")
    public ResponseEntity<QrTokenResponse> getQrToken(@PathVariable Long id) {
        return service.getCustomerById(id)
            .map(customer -> ResponseEntity.ok(new QrTokenResponse(customer.getQrToken())))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-qr-token")
    public ResponseEntity<Customer> getCustomerByQrToken(@RequestParam String token) {
        return service.getCustomerByQrToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // DTO for QR Token Response
    public record QrTokenResponse(String qrToken) {}
}
