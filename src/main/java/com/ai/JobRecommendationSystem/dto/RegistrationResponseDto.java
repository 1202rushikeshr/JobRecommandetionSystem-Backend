package com.ai.JobRecommendationSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponseDto {
    private String userId;
    private String userName;
    private String email;
    private String role;
    private String userStatus;
    private String address;
    private String contactNumber;
    private String fileFormat;
    private byte[] resumeDetails;
    private String resumeTitle;
}
