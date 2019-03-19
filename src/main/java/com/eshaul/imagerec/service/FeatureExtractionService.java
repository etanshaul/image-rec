package com.eshaul.imagerec.service;

import com.eshaul.imagerec.domain.BookFeatures;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

@Service
public class FeatureExtractionService {
    public BookFeatures extractText(InputStream input) throws IOException {
        Map<BoundingPoly, String> polyStringMap = doExtractText(input);

        return getBookFeatures(polyStringMap);
    }

    private Map<BoundingPoly, String> doExtractText(InputStream input) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Map<BoundingPoly, String> polyStringMap = Maps.newHashMap();

        ByteString imgBytes = ByteString.readFrom(input);
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            client.close();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return null;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                TextAnnotation annotation = res.getFullTextAnnotation();
                for (Page page : annotation.getPagesList()) {

                    System.out.println("found pages -----");
                    for (Block block : page.getBlocksList()) {
                        String blockText = "";
                        for (Paragraph para : block.getParagraphsList()) {
                            String paraText = "";
                            List<BoundingPoly> paraWords = Lists.newArrayList();
                            for (Word word : para.getWordsList()) {
                                String wordText = "";
                                for (Symbol symbol : word.getSymbolsList()) {
                                    wordText = wordText + symbol.getText();
                                    paraWords.add(word.getBoundingBox());
                                }
                                paraText = String.format("%s %s", paraText, wordText);
                            }
                            polyStringMap.put(para.getBoundingBox(), paraText);
                            blockText = blockText + paraText;
                        }
                    }
                }
            }
        }

        return polyStringMap;
    }

    private BookFeatures getBookFeatures(Map<BoundingPoly, String> polyStringMap) {
        BoundingPoly titlePoly = polyStringMap.keySet().stream().min(Comparator.comparingInt(this::computePolyArea)).orElse(null);

        return new BookFeatures(polyStringMap.get(titlePoly), "todo:description", "todo:author", "todo:date");
    }


    public File getFeatureBoundingBox(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[39487];
        int n = 0;
        while ((n = input.read(buf)) >= 0)
            baos.write(buf, 0, n);
        byte[] content = baos.toByteArray();
        InputStream is1 = new ByteArrayInputStream(content);
        InputStream is2 = new ByteArrayInputStream(content);

        Map<BoundingPoly, String> polyStringMap = doExtractText(is1);

        BufferedImage image = ImageIO.read(is2);

        Set<BoundingPoly> polys = polyStringMap.keySet();



        Graphics2D gfx = image.createGraphics();
        gfx.setStroke(new BasicStroke(5));
        gfx.setColor(new Color(0x00ff00));

        for (BoundingPoly poly : polys) {
            Polygon jpoly = new Polygon();
            for (Vertex vertex : poly.getVerticesList()) {
                jpoly.addPoint(vertex.getX(), vertex.getY());
            }
            gfx.draw(jpoly);
        }

        File boundingBoxFile = new File("boundingbox.png");
        ImageIO.write(image, "png", boundingBoxFile);

        return boundingBoxFile;
    }

    private int computePolyArea(BoundingPoly poly) {
        int xmin = Integer.MAX_VALUE;
        int xmax = Integer.MIN_VALUE;
        int ymin = Integer.MAX_VALUE;
        int ymax = Integer.MIN_VALUE;

        System.out.println("num vertices: " + poly.getVerticesList().size());

        for (Vertex v : poly.getVerticesList()) {
            xmin = Math.min(v.getX(), xmin);
            xmax = Math.max(v.getX(), xmax);
            ymin = Math.min(v.getY(), ymin);
            ymax = Math.max(v.getY(), ymax);
        }

        return (xmax - xmin) * (ymax - ymin);
    }
}
