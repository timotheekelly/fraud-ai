package com.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private final MongoCollection<Document> collection;

    public TransactionRepository(MongoDBConnector connector) {
        this.collection = connector.getCollection();
    }

    public void bulkSaveTransactions(List<WriteModel<Document>> transactions) {
        if (!transactions.isEmpty()) {
            try {
                BulkWriteOptions options = new BulkWriteOptions().ordered(true);
                collection.bulkWrite(transactions, options); // Perform bulk write operation
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        for (Document doc : collection.find()) {
            double amount = doc.getDouble("amount");
            boolean isFraudulent = doc.getBoolean("isFraudulent");
            transactions.add(new Transaction(amount, isFraudulent));
        }
        return transactions;
    }

}
