package com.ai.JobRecommendationSystem.Events;

import com.ai.JobRecommendationSystem.dto.RegistrationRequestDto;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.UUID;

public class RegistrationEvents{
    private final int userId;
    private final String email;

    public RegistrationEvents(int userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public int getUserId(){
        return userId;
    }

    public String getEmail(){
        return email;
    }
}
