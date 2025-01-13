package com.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

public class MongoDBConnector {
    private static final String URI = "YOUR-CONNECTION-URI";
    private static final String DATABASE_NAME = "fraudDetection";
    private static final String COLLECTION_NAME = "transactions";

    private final MongoClient mongoClient;

    public MongoDBConnector() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(URI))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS))
                .build();

        mongoClient = MongoClients.create(settings);
    }

    public MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
    }

}
