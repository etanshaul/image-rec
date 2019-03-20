package com.eshaul.imagerec.service;

import com.eshaul.imagerec.domain.BookFeatures;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FeatureExtractionService {

    BookFeatures extractText(InputStream input) throws IOException;

    File getFeatureBoundingBox(InputStream input) throws IOException;

}
