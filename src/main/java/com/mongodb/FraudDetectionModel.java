package com.mongodb;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Collections;
import java.util.List;

public class FraudDetectionModel {
    private MultiLayerNetwork model;
    private static final int NUM_INPUT_FEATURES = 1; // Using only Amount for now
    private static final int NUM_CLASSES = 2;

    public FraudDetectionModel() {
        initializeModel();
    }

    private void initializeModel() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.001))
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(NUM_INPUT_FEATURES)
                        .nOut(10)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(10)
                        .nOut(NUM_CLASSES)
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10));
    }

    public void trainModel(List<Transaction> transactions) {
        // Shuffle the data to ensure random distribution
        Collections.shuffle(transactions);

        // Prepare training data
        DataSet dataSet = prepareTrainingData(transactions);;

        // Train the model
        for (int epoch = 0; epoch < 100; epoch++) {
            model.fit(dataSet);
        }

        System.out.println("Model trained successfully.");
    }

    private DataSet prepareTrainingData(List<Transaction> transactions) {
        int numTransactions = transactions.size();
        INDArray features = Nd4j.create(numTransactions, NUM_INPUT_FEATURES);
        INDArray labels = Nd4j.create(numTransactions, NUM_CLASSES);
        DataSet dataSet;

        for (int i = 0; i < numTransactions; i++) {
            Transaction transaction = transactions.get(i);

            // Use transaction amount as feature
            features.putScalar(new int[]{i, 0}, transaction.getAmount());

            // One-hot encoding for labels
            if (transaction.isFraudulent()) {
                labels.putScalar(new int[]{i, 1}, 1.0);
                labels.putScalar(new int[]{i, 0}, 0.0);
            } else {
                labels.putScalar(new int[]{i, 0}, 1.0);
                labels.putScalar(new int[]{i, 1}, 0.0);
            }
        }

        return dataSet = new DataSet(features, labels);
    }

    public boolean predictFraud(Transaction transaction) {
        // Convert transaction to INDArray
        INDArray input = Nd4j.create(new double[][]{{transaction.getAmount()}});

        // Perform prediction
        INDArray output = model.output(input);

        // Interpret the output
        // Index 1 corresponds to fraud class (assuming one-hot encoding)
        return output.getDouble(0, 1) > 0.5;
    }

    public void evaluateModel(List<Transaction> transactions) {
        // Split data into train and test sets
        int trainSize = (int)(transactions.size() * 0.8);
        List<Transaction> testSet = transactions.subList(trainSize, transactions.size());

        // Prepare test data
        DataSet testData = prepareTrainingData(testSet);

        // Perform evaluation
        Evaluation evaluation = new Evaluation(NUM_CLASSES);
        INDArray predicted = model.output(testData.getFeatures());
        evaluation.eval(testData.getLabels(), predicted);

        // Print evaluation statistics
        System.out.println(evaluation.stats());
    }


}