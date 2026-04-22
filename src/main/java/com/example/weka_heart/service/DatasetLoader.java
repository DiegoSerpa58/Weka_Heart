package com.example.weka_heart.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Responsible only for loading Weka {@link Instances} from different sources.
 * Supports both ARFF (default) and CSV formats.
 */
@Service
public class DatasetLoader {

    /**
     * Loads an {@link Instances} dataset from an uploaded multipart file.
     * The class index is set to the last attribute automatically.
     */
    public Instances load(MultipartFile file) throws IOException, Exception {
        String filename = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        Instances data = filename.toLowerCase().endsWith(".csv")
                ? loadCsv(file.getInputStream())
                : loadArff(file.getInputStream());
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    /**
     * Loads an ARFF dataset from the application classpath (e.g. resources/DataSet/).
     * The class index is set to the last attribute automatically.
     */
    public Instances loadFromClasspath(String resourcePath) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalStateException("Dataset not found on classpath: " + resourcePath);
            }
            Instances data = loadArff(stream);
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Instances loadArff(InputStream stream) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return new Instances(reader);
        }
    }

    private Instances loadCsv(InputStream stream) throws Exception {
        CSVLoader loader = new CSVLoader();
        loader.setSource(stream);
        return loader.getDataSet();
    }
}
