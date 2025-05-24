package com.ai.JobRecommendationSystem.repository;

import com.ai.JobRecommendationSystem.entity.JRS_Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JRS_RolesRepository extends JpaRepository<JRS_Roles,Long> {
    Optional<JRS_Roles> findByRoleName(String roleName);
}
