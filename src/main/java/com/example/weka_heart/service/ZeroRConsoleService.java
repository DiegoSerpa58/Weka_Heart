package com.example.weka_heart.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

@Service
public class ZeroRConsoleService {

    private static final Logger logger = LoggerFactory.getLogger(ZeroRConsoleService.class);

    private final Instances dataset;
    private final ZeroR model;

    public ZeroRConsoleService() {
        try {
            InputStream stream = getClass().getResourceAsStream("/DataSet/diabetes.arff");
            if (stream == null) {
                throw new IllegalStateException("No se encontró el dataset: /DataSet/diabetes.arff");
            }
            try (InputStreamReader reader = new InputStreamReader(stream)) {
                dataset = new Instances(reader);
                dataset.setClassIndex(dataset.numAttributes() - 1);
            }
            model = new ZeroR();
            model.buildClassifier(dataset);
            logger.info("ZeroRConsoleService: modelo ZeroR entrenado correctamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar ZeroRConsoleService: " + e.getMessage(), e);
        }
    }

    /**
     * Predice la clase para un paciente con los 8 atributos estándar del dataset diabetes.
     */
    public String predict(double preg, double plas, double pres, double skin,
                          double insu, double mass, double pedi, double age) throws Exception {
        DenseInstance instance = new DenseInstance(dataset.numAttributes());
        instance.setDataset(dataset);
        instance.setValue(dataset.attribute("preg"), preg);
        instance.setValue(dataset.attribute("plas"), plas);
        instance.setValue(dataset.attribute("pres"), pres);
        instance.setValue(dataset.attribute("skin"), skin);
        instance.setValue(dataset.attribute("insu"), insu);
        instance.setValue(dataset.attribute("mass"), mass);
        instance.setValue(dataset.attribute("pedi"), pedi);
        instance.setValue(dataset.attribute("age"), age);

        double result = model.classifyInstance(instance);
        return dataset.classAttribute().value((int) result);
    }

    /**
     * Evalúa ZeroR con cross-validation y devuelve el resultado como texto.
     */
    public String evaluateCrossValidation(int folds, int seed) throws Exception {
        ZeroR zeroR = new ZeroR();
        Evaluation eval = new Evaluation(dataset);
        eval.crossValidateModel(zeroR, dataset, folds, new Random(seed));

        return buildEvaluationOutput(eval);
    }

    /**
     * Evalúa ZeroR con percentage split y devuelve el resultado como texto.
     */
    public String evaluatePercentageSplit(double trainPercent, int seed) throws Exception {
        Instances shuffled = new Instances(dataset);
        shuffled.randomize(new Random(seed));

        int trainSize = (int) Math.round(shuffled.numInstances() * trainPercent / 100.0);
        int testSize = shuffled.numInstances() - trainSize;

        Instances trainSet = new Instances(shuffled, 0, trainSize);
        Instances testSet = new Instances(shuffled, trainSize, testSize);

        ZeroR zeroR = new ZeroR();
        zeroR.buildClassifier(trainSet);

        Evaluation eval = new Evaluation(trainSet);
        eval.evaluateModel(zeroR, testSet);

        return buildEvaluationOutput(eval);
    }

    private String buildEvaluationOutput(Evaluation eval) throws Exception {
        return "\n=== Summary ===\n" + eval.toSummaryString()
                + "\n=== Detailed Accuracy By Class ===\n" + eval.toClassDetailsString()
                + "\n=== Confusion Matrix ===\n" + eval.toMatrixString();
    }
}
