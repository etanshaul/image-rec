package com.eshaul.imagerec;

import com.eshaul.imagerec.domain.BookFeatures;
import com.eshaul.imagerec.service.FeatureExtractionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.internal.IoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;

@Controller
public class ImageUploadController {


    private FeatureExtractionService featureExtractionService;

    @Autowired
    @Qualifier("default")
    public void setFeatureExtractionService(FeatureExtractionService featureExtractionService) {
        this.featureExtractionService = featureExtractionService;
    }

    @GetMapping("/image")
    @ResponseBody
    public BookFeatures image(@RequestParam("url") String input) throws IOException {
        URL url = new URL(input);
        BufferedImage image = ImageIO.read(url);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        InputStream fis = new ByteArrayInputStream(os.toByteArray());

        return featureExtractionService.extractText(fis);
    }

    @GetMapping("/debug")
    public String debug() {
        return "debug";
    }

    @PostMapping("/debug")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BookFeatures features = featureExtractionService.extractText(file.getInputStream());
        model.addAttribute("json", mapper.writeValueAsString(features));

        byte[] annotatedImageByteArray = getImage(file);
        if (annotatedImageByteArray != null) {
            model.addAttribute("image", Base64.getEncoder().encodeToString(annotatedImageByteArray));
        }
        return "debug";
    }

    private byte[] getImage(MultipartFile file) {
        FileInputStream image = null;
        byte[] imageBytes = null;
        try {
            image = new FileInputStream(featureExtractionService.getFeatureBoundingBox(file.getInputStream()));
            imageBytes = IoUtils.toByteArray(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }

    @GetMapping("/alive")
    @ResponseStatus(HttpStatus.OK)
    public void alive() {
    }

}

