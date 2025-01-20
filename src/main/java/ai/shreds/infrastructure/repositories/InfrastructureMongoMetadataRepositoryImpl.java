package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.domain.ports.DomainFileMetadataRepositoryPort;
import ai.shreds.infrastructure.cache.InfrastructureRedisMetadataCache;
import ai.shreds.infrastructure.exceptions.InfrastructureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InfrastructureMongoMetadataRepositoryImpl implements DomainFileMetadataRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureMongoMetadataRepositoryImpl.class);
    private static final String COLLECTION_NAME = "file_metadata";

    private final MongoTemplate mongoTemplate;
    private final InfrastructureRedisMetadataCache redisCache;

    public InfrastructureMongoMetadataRepositoryImpl(MongoTemplate mongoTemplate, 
            InfrastructureRedisMetadataCache redisCache) {
        this.mongoTemplate = mongoTemplate;
        this.redisCache = redisCache;
    }

    @Override
    public DomainEntityFileMetadata save(DomainEntityFileMetadata entity) {
        try {
            logger.debug("Saving metadata for file: {}", entity.getFileID());
            DomainEntityFileMetadata saved = mongoTemplate.save(entity, COLLECTION_NAME);
            redisCache.cacheMetadata(saved);
            return saved;
        } catch (Exception e) {
            logger.error("Error saving metadata for file: {}", entity.getFileID(), e);
            throw new InfrastructureException("Failed to save metadata", e);
        }
    }

    @Override
    public Optional<DomainEntityFileMetadata> findByFileID(String fileID) {
        try {
            // Try cache first
            Optional<DomainEntityFileMetadata> cached = redisCache.getMetadata(fileID);
            if (cached.isPresent()) {
                return cached;
            }

            // If not in cache, query MongoDB
            Query query = new Query(Criteria.where("fileID").is(fileID));
            DomainEntityFileMetadata found = mongoTemplate.findOne(query, 
                    DomainEntityFileMetadata.class, COLLECTION_NAME);

            // Cache the result if found
            if (found != null) {
                redisCache.cacheMetadata(found);
            }

            return Optional.ofNullable(found);
        } catch (Exception e) {
            logger.error("Error finding metadata for file: {}", fileID, e);
            throw new InfrastructureException("Failed to find metadata", e);
        }
    }

    @Override
    public DomainEntityFileMetadata update(DomainEntityFileMetadata entity) {
        try {
            logger.debug("Updating metadata for file: {}", entity.getFileID());
            Query query = new Query(Criteria.where("fileID").is(entity.getFileID()));
            DomainEntityFileMetadata existing = mongoTemplate.findOne(query, 
                    DomainEntityFileMetadata.class, COLLECTION_NAME);

            if (existing == null) {
                throw new InfrastructureException("Metadata not found for update: " + entity.getFileID());
            }

            DomainEntityFileMetadata updated = mongoTemplate.save(entity, COLLECTION_NAME);
            redisCache.cacheMetadata(updated);
            return updated;
        } catch (Exception e) {
            logger.error("Error updating metadata for file: {}", entity.getFileID(), e);
            throw new InfrastructureException("Failed to update metadata", e);
        }
    }

    @Override
    public void delete(String fileID) {
        try {
            logger.debug("Deleting metadata for file: {}", fileID);
            Query query = new Query(Criteria.where("fileID").is(fileID));
            mongoTemplate.remove(query, COLLECTION_NAME);
            redisCache.invalidateCache(fileID);
        } catch (Exception e) {
            logger.error("Error deleting metadata for file: {}", fileID, e);
            throw new InfrastructureException("Failed to delete metadata", e);
        }
    }
}
