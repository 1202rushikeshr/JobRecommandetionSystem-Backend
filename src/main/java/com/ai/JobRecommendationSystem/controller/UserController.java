package com.ai.JobRecommendationSystem.controller;


import com.ai.JobRecommendationSystem.dto.UserRequestDto;
import com.ai.JobRecommendationSystem.dto.UserResponseDto;
import com.ai.JobRecommendationSystem.entity.User;
import com.ai.JobRecommendationSystem.service.UserService;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userDetails")
public class UserController {

    @Autowired
    private UserService userService;


    //method to emit events for distributed microservices architecture whereas ApplicationEvents is for monolithic architecture
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file){
            Map<String, Object> response = new HashMap<>();
            try {
                if (file.isEmpty() || file.getContentType() == null || !file.getContentType().equals("application/pdf")) {
                    return ResponseEntity.badRequest().body("Please upload a valid PDF file.");
                }
                String result = userService.analyzeDocument(file);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(result);
//                response.put("extractedText", node);
                return ResponseEntity.ok(node);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file: " + e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during text extraction: " + e.getMessage());
            }
    }

}
