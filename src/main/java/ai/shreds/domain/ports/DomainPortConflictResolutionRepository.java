package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityConflictResolution;
import ai.shreds.shared.enums.SharedEnumResolutionStrategy;

import java.util.List;

/**
 * Repository port for conflict resolution operations.
 * This interface defines the contract for persisting and retrieving conflict resolution records.
 */
public interface DomainPortConflictResolutionRepository {

    /**
     * Saves a conflict resolution entity.
     *
     * @param conflict The conflict resolution entity to save
     * @return The saved conflict resolution entity
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the save operation fails
     */
    DomainEntityConflictResolution save(DomainEntityConflictResolution conflict);

    /**
     * Finds a conflict resolution entity by its ID.
     *
     * @param conflictID The unique identifier of the conflict resolution
     * @return The found conflict resolution entity or null if not found
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    DomainEntityConflictResolution findById(String conflictID);

    /**
     * Finds all conflict resolutions for a specific file.
     *
     * @param fileID The unique identifier of the file
     * @return List of conflict resolution entities for the file
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    List<DomainEntityConflictResolution> findByFileId(String fileID);

    /**
     * Finds unresolved conflicts for a file.
     *
     * @param fileID The unique identifier of the file
     * @return List of unresolved conflict resolution entities
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    List<DomainEntityConflictResolution> findUnresolvedConflicts(String fileID);

    /**
     * Finds conflicts by resolution strategy.
     *
     * @param strategy The resolution strategy to search for
     * @return List of conflict resolution entities using the specified strategy
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the find operation fails
     */
    List<DomainEntityConflictResolution> findByStrategy(SharedEnumResolutionStrategy strategy);

    /**
     * Deletes a conflict resolution record.
     *
     * @param conflictID The unique identifier of the conflict resolution to delete
     * @return true if the record was deleted, false if it didn't exist
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the delete operation fails
     */
    boolean delete(String conflictID);

    /**
     * Deletes all conflict resolutions for a file.
     *
     * @param fileID The unique identifier of the file
     * @return The number of conflict resolutions deleted
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the delete operation fails
     */
    long deleteAllForFile(String fileID);

    /**
     * Counts unresolved conflicts for a file.
     *
     * @param fileID The unique identifier of the file
     * @return The number of unresolved conflicts
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the count operation fails
     */
    long countUnresolvedConflicts(String fileID);

    /**
     * Marks all conflicts for a file as resolved.
     *
     * @param fileID The unique identifier of the file
     * @return The number of conflicts marked as resolved
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException if the update operation fails
     */
    long markAllConflictsResolved(String fileID);
}
