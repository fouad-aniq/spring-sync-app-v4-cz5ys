package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityVersionRecord;
import java.util.List;

/**
 * Repository port for version record operations.
 * This interface defines the contract for persisting and retrieving version records.
 */
public interface DomainPortVersionRepository {

    /**
     * Saves a version record entity.
     *
     * @param version The version record entity to save
     * @return The saved version record entity
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the save operation fails
     */
    DomainEntityVersionRecord save(DomainEntityVersionRecord version);

    /**
     * Finds all version records for a specific file.
     *
     * @param fileID The unique identifier of the file
     * @return List of version record entities for the file
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    List<DomainEntityVersionRecord> findByFileId(String fileID);

    /**
     * Finds a specific version record by its ID.
     *
     * @param versionID The unique identifier of the version record
     * @return The found version record entity or null if not found
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    DomainEntityVersionRecord findVersionById(String versionID);

    /**
     * Finds the latest version record for a file.
     *
     * @param fileID The unique identifier of the file
     * @return The latest version record entity or null if no versions exist
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    DomainEntityVersionRecord findLatestVersion(String fileID);

    /**
     * Finds version records by checksum.
     *
     * @param checksum The checksum to search for
     * @return List of version record entities with the specified checksum
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    List<DomainEntityVersionRecord> findByChecksum(String checksum);

    /**
     * Deletes all version records for a file.
     *
     * @param fileID The unique identifier of the file
     * @return The number of version records deleted
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the delete operation fails
     */
    long deleteAllVersions(String fileID);

    /**
     * Deletes a specific version record.
     *
     * @param versionID The unique identifier of the version record to delete
     * @return true if the version was deleted, false if it didn't exist
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the delete operation fails
     */
    boolean deleteVersion(String versionID);

    /**
     * Counts the number of versions for a file.
     *
     * @param fileID The unique identifier of the file
     * @return The number of version records for the file
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the count operation fails
     */
    long countVersions(String fileID);
}
