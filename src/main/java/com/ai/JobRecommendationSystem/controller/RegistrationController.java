package com.ai.JobRecommendationSystem.controller;


import com.ai.JobRecommendationSystem.Events.RegistrationEvents;
import com.ai.JobRecommendationSystem.dto.RegistrationRequestDto;
import com.ai.JobRecommendationSystem.dto.RegistrationResponseDto;
import com.ai.JobRecommendationSystem.entity.JRS_Registration;
import com.ai.JobRecommendationSystem.service.RegistrationService;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;


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
    public ResponseEntity<JRS_Registration> addUser(@RequestPart("file") MultipartFile file,  @RequestPart("data") String data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RegistrationRequestDto registrationRequestDto = objectMapper.readValue(data, RegistrationRequestDto.class);

        JRS_Registration jrsRegistration = registrationService.addUser(registrationRequestDto,file);
//        kafkaTemplate.send("jrs-info",new RegistrationEvents(jrsRegistration.getUserID(),jrsRegistration.getEmail()));
        return new ResponseEntity<>(jrsRegistration,HttpStatus.CREATED);
    }

    @GetMapping("/getUsers")
    public Optional<RegistrationResponseDto> getUsers(@RequestParam String email){
        return registrationService.getUsers(email);

    }

    @PostMapping(value = "/extractSkills",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file) throws IOException{
        DetectDocumentTextResult result = registrationService.analyzeDocument(file);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

}
