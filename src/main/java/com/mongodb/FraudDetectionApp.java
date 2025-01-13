package com.mongodb;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FraudDetectionApp {
    private static FraudDetectionModel fraudDetectionModel;

    public static void main(String[] args) {
        MongoDBConnector mongoDBConnector = new MongoDBConnector();
        TransactionRepository transactionRepository = new TransactionRepository(mongoDBConnector);
        DataPreprocessor preprocessor = new DataPreprocessor(transactionRepository);

        try {
            // Load and prepare training data, and add to MongoDB
            preprocessor.loadData("src/main/resources/creditcard.csv");
            List<Transaction> transactions = transactionRepository.getAllTransactions();

            // Shuffle and split data
            Collections.shuffle(transactions);
            int trainSize = (int) (transactions.size() * 0.8);
            List<Transaction> trainSet = transactions.subList(0, trainSize);
            List<Transaction> testSet = transactions.subList(trainSize, transactions.size());

            System.out.println("Train size: " + trainSet.size() + ", Test size: " + testSet.size());

            // Create and train fraud detection model
            fraudDetectionModel = new FraudDetectionModel();
            fraudDetectionModel.trainModel(trainSet);

            // Evaluate model
            fraudDetectionModel.evaluateModel(testSet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}