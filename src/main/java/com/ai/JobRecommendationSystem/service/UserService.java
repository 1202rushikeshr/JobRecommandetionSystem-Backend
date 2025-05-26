package com.ai.JobRecommendationSystem.service;

import com.ai.JobRecommendationSystem.dto.UserRequestDto;
import com.ai.JobRecommendationSystem.dto.UserResponseDto;
import com.ai.JobRecommendationSystem.entity.User;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User addUser(UserRequestDto userRequestDto, MultipartFile file) throws IOException;

    List<UserResponseDto> getUsers(String email);

    DetectDocumentTextResult analyzeDocument(MultipartFile file) throws IOException;

}
