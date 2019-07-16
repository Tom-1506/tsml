package classifiers.template;

import timeseriesweka.classifiers.CheckpointClassifier;
import timeseriesweka.classifiers.ContractClassifier;
import timeseriesweka.classifiers.SaveParameterInfo;
import utilities.Copyable;
import utilities.TrainAccuracyEstimate;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;
import weka.core.Randomizable;

import java.io.Serializable;

public interface TemplateClassifierInterface<A extends TemplateClassifierInterface<A>>
    extends Serializable,
            Randomizable,
            SaveParameterInfo,
            CheckpointClassifier,
            ContractClassifier,
            TrainAccuracyEstimate,
            Classifier,
            OptionHandler,
            Copyable<A> {

}