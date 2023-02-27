package com.homework.repository.scrap;

import com.homework.domain.scrap.Tax;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Tax, Long> {
    Optional<Tax> findTopByUserId(String userId);
}
