package com.mongodb;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
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
            BulkWriteResult result = collection.bulkWrite(transactions); // Perform bulk write operation
            if (result.wasAcknowledged()) {
                System.out.println("transactions successfully saved: " + transactions.size());
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
