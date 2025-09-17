package com.example.bankmanagement.control;


import org.springframework.web.bind.annotation.*;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.CustomerRepository;
import com.example.bankmanagement.service.BankingService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")   // allow all for now
public class AccountController {
    private final AccountRepository accountRepo;
    private final CustomerRepository customerRepo;
    private final BankingService bankingService;

    public AccountController(AccountRepository accountRepo, CustomerRepository customerRepo, BankingService bankingService) {
        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
        this.bankingService = bankingService;
    }

    @GetMapping public List<Account> all(){ return accountRepo.findAll(); }

    @PostMapping public Account create(@RequestBody Map<String, String> payload){
        Long customerId = Long.parseLong(payload.get("customerId"));
        String accountNumber = payload.get("accountNumber");
        String type = payload.getOrDefault("type", "SAVINGS");

        Customer customer = customerRepo.findById(customerId).orElseThrow();
        Account acc = new Account();
        acc.setCustomer(customer);
        acc.setAccountNumber(accountNumber);
        acc.setType(type);
        acc.setBalance(new BigDecimal(payload.getOrDefault("initialDeposit", "0")));
        return accountRepo.save(acc);
    }

    @PostMapping("/{id}/deposit")
    public Account deposit(@PathVariable Long id, @RequestBody Map<String, String> payload){
        BigDecimal amount = new BigDecimal(payload.get("amount"));
        String desc = payload.getOrDefault("description", "Deposit");
        return bankingService.deposit(id, amount, desc);
    }

    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @RequestBody Map<String, String> payload){
        BigDecimal amount = new BigDecimal(payload.get("amount"));
        String desc = payload.getOrDefault("description", "Withdrawal");
        return bankingService.withdraw(id, amount, desc);
    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody Map<String, String> payload){
        Long fromId = Long.parseLong(payload.get("fromAccountId"));
        Long toId = Long.parseLong(payload.get("toAccountId"));
        java.math.BigDecimal amount = new java.math.BigDecimal(payload.get("amount"));
        String desc = payload.getOrDefault("description", "Transfer");
        bankingService.transfer(fromId, toId, amount, desc);
        return "OK";
    }
}

