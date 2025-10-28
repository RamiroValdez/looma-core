package com.amool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataMongoTest
class LoomaCoreApplicationTests {

    @Autowired
    MongoTemplate mongoTemplate; // Para verificar que Mongo embebido arrancó

    @Test
    void contextLoads() {
        // Solo para verificar que el contexto y Mongo se levantaron correctamente
        long count = mongoTemplate.getCollectionNames().size();
        System.out.println("Mongo collections: " + count);
    }
}
