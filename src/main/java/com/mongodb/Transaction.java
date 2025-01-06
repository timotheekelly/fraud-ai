package com.mongodb;

import org.bson.Document;

public class Transaction {
    private double amount;
    private boolean isFraudulent;

    public Transaction(double amount, boolean isFraudulent) {
        this.amount = amount;
        this.isFraudulent = isFraudulent;
    }

    public double getAmount() { return amount; }
    public boolean isFraudulent() { return isFraudulent; }

    public Document toDocument() {
        Document doc = new Document();
        doc.append("amount", amount);
        doc.append("isFraudulent", isFraudulent);
        return doc;
    }
}