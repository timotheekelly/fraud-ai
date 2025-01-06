package com.mongodb;

import com.mongodb.client.model.WriteModel;
import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataPreprocessor {

    TransactionRepository transactionRepository;

    private static final int BATCH_SIZE = 1000; // Define batch size for bulk writes

    public DataPreprocessor(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void loadData(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip the header by reading the first line
            reader.readLine();

            List<String> batch = new ArrayList<>();
            String line;

            // Read the file line-by-line
            while ((line = reader.readLine()) != null) {
                batch.add(line);

                // When batch size is reached, process it
                if (batch.size() == BATCH_SIZE) {
                    processBatch(batch);
                    batch.clear(); // Clear the batch for the next set of lines
                }
            }

            // Process any remaining lines
            if (!batch.isEmpty()) {
                processBatch(batch);
            }
        }
    }

    private void processBatch(List<String> batch) {
        List<WriteModel<Document>> bulkOperations = batch.stream()
                .map(line -> {
                    String[] fields = line.split(",");
                    double amount = Double.parseDouble(fields[29]); // Adjust index as needed
                    boolean isFraudulent = "1".equals(fields[30]);
                    Transaction transaction = new Transaction(amount, isFraudulent);
                    return new InsertOneModel<>(transaction.toDocument());
                })
                .collect(Collectors.toList());
        transactionRepository.bulkSaveTransactions(bulkOperations);
    }

}
