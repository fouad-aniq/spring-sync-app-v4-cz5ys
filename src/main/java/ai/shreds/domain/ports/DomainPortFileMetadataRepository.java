package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.domain.value_objects.DomainValueOwnership;

import java.util.List;

/**
 * Repository port for file metadata operations.
 * This interface defines the contract for persisting and retrieving file metadata.
 */
public interface DomainPortFileMetadataRepository {

    /**
     * Saves a file metadata entity.
     *
     * @param metadata The file metadata entity to save
     * @return The saved file metadata entity
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the save operation fails
     */
    DomainEntityFileMetadata save(DomainEntityFileMetadata metadata);

    /**
     * Finds a file metadata entity by its ID.
     *
     * @param fileID The unique identifier of the file
     * @return The found file metadata entity or null if not found
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    DomainEntityFileMetadata findById(String fileID);

    /**
     * Finds all file metadata entities owned by a specific owner.
     *
     * @param ownership The ownership details to search for
     * @return List of file metadata entities owned by the specified owner
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    List<DomainEntityFileMetadata> findByOwnership(DomainValueOwnership ownership);

    /**
     * Finds all file metadata entities in a specific path.
     *
     * @param path The path to search in
     * @return List of file metadata entities in the specified path
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    List<DomainEntityFileMetadata> findByPath(String path);

    /**
     * Deletes a file metadata entity.
     *
     * @param fileID The ID of the file metadata to delete
     * @return true if the entity was deleted, false if it didn't exist
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the delete operation fails
     */
    boolean delete(String fileID);

    /**
     * Checks if a file metadata entity exists.
     *
     * @param fileID The ID of the file metadata to check
     * @return true if the entity exists, false otherwise
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the check operation fails
     */
    boolean exists(String fileID);
}
