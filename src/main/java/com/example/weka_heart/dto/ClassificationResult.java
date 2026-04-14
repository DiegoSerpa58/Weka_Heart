package com.example.weka_heart.dto;

import java.util.List;

public class ClassificationResult {

    private String algorithm;
    private String evaluationMethod;
    private double accuracy;
    private double kappa;
    private double meanAbsoluteError;
    private double rootMeanSquaredError;
    private List<String> classNames;
    private double[][] confusionMatrix;
    private double[] precision;
    private double[] recall;
    private double[] fMeasure;
    private int numInstances;
    private int numAttributes;
    private String datasetName;

    public ClassificationResult() {}

    public ClassificationResult(String algorithm, String evaluationMethod, double accuracy,
                                 double kappa, double meanAbsoluteError, double rootMeanSquaredError,
                                 List<String> classNames, double[][] confusionMatrix,
                                 double[] precision, double[] recall, double[] fMeasure,
                                 int numInstances, int numAttributes, String datasetName) {
        this.algorithm = algorithm;
        this.evaluationMethod = evaluationMethod;
        this.accuracy = accuracy;
        this.kappa = kappa;
        this.meanAbsoluteError = meanAbsoluteError;
        this.rootMeanSquaredError = rootMeanSquaredError;
        this.classNames = classNames;
        this.confusionMatrix = confusionMatrix;
        this.precision = precision;
        this.recall = recall;
        this.fMeasure = fMeasure;
        this.numInstances = numInstances;
        this.numAttributes = numAttributes;
        this.datasetName = datasetName;
    }

    public String getAlgorithm() { return algorithm; }
    public String getEvaluationMethod() { return evaluationMethod; }
    public double getAccuracy() { return accuracy; }
    public double getKappa() { return kappa; }
    public double getMeanAbsoluteError() { return meanAbsoluteError; }
    public double getRootMeanSquaredError() { return rootMeanSquaredError; }
    public List<String> getClassNames() { return classNames; }
    public double[][] getConfusionMatrix() { return confusionMatrix; }
    public double[] getPrecision() { return precision; }
    public double[] getRecall() { return recall; }
    public double[] getFMeasure() { return fMeasure; }
    public int getNumInstances() { return numInstances; }
    public int getNumAttributes() { return numAttributes; }
    public String getDatasetName() { return datasetName; }
}
