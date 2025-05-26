package com.ai.JobRecommendationSystem.controller;


import com.ai.JobRecommendationSystem.dto.UserRequestDto;
import com.ai.JobRecommendationSystem.dto.UserResponseDto;
import com.ai.JobRecommendationSystem.entity.User;
import com.ai.JobRecommendationSystem.service.UserService;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/userDetails")
public class UserController {

    @Autowired
    private UserService userService;


    //method to emit events for distributed microservices architecture whereas ApplicationEvents is for monolithic architecture
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    //    @Value("${okta.oauth2.issuer}")
//    private String issuer;
//
//    @Value("${okta.oauth2.client-id}")
//    private String clientId;
//
//    @Value("${okta.oauth2.client-secret}")
//    private String clientSecret;

//    @GetMapping("/secure")
//    public String secureEndpoint(@AuthenticationPrincipal Jwt jwt){
//        String email = jwt.getClaimAsString("email");
//        return email;
//    }

    @PostMapping(value = "/addUser",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> addUser(@RequestPart("file") MultipartFile file, @RequestPart("data") String data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserRequestDto userRequestDto = objectMapper.readValue(data, UserRequestDto.class);

        User jrsRegistration = userService.addUser(userRequestDto,file);
//        kafkaTemplate.send("jrs-info",new RegistrationEvents(jrsRegistration.getUserID(),jrsRegistration.getEmail()));
        return new ResponseEntity<>(jrsRegistration,HttpStatus.CREATED);
    }

    @GetMapping("/getUsers")
    public List<UserResponseDto> getUsers(@RequestParam String email){
        return userService.getUsers(email);

    }

    @PostMapping(value = "/extractSkills",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file) throws IOException{
        DetectDocumentTextResult result = userService.analyzeDocument(file);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

}
