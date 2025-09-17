package com.example.bankmanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Transaction;

import java.util.List;
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount(Account account);
}

