//package com.ai.JobRecommendationSystem.service.Impl;
//
//
//import com.ai.JobRecommendationSystem.Events.RegistrationEvents;
//import com.ai.JobRecommendationSystem.Events.RegistrationStatus;
//import com.ai.JobRecommendationSystem.dto.RegistrationRequestDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Sinks;
//
//@Service
//public class RegistrationPublisher {
//
//    //WebFlux+Kafka sinks are used for programmatic, ondemand publishing of messages into reactive stream. This dynamically push messages(HTTP requests/events) to kafka asynchronously sinks emit messages to Flux without needing the consumer to pull them
//    @Autowired
//    private Sinks.Many<RegistrationEvents> registrationSink;
//
//    public void publishRegistrationEvent(RegistrationRequestDto registrationRequestDto, RegistrationStatus registrationStatus){
//        RegistrationEvents registrationEvents = new RegistrationEvents(registrationRequestDto,registrationStatus);
//
//        registrationSink.tryEmitNext(registrationEvents);
//
//    }
//}
