package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityConflictResolutionRecord;
import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.ports.DomainConflictResolutionPort;
import ai.shreds.infrastructure.exceptions.InfrastructureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InfrastructureMongoConflictRepositoryImpl implements DomainConflictResolutionPort {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureMongoConflictRepositoryImpl.class);
    private static final String COLLECTION_NAME = "conflict_resolution_records";

    private final MongoTemplate mongoTemplate;

    public InfrastructureMongoConflictRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DomainEntityConflictResolutionRecord save(DomainEntityConflictResolutionRecord entity) {
        try {
            logger.debug("Saving conflict resolution record with strategy: {}", entity.getResolutionStrategy());
            return mongoTemplate.save(entity, COLLECTION_NAME);
        } catch (Exception e) {
            logger.error("Error saving conflict resolution record", e);
            throw new InfrastructureException("Failed to save conflict resolution record", e);
        }
    }

    @Override
    public DomainEntityConflictResolutionRecord findById(String id) {
        try {
            logger.debug("Finding conflict resolution record by ID: {}", id);
            DomainEntityConflictResolutionRecord record = mongoTemplate.findById(id, 
                    DomainEntityConflictResolutionRecord.class, COLLECTION_NAME);
            if (record == null) {
                throw new InfrastructureException("Conflict resolution record not found: " + id);
            }
            return record;
        } catch (Exception e) {
            logger.error("Error finding conflict resolution record by ID: {}", id, e);
            throw new InfrastructureException("Failed to find conflict resolution record", e);
        }
    }

    @Override
    public DomainEntityVersionRecord resolveConflict(List<DomainEntityVersionRecord> versions, String strategy) {
        try {
            logger.debug("Resolving conflict using strategy: {} for {} versions", strategy, versions.size());
            
            // Save the conflict resolution record
            DomainEntityConflictResolutionRecord resolutionRecord = new DomainEntityConflictResolutionRecord();
            resolutionRecord.setConflictingVersionIDs(versions.stream()
                    .map(DomainEntityVersionRecord::getId)
                    .collect(java.util.stream.Collectors.toList()));
            resolutionRecord.setResolutionStrategy(strategy);
            resolutionRecord.setResolutionTimestamp(new java.util.Date());
            save(resolutionRecord);

            // Apply the resolution strategy and return the resolved version
            DomainEntityVersionRecord resolvedVersion = applyResolutionStrategy(versions, strategy);
            resolvedVersion.setId(java.util.UUID.randomUUID().toString());
            resolvedVersion.setVersionNumber(getMaxVersionNumber(versions) + 1);
            resolvedVersion.setTimestamp(new java.util.Date());

            // Save the resolved version
            return mongoTemplate.save(resolvedVersion, "version_records");

        } catch (Exception e) {
            logger.error("Error resolving conflict", e);
            throw new InfrastructureException("Failed to resolve conflict", e);
        }
    }

    private DomainEntityVersionRecord applyResolutionStrategy(
            List<DomainEntityVersionRecord> versions, String strategy) {
        switch (strategy.toUpperCase()) {
            case "LAST_MODIFIED":
                return versions.stream()
                        .max(java.util.Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
                        .orElseThrow(() -> new InfrastructureException("No version found to resolve conflict"));

            case "FIRST_MODIFIED":
                return versions.stream()
                        .min(java.util.Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
                        .orElseThrow(() -> new InfrastructureException("No version found to resolve conflict"));

            default:
                throw new InfrastructureException("Unsupported resolution strategy: " + strategy);
        }
    }

    private int getMaxVersionNumber(List<DomainEntityVersionRecord> versions) {
        return versions.stream()
                .mapToInt(DomainEntityVersionRecord::getVersionNumber)
                .max()
                .orElse(0);
    }
}
