package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityConflictResolutionRecord;
import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.exceptions.DomainConflictException;
import ai.shreds.domain.exceptions.DomainValidationException;

import java.util.List;
import java.util.Optional;

/**
 * Port defining the contract for conflict resolution operations.
 * Implementations handle the storage and processing of conflict resolutions.
 */
public interface DomainConflictResolutionPort {

    /**
     * Saves a conflict resolution record.
     *
     * @param entity The conflict resolution record to save
     * @return The saved conflict resolution record
     * @throws DomainValidationException if the entity is invalid
     */
    DomainEntityConflictResolutionRecord save(DomainEntityConflictResolutionRecord entity);

    /**
     * Finds a conflict resolution record by its ID.
     *
     * @param id The ID of the conflict resolution record
     * @return The found conflict resolution record
     * @throws DomainValidationException if the ID is invalid
     */
    DomainEntityConflictResolutionRecord findById(String id);

    /**
     * Resolves conflicts between multiple versions using the specified strategy.
     *
     * @param versions List of conflicting versions
     * @param strategy The resolution strategy to apply
     * @return The resolved version
     * @throws DomainConflictException if the conflict cannot be resolved
     * @throws DomainValidationException if the input parameters are invalid
     */
    DomainEntityVersionRecord resolveConflict(List<DomainEntityVersionRecord> versions, String strategy);

    /**
     * Finds all conflict resolutions for a specific file.
     *
     * @param fileId The ID of the file
     * @return List of conflict resolution records
     */
    default List<DomainEntityConflictResolutionRecord> findByFileId(String fileId) {
        throw new UnsupportedOperationException("Operation not implemented");
    }

    /**
     * Finds the latest conflict resolution for a specific file.
     *
     * @param fileId The ID of the file
     * @return Optional containing the latest conflict resolution if any exists
     */
    default Optional<DomainEntityConflictResolutionRecord> findLatestResolution(String fileId) {
        return findByFileId(fileId).stream()
                .max(java.util.Comparator.comparing(DomainEntityConflictResolutionRecord::getResolutionTimestamp));
    }

    /**
     * Checks if there are any unresolved conflicts for a file.
     *
     * @param fileId The ID of the file
     * @return true if there are unresolved conflicts, false otherwise
     */
    default boolean hasUnresolvedConflicts(String fileId) {
        return findByFileId(fileId).stream()
                .anyMatch(resolution -> !resolution.isSuccessful());
    }

    /**
     * Validates if a resolution strategy is supported.
     *
     * @param strategy The strategy to validate
     * @return true if the strategy is supported, false otherwise
     */
    default boolean isStrategySupported(String strategy) {
        List<String> supportedStrategies = List.of(
            "LAST_MODIFIED", "FIRST_MODIFIED", "MERGE", "MANUAL", "KEEP_LONGEST", "KEEP_ALL"
        );
        return supportedStrategies.contains(strategy.toUpperCase());
    }
}
