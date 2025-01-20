package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityConflictResolutionRecord;
import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.ports.DomainConflictResolutionPort;
import ai.shreds.domain.exceptions.DomainValidationException;
import ai.shreds.domain.exceptions.DomainConflictException;

import java.util.List;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

public class DomainConflictService {

    private final DomainConflictResolutionPort conflictRepository;

    public DomainConflictService(DomainConflictResolutionPort conflictRepository) {
        this.conflictRepository = conflictRepository;
    }

    public DomainEntityVersionRecord resolveConflict(List<DomainEntityVersionRecord> versions, String strategy) {
        validateConflictResolutionRequest(versions, strategy);

        DomainEntityVersionRecord resolvedVersion;
        switch (strategy.toUpperCase()) {
            case "LAST_MODIFIED":
                resolvedVersion = versions.stream()
                    .max(Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
                    .orElseThrow(() -> new DomainConflictException("No version found to resolve conflict"));
                break;

            case "FIRST_MODIFIED":
                resolvedVersion = versions.stream()
                    .min(Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
                    .orElseThrow(() -> new DomainConflictException("No version found to resolve conflict"));
                break;

            case "KEEP_LONGEST":
                resolvedVersion = versions.stream()
                    .max(Comparator.comparing(v -> v.getAdditionalDetails().length()))
                    .orElseThrow(() -> new DomainConflictException("No version found to resolve conflict"));
                break;

            case "MERGE":
                resolvedVersion = mergeVersions(versions);
                break;

            default:
                throw new DomainValidationException("Unsupported resolution strategy: " + strategy);
        }

        return conflictRepository.resolveConflict(versions, strategy);
    }

    public void recordResolution(DomainEntityConflictResolutionRecord resolution) {
        validateResolutionRecord(resolution);
        resolution.setId(UUID.randomUUID().toString());
        resolution.setResolutionTimestamp(new Date());
        conflictRepository.save(resolution);
    }

    private void validateConflictResolutionRequest(List<DomainEntityVersionRecord> versions, String strategy) {
        if (versions == null || versions.isEmpty()) {
            throw new DomainValidationException("Versions list cannot be null or empty");
        }
        if (versions.size() < 2) {
            throw new DomainValidationException("At least two versions are required for conflict resolution");
        }
        if (strategy == null || strategy.isBlank()) {
            throw new DomainValidationException("Resolution strategy cannot be null or empty");
        }
    }

    private void validateResolutionRecord(DomainEntityConflictResolutionRecord resolution) {
        if (resolution == null) {
            throw new DomainValidationException("Resolution record cannot be null");
        }
        if (resolution.getConflictingVersionIDs() == null || resolution.getConflictingVersionIDs().isEmpty()) {
            throw new DomainValidationException("Conflicting version IDs cannot be null or empty");
        }
        if (resolution.getResolutionStrategy() == null || resolution.getResolutionStrategy().isBlank()) {
            throw new DomainValidationException("Resolution strategy cannot be null or empty");
        }
    }

    private DomainEntityVersionRecord mergeVersions(List<DomainEntityVersionRecord> versions) {
        // This is a simplified merge strategy
        // In a real implementation, this would be more sophisticated based on the file type and content
        DomainEntityVersionRecord latest = versions.stream()
            .max(Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
            .orElseThrow(() -> new DomainConflictException("No version found to merge"));

        // Create a new version with merged information
        DomainEntityVersionRecord merged = new DomainEntityVersionRecord();
        merged.setFileID(latest.getFileID());
        merged.setVersionNumber(latest.getVersionNumber() + 1);
        merged.setChecksum(latest.getChecksum());
        merged.setTimestamp(new Date());
        merged.setAdditionalDetails("Merged version from conflict resolution");

        return merged;
    }
}
