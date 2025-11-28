package com.jitplus.customer.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jitplus.customer.model.Customer;
import com.jitplus.customer.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer registerCustomer(Customer customer) {
        Optional<Customer> existing = repository.findByPhoneNumber(customer.getPhoneNumber());
        if (existing.isPresent()) {
            return existing.get();
        }
        return repository.save(customer);
    }

    public Optional<Customer> getCustomerByPhone(String phone) {
        return repository.findByPhoneNumber(phone);
    }
    
    public Optional<Customer> getCustomerByQrToken(String qrToken) {
        return repository.findByQrToken(qrToken);
    }
    
    public Optional<Customer> getCustomerById(Long id) {
        return repository.findById(id);
    }
}
