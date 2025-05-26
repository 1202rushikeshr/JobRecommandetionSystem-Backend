package com.ai.JobRecommendationSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_REGISTRATION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {


    private String userName;
    @Id
    @Column(nullable = false, unique = true)
    private String email;
    private String role;
    private String userStatus;
    private String address;
    private String contactNumber;
//    @Lob
//    @Column(name="resumeDetails")
//    private byte[] resumeDetails;

    private String resumeTitle;
    private String fileFormat;

}