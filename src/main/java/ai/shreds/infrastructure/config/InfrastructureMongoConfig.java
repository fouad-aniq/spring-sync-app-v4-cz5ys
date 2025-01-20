package ai.shreds.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableMongoAuditing
public class InfrastructureMongoConfig {

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabaseName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.connection-timeout:30000}")
    private int connectionTimeout;

    @Value("${spring.data.mongodb.max-connection-idle-time:300000}")
    private int maxConnectionIdleTime;

    @Value("${spring.data.mongodb.retry-writes:true}")
    private boolean retryWrites;

    @Bean
    public MongoClient mongoClient() {
        log.info("Initializing MongoDB client for database: {}", mongoDatabaseName);

        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder ->
                        builder.maxConnectionIdleTime(maxConnectionIdleTime, TimeUnit.MILLISECONDS))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS))
                .retryWrites(retryWrites)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        log.info("Initializing MongoTemplate for database: {}", mongoDatabaseName);

        // Create custom converter to remove _class
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(mongoDbFactory(mongoClient)),
                new MongoMappingContext()
        );
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(mongoClient), converter);

        // Ensure indexes
        ensureIndexes(mongoTemplate);

        return mongoTemplate;
    }

    @Bean
    public SimpleMongoClientDatabaseFactory mongoDbFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoDatabaseName);
    }

    private void ensureIndexes(MongoTemplate mongoTemplate) {
        try {
            log.info("Ensuring MongoDB indexes");
            
            // Create indexes for file_metadata collection
            mongoTemplate.indexOps("file_metadata").ensureIndex(
                    new Index().on("fileID", Sort.Direction.ASC).unique());

            // Create indexes for version_record collection
            mongoTemplate.indexOps("version_record").ensureIndex(
                    new Index().on("fileID", Sort.Direction.ASC));
            mongoTemplate.indexOps("version_record").ensureIndex(
                    new Index()
                            .on("fileID", Sort.Direction.ASC)
                            .on("versionNumber", Sort.Direction.ASC)
                            .unique());

            // Create indexes for conflict_resolution collection
            mongoTemplate.indexOps("conflict_resolution").ensureIndex(
                    new Index().on("fileID", Sort.Direction.ASC));

            log.info("MongoDB indexes created successfully");
        } catch (Exception e) {
            log.error("Error creating MongoDB indexes", e);
            throw new RuntimeException("Failed to create MongoDB indexes", e);
        }
    }
}
