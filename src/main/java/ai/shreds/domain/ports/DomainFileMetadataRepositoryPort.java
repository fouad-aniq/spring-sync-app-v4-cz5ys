package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainFileMetadataEntity;

/**
 * Repository port for file metadata operations.
 * This interface defines the contract for persisting and retrieving file metadata.
 */
public interface DomainFileMetadataRepositoryPort {

    /**
     * Saves a file metadata entity.
     *
     * @param metadata The file metadata entity to save
     * @return The saved file metadata entity
     */
    DomainFileMetadataEntity save(DomainFileMetadataEntity metadata);

    /**
     * Finds a file metadata entity by its ID.
     *
     * @param fileID The unique identifier of the file
     * @return The found file metadata entity or null if not found
     */
    DomainFileMetadataEntity findById(String fileID);
}
