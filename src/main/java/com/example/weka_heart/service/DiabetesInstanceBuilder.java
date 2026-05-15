package com.example.weka_heart.service;

import com.example.weka_heart.request.PatientDataRequest;
import org.springframework.stereotype.Component;
import weka.core.DenseInstance;
import weka.core.Instances;

@Component
public class DiabetesInstanceBuilder {
    /**
     * Creates a {@link DenseInstance} populated with all 8 diabetes attributes,
     * linked to the given dataset structure.
     */
    public DenseInstance build(PatientDataRequest request, Instances datasetStructure) {
        DenseInstance instance = new DenseInstance(datasetStructure.numAttributes());
        instance.setDataset(datasetStructure);

        instance.setValue(datasetStructure.attribute("preg"), request.preg());
        instance.setValue(datasetStructure.attribute("plas"), request.plas());
        instance.setValue(datasetStructure.attribute("pres"), request.pres());
        instance.setValue(datasetStructure.attribute("skin"), request.skin());
        instance.setValue(datasetStructure.attribute("insu"), request.insu());
        instance.setValue(datasetStructure.attribute("mass"), request.mass());
        instance.setValue(datasetStructure.attribute("pedi"), request.pedi());
        instance.setValue(datasetStructure.attribute("age"),  request.age());

        return instance;
    }
}
