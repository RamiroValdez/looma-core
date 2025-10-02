package com.amool.hexagonal.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile("!test")
public class MongoConfig {

    private final MongoTemplate mongoTemplate;
    private final MongoClient mongoClient;

    public MongoConfig(MongoTemplate mongoTemplate, MongoClient mongoClient) {
        this.mongoTemplate = mongoTemplate;
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    public void initMongoCollections() {
        String dbName = mongoTemplate.getDb().getName();
        MongoDatabase db = mongoClient.getDatabase(dbName);
        
        // Create chapter_contents collection only if it doesn't exist
        if (!db.listCollectionNames().into(new java.util.ArrayList<>()).contains("chapter_contents")) {
            db.createCollection("chapter_contents");
            System.out.println("Created empty 'chapter_contents' collection");
        }
    }
}
