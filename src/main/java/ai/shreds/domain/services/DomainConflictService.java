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
public class DomainConflictService {

    private final DomainPortConflictResolutionRepository conflictRepository;
    private final DomainPortVersionRepository versionRepository;

    public DomainEntityConflictResolution resolveConflict(DomainEntityConflictResolution conflict) {
        validateConflict(conflict);
        applyResolutionStrategy(conflict);
        return conflictRepository.save(conflict);
    }

    private void validateConflict(DomainEntityConflictResolution conflict) {
        if (!conflict.isValid()) {
            throw new DomainExceptionMetadata("Invalid conflict resolution data", "CONF_001");
        }

        // Verify all versions exist
        for (String versionId : conflict.getConflictingVersionIDs()) {
            if (versionRepository.findVersionById(versionId) == null) {
                throw new DomainExceptionMetadata(
                        "Version not found: " + versionId,
                        "CONF_002"
                );
            }
        }
    }

    private void applyResolutionStrategy(DomainEntityConflictResolution conflict) {
        List<DomainEntityVersionRecord> versions = conflict.getConflictingVersionIDs().stream()
                .map(versionRepository::findVersionById)
                .toList();

        SharedEnumResolutionStrategy strategy = conflict.getResolutionStrategy();
        switch (strategy) {
            case LAST_MODIFIED -> applyLastModifiedStrategy(versions, conflict);
            case FIRST_MODIFIED -> applyFirstModifiedStrategy(versions, conflict);
            case MANUAL_MERGE -> validateManualMerge(versions, conflict);
            case KEEP_BOTH -> validateKeepBoth(versions, conflict);
            case FORCE_LATEST_VERSION -> applyForceLatestStrategy(versions, conflict);
            default -> throw new DomainExceptionMetadata(
                    "Unsupported resolution strategy: " + strategy,
                    "CONF_003"
            );
        }
    }

    private void applyLastModifiedStrategy(List<DomainEntityVersionRecord> versions, 
                                         DomainEntityConflictResolution conflict) {
        DomainEntityVersionRecord latest = versions.stream()
                .max(Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
                .orElseThrow(() -> new DomainExceptionMetadata(
                        "No versions found for comparison",
                        "CONF_004"
                ));

        log.info("Selected latest version {} for conflict resolution", latest.getId());
        conflict.markAsResolved();
    }

    private void applyFirstModifiedStrategy(List<DomainEntityVersionRecord> versions,
                                          DomainEntityConflictResolution conflict) {
        DomainEntityVersionRecord earliest = versions.stream()
                .min(Comparator.comparing(DomainEntityVersionRecord::getTimestamp))
                .orElseThrow(() -> new DomainExceptionMetadata(
                        "No versions found for comparison",
                        "CONF_005"
                ));

        log.info("Selected earliest version {} for conflict resolution", earliest.getId());
        conflict.markAsResolved();
    }

    private void validateManualMerge(List<DomainEntityVersionRecord> versions,
                                    DomainEntityConflictResolution conflict) {
        if (versions.size() != 2) {
            throw new DomainExceptionMetadata(
                    "Manual merge requires exactly two versions",
                    "CONF_006"
            );
        }
        // Manual merge requires external intervention
        log.info("Manual merge required for versions: {}", 
                versions.stream().map(DomainEntityVersionRecord::getId).toList());
    }

    private void validateKeepBoth(List<DomainEntityVersionRecord> versions,
                                 DomainEntityConflictResolution conflict) {
        if (versions.size() < 2) {
            throw new DomainExceptionMetadata(
                    "Keep both strategy requires at least two versions",
                    "CONF_007"
            );
        }
        conflict.markAsResolved();
        log.info("Keeping all versions: {}", 
                versions.stream().map(DomainEntityVersionRecord::getId).toList());
    }

    private void applyForceLatestStrategy(List<DomainEntityVersionRecord> versions,
                                         DomainEntityConflictResolution conflict) {
        DomainEntityVersionRecord latest = versions.stream()
                .max(Comparator.comparing(DomainEntityVersionRecord::getVersionNumber))
                .orElseThrow(() -> new DomainExceptionMetadata(
                        "No versions found for comparison",
                        "CONF_008"
                ));

        log.info("Forced latest version {} for conflict resolution", latest.getId());
        conflict.markAsResolved();
    }
}
