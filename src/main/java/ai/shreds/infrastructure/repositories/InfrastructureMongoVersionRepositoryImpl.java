package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.ports.DomainPortVersionRepository;
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
public class InfrastructureMongoVersionRepositoryImpl implements DomainPortVersionRepository {

    private static final String COLLECTION_NAME = "version_record";

    private final MongoTemplate mongoTemplate;

    @Override
    public DomainEntityVersionRecord save(DomainEntityVersionRecord version) {
        try {
            log.debug("Saving version record for fileID: {}, version: {}",
                    version.getFileID(), version.getVersionNumber());

            // Check if version already exists
            Query query = new Query(Criteria.where("id").is(version.getId()));
            DomainEntityVersionRecord existing = mongoTemplate.findOne(
                    query,
                    DomainEntityVersionRecord.class,
                    COLLECTION_NAME
            );

            DomainEntityVersionRecord saved;
            if (existing != null) {
                // Update existing version
                Update update = new Update()
                        .set("checksum", version.getChecksum())
                        .set("timestamp", version.getTimestamp())
                        .set("additionalDetails", version.getAdditionalDetails());

                mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
                saved = mongoTemplate.findOne(query, DomainEntityVersionRecord.class, COLLECTION_NAME);
                log.debug("Updated existing version record for fileID: {}, version: {}",
                        version.getFileID(), version.getVersionNumber());
            } else {
                // Save new version
                saved = mongoTemplate.save(version, COLLECTION_NAME);
                log.debug("Created new version record for fileID: {}, version: {}",
                        version.getFileID(), version.getVersionNumber());
            }

            return saved;
        } catch (Exception e) {
            log.error("Error saving version record for fileID: {}, version: {}",
                    version.getFileID(), version.getVersionNumber(), e);
            throw new InfrastructureRepositoryException(
                    "Failed to save version record: " + e.getMessage(),
                    e,
                    "REPO_003"
            );
        }
    }

    @Override
    public List<DomainEntityVersionRecord> findByFileId(String fileID) {
        try {
            log.debug("Finding version records for fileID: {}", fileID);
            Query query = new Query(Criteria.where("fileID").is(fileID));
            List<DomainEntityVersionRecord> versions = mongoTemplate.find(
                    query,
                    DomainEntityVersionRecord.class,
                    COLLECTION_NAME
            );
            log.debug("Found {} version records for fileID: {}", versions.size(), fileID);
            return versions;
        } catch (Exception e) {
            log.error("Error finding version records for fileID: {}", fileID, e);
            throw new InfrastructureRepositoryException(
                    "Failed to find version records: " + e.getMessage(),
                    e,
                    "REPO_004"
            );
        }
    }

    @Override
    public DomainEntityVersionRecord findVersionById(String versionID) {
        try {
            log.debug("Finding version record by ID: {}", versionID);
            Query query = new Query(Criteria.where("id").is(versionID));
            DomainEntityVersionRecord version = mongoTemplate.findOne(
                    query,
                    DomainEntityVersionRecord.class,
                    COLLECTION_NAME
            );
            if (version != null) {
                log.debug("Found version record: {} for fileID: {}",
                        version.getVersionNumber(), version.getFileID());
            } else {
                log.debug("No version record found for ID: {}", versionID);
            }
            return version;
        } catch (Exception e) {
            log.error("Error finding version record by ID: {}", versionID, e);
            throw new InfrastructureRepositoryException(
                    "Failed to find version record: " + e.getMessage(),
                    e,
                    "REPO_005"
            );
        }
    }
}
