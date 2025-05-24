package com.ai.JobRecommendationSystem.repository;

import com.ai.JobRecommendationSystem.dto.RegistrationResponseDto;
import com.ai.JobRecommendationSystem.entity.JRS_Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JRS_RegistrationRepository extends JpaRepository<JRS_Registration,Integer> {

//    @Query("SELECT * FROM user_Registration usr WHERE usr.email = :email")
//    Optional<JRS_Registration> findByEmail(@Param("email") String email);
}
