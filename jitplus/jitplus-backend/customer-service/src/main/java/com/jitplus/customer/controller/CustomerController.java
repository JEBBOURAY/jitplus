package com.jitplus.customer.controller;

import com.jitplus.customer.model.Customer;
import com.jitplus.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping
    public Customer registerCustomer(@RequestBody Customer customer) {
        return service.registerCustomer(customer);
    }

    @GetMapping("/by-phone")
    public Customer getCustomerByPhone(@RequestParam String phone) {
        return service.getCustomerByPhone(phone).orElse(null);
    }
}
