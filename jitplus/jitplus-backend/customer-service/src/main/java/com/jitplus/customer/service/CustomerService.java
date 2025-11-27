package com.jitplus.customer.service;

import com.jitplus.customer.model.Customer;
import com.jitplus.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
