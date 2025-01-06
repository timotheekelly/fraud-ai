package com.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnector {
    private static final String URI = "mongodb+srv://timkelly:secr3t@cluster0.7uay1.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final String DATABASE_NAME = "fraudDetection";
    private static final String COLLECTION_NAME = "transactions";

    private final MongoClient mongoClient;
    private final MongoCollection<Document> collection;

    public MongoDBConnector() {
        mongoClient = MongoClients.create(URI);
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(COLLECTION_NAME);
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void close() {
        mongoClient.close();
    }
}
