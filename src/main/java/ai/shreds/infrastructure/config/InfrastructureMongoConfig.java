package ai.shreds.infrastructure.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableMongoRepositories(basePackages = "ai.shreds.infrastructure.repositories")
public class InfrastructureMongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), database);
    }

    @Bean
    public Map<String, String> apiKeys() {
        Map<String, String> apiKeys = new HashMap<>();
        apiKeys.put("monitoring-service", "${monitoring.service.api-key}");
        apiKeys.put("sync-service", "${sync.service.api-key}");
        return apiKeys;
    }
}
