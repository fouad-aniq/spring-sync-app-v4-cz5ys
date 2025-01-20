package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.exceptions.DomainExceptionMetadata;
import ai.shreds.domain.ports.DomainPortVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainVersionService {

    private final DomainPortVersionRepository versionRepository;

    public DomainEntityVersionRecord createVersion(DomainEntityVersionRecord version) {
        log.debug("Creating new version for fileID: {}, version: {}",
                version.getFileID(), version.getVersionNumber());

        validateVersion(version);
        applyVersionRules(version);

        DomainEntityVersionRecord savedVersion = versionRepository.save(version);
        log.info("Created version {} for fileID: {}", 
                savedVersion.getVersionNumber(), savedVersion.getFileID());

        return savedVersion;
    }

    private void validateVersion(DomainEntityVersionRecord version) {
        if (!version.isValid()) {
            throw new DomainExceptionMetadata("Invalid version data", "VER_001");
        }

        // Check if version already exists
        List<DomainEntityVersionRecord> existingVersions = 
                versionRepository.findByFileId(version.getFileID());

        boolean versionExists = existingVersions.stream()
                .anyMatch(v -> v.getVersionNumber() == version.getVersionNumber());

        if (versionExists) {
            throw new DomainExceptionMetadata(
                    String.format("Version %d already exists for file %s",
                            version.getVersionNumber(), version.getFileID()),
                    "VER_002"
            );
        }

        // Ensure version number is sequential
        int maxVersion = existingVersions.stream()
                .mapToInt(DomainEntityVersionRecord::getVersionNumber)
                .max()
                .orElse(0);

        if (version.getVersionNumber() != maxVersion + 1) {
            throw new DomainExceptionMetadata(
                    String.format("Version number must be sequential. Expected %d, got %d",
                            maxVersion + 1, version.getVersionNumber()),
                    "VER_003"
            );
        }
    }

    private void applyVersionRules(DomainEntityVersionRecord version) {
        // Set timestamp if not set
        if (version.getTimestamp() == null) {
            version.setTimestamp(Instant.now());
        }

        // Get previous version if exists
        List<DomainEntityVersionRecord> existingVersions = 
                versionRepository.findByFileId(version.getFileID());

        DomainEntityVersionRecord previousVersion = existingVersions.stream()
                .max(Comparator.comparing(DomainEntityVersionRecord::getVersionNumber))
                .orElse(null);

        if (previousVersion != null) {
            // Check for checksum changes
            if (previousVersion.getChecksum().equals(version.getChecksum())) {
                log.warn("New version has same checksum as previous version {} for file {}",
                        previousVersion.getVersionNumber(), version.getFileID());
            }

            // Add reference to previous version
            String details = String.format("Previous version: %d, checksum: %s",
                    previousVersion.getVersionNumber(), previousVersion.getChecksum());
            version.setAdditionalDetails(details);
        }
    }

    public List<DomainEntityVersionRecord> getVersionHistory(String fileID) {
        log.debug("Retrieving version history for fileID: {}", fileID);

        List<DomainEntityVersionRecord> versions = versionRepository.findByFileId(fileID);
        if (versions.isEmpty()) {
            log.warn("No versions found for fileID: {}", fileID);
        } else {
            log.debug("Found {} versions for fileID: {}", versions.size(), fileID);
        }

        return versions.stream()
                .sorted(Comparator.comparing(DomainEntityVersionRecord::getVersionNumber))
                .toList();
    }

    public DomainEntityVersionRecord getVersion(String versionId) {
        log.debug("Retrieving version: {}", versionId);

        DomainEntityVersionRecord version = versionRepository.findVersionById(versionId);
        if (version == null) {
            throw new DomainExceptionMetadata(
                    String.format("Version not found: %s", versionId),
                    "VER_004"
            );
        }

        return version;
    }
}
