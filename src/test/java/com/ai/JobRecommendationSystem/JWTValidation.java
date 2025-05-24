//package com.ai.JobRecommendationSystem;
//
//
//import com.ai.JobRecommendationSystem.controller.RegistrationController;
//import com.ai.JobRecommendationSystem.service.RegistrationService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.*;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import java.time.Instant;
//import java.util.Map;
//import org.springframework.security.oauth2.jwt.Jwt;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//@ExtendWith(MockitoExtension.class)
//public class JWTValidation {
//
//
//    @InjectMocks
//    private RegistrationController registrationController;
//
//    @Mock
//    private RegistrationService registrationService;
//
//    @Test
//    void testSecureEndpoint() throws Exception {
//
//        Jwt jwt = new Jwt("fake-token",
//                Instant.now(),
//                        Instant.now().plusSeconds(3600),
//                Map.of("alg","none"),
//                Map.of("email","user@example.com","sub","user"));
//        assertTrue(registrationController.secureEndpoint(jwt));
//
//    }
//}
