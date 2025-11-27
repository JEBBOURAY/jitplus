package com.jitplus.customer.controller;

import com.jitplus.customer.model.Customer;
import com.jitplus.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    public Customer getCustomerByPhone(@RequestParam String phone) {
        return service.getCustomerByPhone(phone).orElse(null);
    }
}
