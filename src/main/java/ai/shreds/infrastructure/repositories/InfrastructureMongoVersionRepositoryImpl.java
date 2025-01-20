package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.ports.DomainVersionRepositoryPort;
import ai.shreds.infrastructure.exceptions.InfrastructureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InfrastructureMongoVersionRepositoryImpl implements DomainVersionRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureMongoVersionRepositoryImpl.class);
    private static final String COLLECTION_NAME = "version_records";

    private final MongoTemplate mongoTemplate;

    public InfrastructureMongoVersionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DomainEntityVersionRecord save(DomainEntityVersionRecord entity) {
        try {
            logger.debug("Saving version record: {} for file: {}", 
                    entity.getVersionNumber(), entity.getFileID());
            return mongoTemplate.save(entity, COLLECTION_NAME);
        } catch (Exception e) {
            logger.error("Error saving version record", e);
            throw new InfrastructureException("Failed to save version record", e);
        }
    }

    @Override
    public List<DomainEntityVersionRecord> findByFileID(String fileID) {
        try {
            logger.debug("Finding version records for file: {}", fileID);
            Query query = new Query(Criteria.where("fileID").is(fileID))
                    .with(org.springframework.data.domain.Sort.by(
                            org.springframework.data.domain.Sort.Direction.DESC, "versionNumber"));
            return mongoTemplate.find(query, DomainEntityVersionRecord.class, COLLECTION_NAME);
        } catch (Exception e) {
            logger.error("Error finding version records for file: {}", fileID, e);
            throw new InfrastructureException("Failed to find version records", e);
        }
    }

    @Override
    public Optional<DomainEntityVersionRecord> findById(String id) {
        try {
            logger.debug("Finding version record by ID: {}", id);
            return Optional.ofNullable(mongoTemplate.findById(id, 
                    DomainEntityVersionRecord.class, COLLECTION_NAME));
        } catch (Exception e) {
            logger.error("Error finding version record by ID: {}", id, e);
            throw new InfrastructureException("Failed to find version record", e);
        }
    }

    public void updateVersionDetails(String id, String additionalDetails) {
        try {
            logger.debug("Updating version details for ID: {}", id);
            Query query = new Query(Criteria.where("id").is(id));
            Update update = new Update().set("additionalDetails", additionalDetails);
            mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        } catch (Exception e) {
            logger.error("Error updating version details for ID: {}", id, e);
            throw new InfrastructureException("Failed to update version details", e);
        }
    }

    public void deleteByFileID(String fileID) {
        try {
            logger.debug("Deleting all version records for file: {}", fileID);
            Query query = new Query(Criteria.where("fileID").is(fileID));
            mongoTemplate.remove(query, COLLECTION_NAME);
        } catch (Exception e) {
            logger.error("Error deleting version records for file: {}", fileID, e);
            throw new InfrastructureException("Failed to delete version records", e);
        }
    }
}
