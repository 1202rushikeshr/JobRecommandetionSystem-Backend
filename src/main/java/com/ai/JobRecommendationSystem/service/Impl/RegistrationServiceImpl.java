package com.ai.JobRecommendationSystem.service.Impl;


//import com.ai.JobRecommendationSystem.Events.SyncProducerEvent;
import com.ai.JobRecommendationSystem.Events.RegistrationEvents;
import com.ai.JobRecommendationSystem.Events.SyncProducerEvent;
import com.ai.JobRecommendationSystem.dto.RegistrationRequestDto;
import com.ai.JobRecommendationSystem.dto.RegistrationResponseDto;
import com.ai.JobRecommendationSystem.entity.JRS_Registration;
import com.ai.JobRecommendationSystem.repository.JRS_RegistrationRepository;
import com.ai.JobRecommendationSystem.repository.JRS_RolesRepository;
import com.ai.JobRecommendationSystem.repository.JRS_UserStatusRepository;
import com.ai.JobRecommendationSystem.service.KafkaConnectorService;
import com.ai.JobRecommendationSystem.service.KafkaStreamingService;
import com.ai.JobRecommendationSystem.service.RegistrationService;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

@Service
@Configuration
public class RegistrationServiceImpl implements RegistrationService {

    private final AmazonTextract textractClient;

    @Autowired
    private JRS_RolesRepository jrsRolesRepository;

    @Autowired
    private JRS_UserStatusRepository jrsUserStatusRepository;

    @Autowired
    private SyncProducerEvent syncProducerEvent;


    @Autowired
    private KafkaConnectorService kafkaConnectorService;

    @Autowired
    private KafkaStreamingService kafkaStreamingService;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.query-topic}")
    private String queryTopic;

    @Autowired
    public RegistrationServiceImpl(AmazonTextract textractClient) {
        this.textractClient = textractClient;
    }

    @Override
    public JRS_Registration addUser(RegistrationRequestDto registrationRequestDto, MultipartFile file) throws IOException{
        JRS_Registration jrsRegistration = convertDtoToEntity(registrationRequestDto,file);
        //produce kafka events Synchronously
        new SyncProducerEvent(bootstrapServers,topic);
        syncProducerEvent.sendSync(jrsRegistration);
        syncProducerEvent.close();
//
        //consume kafka events using kafka connect
        kafkaConnectorService.createPostgresSinkConnector();
////
////        RegistrationRequestDto requestDto = syncConsumerEvent.consume(registrationRequestDto);
////        registrationPublisher.publishRegistrationEvent(registrationRequestDto, RegistrationStatus.SUCCESSFULLY_REGISTERED);
//        jrsRegistrationRepository.save(jrsRegistration);

                return jrsRegistration;
    }

    @Override
    public Optional<RegistrationResponseDto> getUsers(String email){
        new SyncProducerEvent(bootstrapServers,topic);
        syncProducerEvent.sendQuery("select * from user_registration usr where usr.email='"+email+"' ;");
        syncProducerEvent.close();

        kafkaStreamingService.createKafkaStream(bootstrapServers,queryTopic);
         return null;


    }

    private JRS_Registration convertDtoToEntity(RegistrationRequestDto registrationRequestDto, MultipartFile file) throws IOException {
        JRS_Registration jrsRegistration = new JRS_Registration();
        jrsRegistration.setUserName(registrationRequestDto.getUserName());
        jrsRegistration.setEmail(registrationRequestDto.getEmail());
        jrsRegistration.setRole(registrationRequestDto.getRole());
        jrsRegistration.setUserStatus(registrationRequestDto.getUserStatus());
        jrsRegistration.setAddress(registrationRequestDto.getAddress());
        jrsRegistration.setContactNumber(registrationRequestDto.getContactNumber());
        jrsRegistration.setResumeDetails(file.getBytes());
        jrsRegistration.setResumeTitle(file.getOriginalFilename());
        String fileName = file.getOriginalFilename();
        String fileFormat = "";
        if (fileName != null && fileName.contains(".")) {
            fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        jrsRegistration.setFileFormat(fileFormat);
        return jrsRegistration;

    }

    //convert Multipart file to AWS Byte Buffer
    public ByteBuffer convertToByteBuffer(MultipartFile file) throws IOException {
        return ByteBuffer.wrap(file.getBytes());
    }

    public DetectDocumentTextResult analyzeDocument(MultipartFile file) throws IOException{
        ByteBuffer imageBytes = convertToByteBuffer(file);
        Document document = new Document().withBytes(imageBytes);
        DetectDocumentTextRequest request = new DetectDocumentTextRequest().withDocument(document);
        return textractClient.detectDocumentText(request);
    }
}



