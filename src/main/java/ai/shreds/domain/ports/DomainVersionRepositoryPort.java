package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainVersionRecordEntity;

import java.util.List;

/**
 * Repository port for version record operations.
 * This interface defines the contract for persisting and retrieving version records.
 */
public interface DomainVersionRepositoryPort {

    /**
     * Saves a version record entity.
     *
     * @param version The version record entity to save
     * @return The saved version record entity
     */
    DomainVersionRecordEntity save(DomainVersionRecordEntity version);

    /**
     * Finds all version records for a specific file.
     *
     * @param fileID The unique identifier of the file
     * @return List of version record entities for the file
     */
    List<DomainVersionRecordEntity> findByFileId(String fileID);

    /**
     * Finds a specific version record by its ID.
     *
     * @param versionID The unique identifier of the version record
     * @return The found version record entity or null if not found
     */
    DomainVersionRecordEntity findVersionById(String versionID);
}
