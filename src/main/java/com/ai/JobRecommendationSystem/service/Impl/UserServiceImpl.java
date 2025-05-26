package com.ai.JobRecommendationSystem.service.Impl;


//import com.ai.JobRecommendationSystem.service.SyncProducerEvent;
import com.ai.JobRecommendationSystem.service.JRSConsumerService;
import com.ai.JobRecommendationSystem.service.JRSProducerService;
import com.ai.JobRecommendationSystem.dto.UserRequestDto;
import com.ai.JobRecommendationSystem.dto.UserResponseDto;
import com.ai.JobRecommendationSystem.entity.User;
import com.ai.JobRecommendationSystem.repository.JRS_RolesRepository;
import com.ai.JobRecommendationSystem.repository.JRS_UserStatusRepository;
import com.ai.JobRecommendationSystem.service.JRSConnectorService;
import com.ai.JobRecommendationSystem.service.JRSStreamService;
import com.ai.JobRecommendationSystem.service.UserService;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

@Service
@Configuration
public class UserServiceImpl implements UserService {

    private final AmazonTextract textractClient;

    @Autowired
    private JRS_RolesRepository jrsRolesRepository;

    @Autowired
    private JRS_UserStatusRepository jrsUserStatusRepository;

    @Autowired
    private JRSProducerService JRSProducerService;

    @Autowired
    private JRSConsumerService JRSConsumerService;


    @Autowired
    private JRSConnectorService JRSConnectorService;

    @Autowired
    private JRSStreamService kafkaStreamingService;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.query-topic}")
    private String queryTopic;

    @Autowired
    public UserServiceImpl(AmazonTextract textractClient) {
        this.textractClient = textractClient;
    }

    @Override
    public User addUser(UserRequestDto userRequestDto, MultipartFile file) throws IOException{
        User jrsRegistration = convertDtoToEntity(userRequestDto,file);

        JRSProducerService.sendSync(jrsRegistration);
//        syncProducerEvent.close();
//
        //consume kafka events using kafka connect
        JRSConnectorService.createPostgresSinkConnector();
////
////        RegistrationRequestDto requestDto = syncConsumerEvent.consume(registrationRequestDto);
////        registrationPublisher.publishRegistrationEvent(registrationRequestDto, RegistrationStatus.SUCCESSFULLY_REGISTERED);
//        jrsRegistrationRepository.save(jrsRegistration);

                return jrsRegistration;
    }

    @Override
    public List<UserResponseDto> getUsers(String email){

        JRSProducerService.sendQuery("select * from user_registration usr where usr.email='"+email+"' ;");
         return JRSConsumerService.consumeTopics();


    }

    private User convertDtoToEntity(UserRequestDto userRequestDto, MultipartFile file) throws IOException {
        User jrsRegistration = new User();
        jrsRegistration.setUserName(userRequestDto.getUserName());
        jrsRegistration.setEmail(userRequestDto.getEmail());
        jrsRegistration.setRole(userRequestDto.getRole());
        jrsRegistration.setUserStatus(userRequestDto.getUserStatus());
        jrsRegistration.setAddress(userRequestDto.getAddress());
        jrsRegistration.setContactNumber(userRequestDto.getContactNumber());
//        jrsRegistration.setResumeDetails(file.getBytes());
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



