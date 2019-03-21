package com.eshaul.imagerec.service;

import com.eshaul.imagerec.domain.BookFeatures;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
@Qualifier("test")
public class TestFeatureExtractionService implements FeatureExtractionService {
    @Override
    public BookFeatures extractText(InputStream input) {
        return new BookFeatures("title", "description", "author", "publishedDate");
    }

    @Override
    public File getFeatureBoundingBox(InputStream input) {
        return new File("");
    }
}
