package com.ai.JobRecommendationSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="User_Status")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JRS_UserStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userStatusId;
    private String userStatus;
}