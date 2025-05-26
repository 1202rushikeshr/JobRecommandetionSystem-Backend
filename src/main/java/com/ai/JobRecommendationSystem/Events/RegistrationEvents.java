package com.ai.JobRecommendationSystem.Events;

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
