package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityConflictResolution;
import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.exceptions.DomainExceptionMetadata;
import ai.shreds.domain.ports.DomainPortConflictResolutionRepository;
import ai.shreds.domain.ports.DomainPortVersionRepository;
import ai.shreds.shared.enums.SharedEnumResolutionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainServiceConflict {

    private final DomainPortConflictResolutionRepository conflictRepository;
    private final DomainPortVersionRepository versionRepository;

    public void applyBusinessRulesForConflict(DomainEntityConflictResolution conflict) throws DomainExceptionMetadata {
        if (!conflict.isValid()) {
            throw new DomainExceptionMetadata("Invalid conflict resolution data", "DOMAIN_CONF_001");
        }

        // Verify all conflicting versions exist
        for (String versionId : conflict.getConflictingVersionIDs()) {
            DomainEntityVersionRecord version = versionRepository.findVersionById(versionId);
            if (version == null) {
                throw new DomainExceptionMetadata(
                        "Version not found: " + versionId,
                        "DOMAIN_CONF_002"
                );
            }
        }

        // Apply strategy-specific rules
        switch (conflict.getResolutionStrategy()) {
            case LAST_MODIFIED -> applyLastModifiedStrategy(conflict);
            case FIRST_MODIFIED -> applyFirstModifiedStrategy(conflict);
            case MANUAL_MERGE -> validateManualMerge(conflict);
            case KEEP_BOTH -> validateKeepBoth(conflict);
            case FORCE_LATEST_VERSION -> applyForceLatestStrategy(conflict);
            default -> throw new DomainExceptionMetadata(
                    "Unsupported resolution strategy: " + conflict.getResolutionStrategy(),
                    "DOMAIN_CONF_003"
            );
        }
    }

    private void applyLastModifiedStrategy(DomainEntityConflictResolution conflict) {
        List<DomainEntityVersionRecord> versions = conflict.getConflictingVersionIDs().stream()
                .map(versionRepository::findVersionById)
                .toList();

        DomainEntityVersionRecord latest = versions.stream()
                .max(Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
                .orElseThrow(() -> new DomainExceptionMetadata(
                        "No versions found for comparison",
                        "DOMAIN_CONF_004"
                ));

        log.info("Selected latest version {} for conflict resolution", latest.getId());
    }

    private void applyFirstModifiedStrategy(DomainEntityConflictResolution conflict) {
        List<DomainEntityVersionRecord> versions = conflict.getConflictingVersionIDs().stream()
                .map(versionRepository::findVersionById)
                .toList();

        DomainEntityVersionRecord earliest = versions.stream()
                .min(Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
                .orElseThrow(() -> new DomainExceptionMetadata(
                        "No versions found for comparison",
                        "DOMAIN_CONF_005"
                ));

        log.info("Selected earliest version {} for conflict resolution", earliest.getId());
    }

    private void validateManualMerge(DomainEntityConflictResolution conflict) {
        // Ensure we have exactly two versions for manual merge
        if (conflict.getConflictingVersionIDs().size() != 2) {
            throw new DomainExceptionMetadata(
                    "Manual merge requires exactly two versions",
                    "DOMAIN_CONF_006"
            );
        }
    }

    private void validateKeepBoth(DomainEntityConflictResolution conflict) {
        // Ensure we have at least two versions to keep
        if (conflict.getConflictingVersionIDs().size() < 2) {
            throw new DomainExceptionMetadata(
                    "Keep both strategy requires at least two versions",
                    "DOMAIN_CONF_007"
            );
        }
    }

    private void applyForceLatestStrategy(DomainEntityConflictResolution conflict) {
        // Ensure we have at least one version
        if (conflict.getConflictingVersionIDs().isEmpty()) {
            throw new DomainExceptionMetadata(
                    "Force latest strategy requires at least one version",
                    "DOMAIN_CONF_008"
            );
        }

        // Get the version with the highest version number
        List<DomainEntityVersionRecord> versions = conflict.getConflictingVersionIDs().stream()
                .map(versionRepository::findVersionById)
                .toList();

        DomainEntityVersionRecord latest = versions.stream()
                .max(Comparator.comparing(DomainEntityVersionRecord::getVersionNumber))
                .orElseThrow(() -> new DomainExceptionMetadata(
                        "No versions found for comparison",
                        "DOMAIN_CONF_009"
                ));

        log.info("Selected version {} with highest version number for conflict resolution", latest.getId());
    }

    public DomainEntityConflictResolution resolveConflict(DomainEntityConflictResolution conflict) {
        log.debug("Resolving conflict for file: {}", conflict.getFileID());
        applyBusinessRulesForConflict(conflict);
        
        // Apply the resolution
        conflict.markAsResolved();
        DomainEntityConflictResolution resolved = conflictRepository.save(conflict);
        
        log.info("Conflict resolved for file: {} using strategy: {}",
                resolved.getFileID(), resolved.getResolutionStrategy());
        
        return resolved;
    }
}
