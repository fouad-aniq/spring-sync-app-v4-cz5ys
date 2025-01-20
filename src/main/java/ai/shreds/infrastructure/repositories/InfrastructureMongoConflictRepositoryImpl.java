package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityConflictResolution;
import ai.shreds.domain.ports.DomainPortConflictResolutionRepository;
import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InfrastructureMongoConflictRepositoryImpl implements DomainPortConflictResolutionRepository {

    private static final String COLLECTION_NAME = "conflict_resolution";

    private final MongoTemplate mongoTemplate;

    @Override
    public DomainEntityConflictResolution save(DomainEntityConflictResolution conflict) {
        try {
            log.debug("Saving conflict resolution for fileID: {}, strategy: {}",
                    conflict.getFileID(), conflict.getResolutionStrategy());

            // Check if conflict resolution already exists
            Query query = new Query(Criteria.where("id").is(conflict.getId()));
            DomainEntityConflictResolution existing = mongoTemplate.findOne(
                    query,
                    DomainEntityConflictResolution.class,
                    COLLECTION_NAME
            );

            DomainEntityConflictResolution saved;
            if (existing != null) {
                // Update existing conflict resolution
                Update update = new Update()
                        .set("resolutionStrategy", conflict.getResolutionStrategy())
                        .set("resolutionTimestamp", conflict.getResolutionTimestamp())
                        .set("resolved", conflict.isResolved())
                        .set("conflictingVersionIDs", conflict.getConflictingVersionIDs());

                mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
                saved = mongoTemplate.findOne(query, DomainEntityConflictResolution.class, COLLECTION_NAME);
                log.debug("Updated existing conflict resolution for fileID: {}", conflict.getFileID());
            } else {
                // Save new conflict resolution
                saved = mongoTemplate.save(conflict, COLLECTION_NAME);
                log.debug("Created new conflict resolution for fileID: {}", conflict.getFileID());
            }

            return saved;
        } catch (Exception e) {
            log.error("Error saving conflict resolution for fileID: {}", conflict.getFileID(), e);
            throw new InfrastructureRepositoryException(
                    "Failed to save conflict resolution: " + e.getMessage(),
                    e,
                    "REPO_006"
            );
        }
    }

    @Override
    public DomainEntityConflictResolution findById(String conflictID) {
        try {
            log.debug("Finding conflict resolution by ID: {}", conflictID);
            Query query = new Query(Criteria.where("id").is(conflictID));
            DomainEntityConflictResolution conflict = mongoTemplate.findOne(
                    query,
                    DomainEntityConflictResolution.class,
                    COLLECTION_NAME
            );

            if (conflict != null) {
                log.debug("Found conflict resolution for fileID: {}", conflict.getFileID());
            } else {
                log.debug("No conflict resolution found for ID: {}", conflictID);
            }

            return conflict;
        } catch (Exception e) {
            log.error("Error finding conflict resolution by ID: {}", conflictID, e);
            throw new InfrastructureRepositoryException(
                    "Failed to find conflict resolution: " + e.getMessage(),
                    e,
                    "REPO_007"
            );
        }
    }

    @Override
    public List<DomainEntityConflictResolution> findByFileId(String fileID) {
        try {
            log.debug("Finding conflict resolutions for fileID: {}", fileID);
            Query query = new Query(Criteria.where("fileID").is(fileID));
            List<DomainEntityConflictResolution> conflicts = mongoTemplate.find(
                    query,
                    DomainEntityConflictResolution.class,
                    COLLECTION_NAME
            );
            log.debug("Found {} conflict resolutions for fileID: {}", conflicts.size(), fileID);
            return conflicts;
        } catch (Exception e) {
            log.error("Error finding conflict resolutions for fileID: {}", fileID, e);
            throw new InfrastructureRepositoryException(
                    "Failed to find conflict resolutions: " + e.getMessage(),
                    e,
                    "REPO_008"
            );
        }
    }
}
