package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityConflictResolution;

import java.util.List;

/**
 * Repository port for conflict resolution operations.
 * This interface defines the contract for persisting and retrieving conflict resolution data.
 */
public interface DomainConflictResolutionRepositoryPort {

    /**
     * Saves a conflict resolution entity.
     *
     * @param conflict The conflict resolution entity to save
     * @return The saved conflict resolution entity
     */
    DomainEntityConflictResolution save(DomainEntityConflictResolution conflict);

    /**
     * Finds a conflict resolution entity by its ID.
     *
     * @param conflictID The unique identifier of the conflict resolution
     * @return The found conflict resolution entity or null if not found
     */
    DomainEntityConflictResolution findById(String conflictID);

    /**
     * Finds all conflict resolutions for a specific file.
     *
     * @param fileID The unique identifier of the file
     * @return List of conflict resolution entities for the file
     */
    List<DomainEntityConflictResolution> findByFileId(String fileID);
}
