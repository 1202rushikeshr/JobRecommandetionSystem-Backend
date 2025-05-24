package com.ai.JobRecommendationSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestDto {
    private String userName;
    private String email;
    private String role;
    private String userStatus;
    private String address;
    private String contactNumber;
}

