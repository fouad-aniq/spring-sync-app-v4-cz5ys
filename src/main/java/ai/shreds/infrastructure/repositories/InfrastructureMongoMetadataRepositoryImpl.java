package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.domain.ports.DomainPortFileMetadataRepository;
import ai.shreds.infrastructure.cache.InfrastructureRedisMetadataCache;
import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InfrastructureMongoMetadataRepositoryImpl implements DomainPortFileMetadataRepository {

    private static final String COLLECTION_NAME = "file_metadata";

    private final MongoTemplate mongoTemplate;
    private final InfrastructureRedisMetadataCache redisCache;

    @Override
    public DomainEntityFileMetadata save(DomainEntityFileMetadata metadata) {
        try {
            log.debug("Saving metadata for fileID: {}", metadata.getFileID());

            // Save to MongoDB
            DomainEntityFileMetadata saved = mongoTemplate.save(metadata, COLLECTION_NAME);
            log.debug("Saved metadata to MongoDB for fileID: {}", saved.getFileID());

            // Update cache
            redisCache.cacheMetadata(saved.getFileID(), saved);
            log.debug("Updated cache for fileID: {}", saved.getFileID());

            return saved;
        } catch (Exception e) {
            log.error("Error saving metadata for fileID: {}", metadata.getFileID(), e);
            throw new InfrastructureRepositoryException(
                    "Failed to save metadata: " + e.getMessage(),
                    e,
                    "REPO_001"
            );
        }
    }

    @Override
    public DomainEntityFileMetadata findById(String fileID) {
        try {
            log.debug("Finding metadata for fileID: {}", fileID);

            // Try cache first
            return redisCache.getMetadata(fileID)
                    .orElseGet(() -> {
                        log.debug("Cache miss for fileID: {}, querying MongoDB", fileID);

                        // Query MongoDB
                        Query query = new Query(Criteria.where("fileID").is(fileID));
                        DomainEntityFileMetadata metadata = mongoTemplate.findOne(
                                query,
                                DomainEntityFileMetadata.class,
                                COLLECTION_NAME
                        );

                        // Cache if found
                        if (metadata != null) {
                            log.debug("Found metadata in MongoDB for fileID: {}, updating cache", fileID);
                            redisCache.cacheMetadata(fileID, metadata);
                        } else {
                            log.debug("No metadata found in MongoDB for fileID: {}", fileID);
                        }

                        return metadata;
                    });

        } catch (Exception e) {
            log.error("Error finding metadata for fileID: {}", fileID, e);
            throw new InfrastructureRepositoryException(
                    "Failed to find metadata: " + e.getMessage(),
                    e,
                    "REPO_002"
            );
        }
    }
}
