package com.ai.JobRecommendationSystem.repository;

import com.ai.JobRecommendationSystem.entity.JRS_UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JRS_UserStatusRepository extends JpaRepository<JRS_UserStatus,Long> {
    Optional<JRS_UserStatus> findByUserStatus(String userStatus);
}
