package com.Senior.Golves_Project.repository;

import com.Senior.Golves_Project.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository
        extends JpaRepository<Feedback, Long> {
}