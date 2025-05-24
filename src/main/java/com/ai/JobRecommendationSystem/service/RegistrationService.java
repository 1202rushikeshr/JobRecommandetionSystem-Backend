package com.ai.JobRecommendationSystem.service;

import com.ai.JobRecommendationSystem.dto.RegistrationRequestDto;
import com.ai.JobRecommendationSystem.dto.RegistrationResponseDto;
import com.ai.JobRecommendationSystem.entity.JRS_Registration;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface RegistrationService {
    JRS_Registration addUser(RegistrationRequestDto registrationRequestDto, MultipartFile file) throws IOException;

    Optional<RegistrationResponseDto> getUsers(String email);

    DetectDocumentTextResult analyzeDocument(MultipartFile file) throws IOException;

}
