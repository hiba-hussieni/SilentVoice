package com.Senior.Golves_Project.repository;

import com.Senior.Golves_Project.entity.TranslationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TranslationHistoryRepository extends JpaRepository<TranslationHistory, Long> {

    List<TranslationHistory> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<TranslationHistory> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}