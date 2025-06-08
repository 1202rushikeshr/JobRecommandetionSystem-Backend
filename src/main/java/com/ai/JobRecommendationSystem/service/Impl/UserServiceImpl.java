package com.ai.JobRecommendationSystem.service.Impl;


//import com.ai.JobRecommendationSystem.service.SyncProducerEvent;
//import com.ai.JobRecommendationSystem.Utils.PdfToImageConvertor;
import com.ai.JobRecommendationSystem.Utils.PdfToImageConvertor;
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
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JRS_RolesRepository jrsRolesRepository;

    @Autowired
    private JRS_UserStatusRepository jrsUserStatusRepository;

    @Autowired
    private JRSProducerService jrsProducerService;

    @Autowired
    private JRSConsumerService JRSConsumerService;

   private final PdfToImageConvertor pdfToImageConvertor;

//    @Autowired
//    private PdfToImageConvertor pdfToImageConvertor;

    private final AmazonTextract textract;

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
    public UserServiceImpl(PdfToImageConvertor pdfToImageConvertor, AmazonTextract textract) {
        this.pdfToImageConvertor = pdfToImageConvertor;
        this.textract = textract;
    }

    @Override
    public User addUser(UserRequestDto userRequestDto, MultipartFile file) throws IOException{
        User jrsRegistration = convertDtoToEntity(userRequestDto,file);

        jrsProducerService.sendSync(jrsRegistration);
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

        jrsProducerService.sendQuery(email);
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

    public String analyzeDocument(MultipartFile file) throws IOException{
        int pageCount = 0;
//        List<String> extractedTexts = new ArrayList<>();
//        try(InputStream inputStream = file.getInputStream();
//        PDDocument pdDocument = PDDocument.load(inputStream)){
//            pageCount = pdDocument.getNumberOfPages();
//            for(PDPage page : pdDocument.getPages()){
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                PDDocument singlePageDocument = new PDDocument();
//                singlePageDocument.save(byteArrayOutputStream);
//                byte[] pageBytes = byteArrayOutputStream.toByteArray();
//                DetectDocumentTextRequest request = new DetectDocumentTextRequest().withDocument(new Document().withBytes(ByteBuffer.wrap(pageBytes)));
//                DetectDocumentTextResult result =  textract.detectDocumentText(request);
//                StringBuilder text = new StringBuilder();
//                List<Block> blocks = result.getBlocks();
//                for(Block block: blocks){
//                if (block.getBlockType().equals("LINE") || block.getBlockType().equals("WORD")) {
//                    text.append(block.getText()).append(" ");
//                }
//            }
//            extractedTexts.add(text.toString());
//            }
//        }


        List<String> extractedTexts = new ArrayList<>();
        List<byte[]> imagearray = pdfToImageConvertor.convertPdfToImage(file);
        for(byte[] imageBytes : imagearray){
            //call textract
            //Detect document text is a synchronous API that supports only PNG or JPG, for PDF it supports only 1 page PDF.
            //Only option to go with multipart file PDF is to use async API StartDocumentTextDetection that picks data from s3 or convert the pdf to image
            DetectDocumentTextRequest request = new DetectDocumentTextRequest().withDocument(new Document().withBytes(ByteBuffer.wrap(imageBytes)));
            DetectDocumentTextResult result =  textract.detectDocumentText(request);
            StringBuilder text = new StringBuilder();
            List<Block> blocks = result.getBlocks();
            for(Block block: blocks){
                if (block.getBlockType().equals("LINE") || block.getBlockType().equals("WORD")) {
//                    text.append(block.getText()).append(" ");
//                    ObjectMapper mapper = new ObjectMapper();
//                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    extractedTexts.add(block.getText());
                }
            }

        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        if (extractedTexts.size() >= 4) {
            json.put("name", extractedTexts.get(0));
            json.put("email", extractedTexts.get(1));
            json.put("phone", extractedTexts.get(2));

            // Skills from index 3 onwards
            List<String> skills = extractedTexts.subList(3, extractedTexts.size());
            json.putPOJO("skills", skills);
        } else {
            json.put("error", "Insufficient resume data");
        }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    }
}



