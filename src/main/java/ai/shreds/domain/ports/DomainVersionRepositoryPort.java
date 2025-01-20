package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.exceptions.DomainValidationException;

import java.util.List;
import java.util.Optional;

/**
 * Port defining the contract for version record persistence operations.
 * Implementations handle the storage and retrieval of version records.
 */
public interface DomainVersionRepositoryPort {

    /**
     * Saves a new version record or updates an existing one.
     *
     * @param entity The version record to save
     * @return The saved version record
     * @throws DomainValidationException if the entity is invalid
     */
    DomainEntityVersionRecord save(DomainEntityVersionRecord entity);

    /**
     * Retrieves all version records for a specific file.
     *
     * @param fileID The ID of the file
     * @return List of version records, ordered by version number descending
     */
    List<DomainEntityVersionRecord> findByFileID(String fileID);

    /**
     * Finds a specific version record by its ID.
     *
     * @param id The ID of the version record
     * @return Optional containing the version record if found
     */
    Optional<DomainEntityVersionRecord> findById(String id);

    /**
     * Finds the latest version record for a file.
     *
     * @param fileID The ID of the file
     * @return Optional containing the latest version record if any exists
     */
    default Optional<DomainEntityVersionRecord> findLatestVersion(String fileID) {
        return findByFileID(fileID).stream()
                .max(java.util.Comparator.comparing(DomainEntityVersionRecord::getVersionNumber));
    }

    /**
     * Deletes all version records for a specific file.
     *
     * @param fileID The ID of the file
     */
    default void deleteByFileID(String fileID) {
        throw new UnsupportedOperationException("Delete operation not implemented");
    }

    /**
     * Counts the number of versions for a specific file.
     *
     * @param fileID The ID of the file
     * @return The number of versions
     */
    default long countVersions(String fileID) {
        return findByFileID(fileID).size();
    }

    /**
     * Checks if a specific version exists.
     *
     * @param versionId The ID of the version to check
     * @return true if the version exists, false otherwise
     */
    default boolean existsById(String versionId) {
        return findById(versionId).isPresent();
    }
}
