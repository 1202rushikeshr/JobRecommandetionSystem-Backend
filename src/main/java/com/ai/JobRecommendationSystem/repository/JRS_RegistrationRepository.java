package com.ai.JobRecommendationSystem.repository;

import com.ai.JobRecommendationSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JRS_RegistrationRepository extends JpaRepository<User,Integer> {

//    @Query("SELECT * FROM user_Registration usr WHERE usr.email = :email")
//    Optional<JRS_Registration> findByEmail(@Param("email") String email);
}
