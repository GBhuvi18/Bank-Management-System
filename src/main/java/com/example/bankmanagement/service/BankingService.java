package com.example.bankmanagement.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.TransactionRepository;

import java.math.BigDecimal;

@Service
public class BankingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankingService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount, String desc) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        acc.setBalance(acc.getBalance().add(amount));
        accountRepository.save(acc);

        Transaction t = new Transaction();
        t.setAccount(acc);
        t.setType("DEPOSIT");
        t.setAmount(amount);
        t.setDescription(desc);
        transactionRepository.save(t);

        return acc;
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount, String desc) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (acc.getBalance().compareTo(amount) < 0) throw new RuntimeException("Insufficient balance");
        acc.setBalance(acc.getBalance().subtract(amount));
        accountRepository.save(acc);

        Transaction t = new Transaction();
        t.setAccount(acc);
        t.setType("WITHDRAWAL");
        t.setAmount(amount);
        t.setDescription(desc);
        transactionRepository.save(t);

        return acc;
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount, String desc) {
        if (fromId.equals(toId)) throw new RuntimeException("Cannot transfer to the same account");
        Account from = accountRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account to = accountRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("To account not found"));

        if (from.getBalance().compareTo(amount) < 0) throw new RuntimeException("Insufficient balance");

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tOut = new Transaction();
        tOut.setAccount(from);
        tOut.setType("TRANSFER_OUT");
        tOut.setAmount(amount);
        tOut.setDescription(desc);
        transactionRepository.save(tOut);

        Transaction tIn = new Transaction();
        tIn.setAccount(to);
        tIn.setType("TRANSFER_IN");
        tIn.setAmount(amount);
        tIn.setDescription(desc);
        transactionRepository.save(tIn);
    }
}
