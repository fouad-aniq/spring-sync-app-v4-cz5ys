package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.exceptions.DomainExceptionMetadata;
import ai.shreds.domain.ports.DomainPortVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainServiceVersion {

    private final DomainPortVersionRepository versionRepository;

    public void validateVersion(DomainEntityVersionRecord version) throws DomainExceptionMetadata {
        if (!version.isValid()) {
            throw new DomainExceptionMetadata("Invalid version record", "DOMAIN_VER_001");
        }

        // Check if version number is unique for the file
        List<DomainEntityVersionRecord> existingVersions = versionRepository.findByFileId(version.getFileID());
        boolean versionExists = existingVersions.stream()
                .anyMatch(v -> v.getVersionNumber() == version.getVersionNumber());

        if (versionExists) {
            throw new DomainExceptionMetadata(
                    String.format("Version %d already exists for file %s",
                            version.getVersionNumber(), version.getFileID()),
                    "DOMAIN_VER_002"
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
                    "DOMAIN_VER_003"
            );
        }
    }

    public void applyBusinessRulesForVersion(DomainEntityVersionRecord version) {
        // Get previous version if exists
        List<DomainEntityVersionRecord> existingVersions = versionRepository.findByFileId(version.getFileID());
        DomainEntityVersionRecord previousVersion = existingVersions.stream()
                .max(Comparator.comparing(DomainEntityVersionRecord::getVersionNumber))
                .orElse(null);

        if (previousVersion != null) {
            // Check for checksum changes
            if (previousVersion.getChecksum().equals(version.getChecksum())) {
                log.warn("New version {} has same checksum as previous version {} for file {}",
                        version.getVersionNumber(), previousVersion.getVersionNumber(), version.getFileID());
            }

            // Add reference to previous version in additional details
            String details = String.format("Previous version: %d, Previous checksum: %s",
                    previousVersion.getVersionNumber(), previousVersion.getChecksum());
            version.setAdditionalDetails(details);
        }
    }

    public DomainEntityVersionRecord createVersion(DomainEntityVersionRecord version) {
        log.debug("Creating new version {} for file {}", version.getVersionNumber(), version.getFileID());
        validateVersion(version);
        applyBusinessRulesForVersion(version);

        DomainEntityVersionRecord savedVersion = versionRepository.save(version);
        log.info("Created version {} for file {}", savedVersion.getVersionNumber(), savedVersion.getFileID());

        return savedVersion;
    }

    public List<DomainEntityVersionRecord> getVersionHistory(String fileID) {
        log.debug("Retrieving version history for file {}", fileID);
        List<DomainEntityVersionRecord> versions = versionRepository.findByFileId(fileID);
        log.info("Retrieved {} versions for file {}", versions.size(), fileID);

        // Sort versions by version number
        return versions.stream()
                .sorted(Comparator.comparing(DomainEntityVersionRecord::getVersionNumber))
                .toList();
    }

    public DomainEntityVersionRecord getVersion(String versionId) {
        log.debug("Retrieving version {}", versionId);
        DomainEntityVersionRecord version = versionRepository.findVersionById(versionId);
        
        if (version == null) {
            throw new DomainExceptionMetadata(
                    String.format("Version %s not found", versionId),
                    "DOMAIN_VER_004"
            );
        }

        return version;
    }
}
