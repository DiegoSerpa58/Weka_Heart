package com.example.weka_heart.dto;

import java.util.List;

public record ClassificationResult(
        String algorithm,
        String evaluationMethod,
        double accuracy,
        double kappa,
        double meanAbsoluteError,
        double rootMeanSquaredError,
        List<String> classNames,
        double[][] confusionMatrix,
        double[] precision,
        double[] recall,
        double[] fMeasure,
        int numInstances,
        int numAttributes,
        String datasetName
) {



}
