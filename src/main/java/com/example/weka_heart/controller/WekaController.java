package com.example.weka_heart.controller;

import com.example.weka_heart.service.WekaService;
import com.example.weka_heart.config.AlgorithmRegistry;
import com.example.weka_heart.dto.ClassificationResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weka")
@CrossOrigin("*")
public class WekaController {

    private final WekaService wekaService;
    private final AlgorithmRegistry algorithmRegistry;

    public WekaController(WekaService wekaService, AlgorithmRegistry algorithmRegistry) {
        this.wekaService       = wekaService;
        this.algorithmRegistry = algorithmRegistry;
    }

    /**
     * Classifies an uploaded dataset using the specified algorithm and evaluation method.
     *
     * @param file             ARFF or CSV multipart file
     * @param algorithm        Algorithm ID (e.g. "ZeroR", "OneR", "NaiveBayes")
     * @param evaluationMethod "crossvalidation" or "percentagesplit"
     * @param folds            Folds for cross-validation (default 10)
     * @param trainPercent     Training percentage for percentage split (default 66.0)
     * @param seed             Random seed (default 1)
     */
    @PostMapping(value = "/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClassificationResult> classify(
            @RequestPart("file")                                      MultipartFile file,
            @RequestParam(defaultValue = "ZeroR")           String    algorithm,
            @RequestParam(defaultValue = "crossvalidation") String    evaluationMethod,
            @RequestParam(defaultValue = "10")              int       folds,
            @RequestParam(defaultValue = "66.0")            double    trainPercent,
            @RequestParam(defaultValue = "1")               int       seed) throws Exception {

        ClassificationResult result = wekaService.classify(
                file, algorithm, evaluationMethod, folds, trainPercent, seed);
        return ResponseEntity.ok(result);
    }

    /** Returns the list of all registered algorithms (id, name, description). */
    @GetMapping("/algorithms")
    public ResponseEntity<List<Map<String, String>>> getAlgorithms() {
        return ResponseEntity.ok(algorithmRegistry.getAlgorithmList());
    }


//    @Autowired
//    private WekaService wekaService;
//
//    @Autowired
//    private AlgorithmRegistry algorithmRegistry;
//
//    /**
//     * Run classification on an uploaded dataset.
//     *
//     * @param file             Multipart file (.arff or .csv)
//     * @param algorithm        Algorithm ID (e.g. "ZeroR", "OneR", "NaiveBayes")
//     * @param evaluationMethod "crossvalidation" or "percentagesplit"
//     * @param folds            Folds for cross-validation (default 10)
//     * @param trainPercent     Training percentage for percentage split (default 66)
//     * @param seed             Random seed (default 1)
//     */
//    @PostMapping(value = "/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> classify(
//            @RequestPart("file") MultipartFile file,
//            @RequestParam(defaultValue = "ZeroR")           String algorithm,
//            @RequestParam(defaultValue = "crossvalidation") String evaluationMethod,
//            @RequestParam(defaultValue = "10")              int    folds,
//            @RequestParam(defaultValue = "66.0")            double trainPercent,
//            @RequestParam(defaultValue = "1")               int    seed) {
//        try {
//            ClassificationResult result = wekaService.classify(
//                    file, algorithm, evaluationMethod, folds, trainPercent, seed);
//            return ResponseEntity.ok(result);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("message", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError()
//                    .body(Map.of("message", "Error processing dataset: " + e.getMessage()));
//        }
//    }
//
//    /** Returns the list of all registered algorithms (id, name, description). */
//    @GetMapping("/algorithms")
//    public ResponseEntity<?> getAlgorithms() {
//        return ResponseEntity.ok(algorithmRegistry.getAlgorithmList());
//    }
}
