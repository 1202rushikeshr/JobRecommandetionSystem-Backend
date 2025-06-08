package com.ai.JobRecommendationSystem.Utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Component
public class PdfToImageConvertor {


    public static List<byte[]> convertPdfToImage(MultipartFile multipartFile) throws IOException {
        List<String> pageTexts = new ArrayList<>();

        List<byte[]> imageByteList = new ArrayList<>();

        try(InputStream inputStream = multipartFile.getInputStream();
            PDDocument pdDocument = PDDocument.load(inputStream)){
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
            int pageCount = pdDocument.getNumberOfPages();

            for (int page = 0; page<pageCount; ++page){
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page,300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            imageByteList.add(baos.toByteArray());
        }
    }
    return imageByteList;
}

}
