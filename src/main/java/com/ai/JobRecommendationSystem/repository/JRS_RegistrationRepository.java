package com.ai.JobRecommendationSystem.repository;

import com.ai.JobRecommendationSystem.entity.JRS_UserStatus;
import com.ai.JobRecommendationSystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JRS_RegistrationRepository extends JpaRepository<User,Integer> {

    @Query("SELECT usr FROM User usr WHERE usr.email = :email")
    Optional<List<User>> findByEmail(@Param("email") String email);
}
