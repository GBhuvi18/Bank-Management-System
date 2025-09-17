package com.example.bankmanagement.control;

import org.springframework.web.bind.annotation.*;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.TransactionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")   // allow all for now
public class TransactionController {
    private final TransactionRepository repo;
    private final AccountRepository accountRepository;

    public TransactionController(TransactionRepository repo, AccountRepository accountRepository) {
        this.repo = repo;
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public List<Transaction> byAccount(@RequestParam Long accountId){
        Account acc = accountRepository.findById(accountId).orElseThrow();
        return repo.findByAccount(acc);
    }
}
