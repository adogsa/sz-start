package com.homework.repository;

import com.homework.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(String userId);
    Optional<Account> findByName(String name);
    boolean existsByUserId(String userId);
}
