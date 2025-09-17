package com.example.bankmanagement.control;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.repository.CustomerRepository;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")   // allow all for now
public class CustomerController {
    private final CustomerRepository repo;
    public CustomerController(CustomerRepository repo) { this.repo = repo; }

    @GetMapping public List<Customer> all() {
    	return repo.findAll(); 
    	}
    @PostMapping public Customer create(@Valid @RequestBody Customer c) { 
    	return repo.save(c); 
    	}
    @GetMapping("/{id}") public Customer one(@PathVariable Long id){
    	return repo.findById(id).orElseThrow();
    	}
    @PutMapping("/{id}") public Customer update(@PathVariable Long id, @Valid @RequestBody Customer c){
        Customer existing = repo.findById(id).orElseThrow();
        existing.setName(c.getName()); existing.setEmail(c.getEmail()); existing.setPhone(c.getPhone());
        return repo.save(existing);
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id){
    	repo.deleteById(id);
    	}
}

