package com.example.bankmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankmanagement.model.Account;

import java.util.Optional;
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
}

