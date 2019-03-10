package com.eshaul.imagerec;

import com.eshaul.imagerec.domain.BookFeatures;
import com.eshaul.imagerec.service.FeatureExtractionService;
import io.grpc.internal.IoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class ImageUploadController {

    @Autowired
    FeatureExtractionService featureExtractionService;

    @GetMapping("/upload")
    public String uploadEp(Model model) {
        model.addAttribute("target", "upload");
        return "upload";
    }

    @PostMapping("/upload")
    @ResponseBody
    public BookFeatures handleFileUpload(@RequestParam("file") MultipartFile file,
                                         RedirectAttributes redirectAttributes) throws IOException {

        return featureExtractionService.extractText(file.getInputStream());
    }

    @GetMapping("/debug")
    public String debug(Model model) {
        model.addAttribute("target", "debug");
        return "upload";
    }

    @PostMapping("/debug")
    @ResponseBody
    public ResponseEntity<byte[]> debug(@RequestParam("file") MultipartFile file,
                                        RedirectAttributes redirectAttributes) throws IOException {
        FileInputStream image = null;
        byte[] imageBytes = null;
        try {
            image = new FileInputStream(featureExtractionService.getFeatureBoundingBox(file.getInputStream()));
            imageBytes = IoUtils.toByteArray(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }
}

