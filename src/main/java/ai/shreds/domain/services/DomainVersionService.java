package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.ports.DomainVersionRepositoryPort;
import ai.shreds.domain.exceptions.DomainValidationException;
import java.util.List;
import java.util.UUID;
import java.util.Date;

public class DomainVersionService {

    private final DomainVersionRepositoryPort versionRepository;

    public DomainVersionService(DomainVersionRepositoryPort versionRepository) {
        this.versionRepository = versionRepository;
    }

    public DomainEntityVersionRecord createVersion(DomainEntityVersionRecord version) {
        validateVersion(version);
        version.setId(UUID.randomUUID().toString());
        version.setTimestamp(new Date());
        return versionRepository.save(version);
    }

    public List<DomainEntityVersionRecord> getVersionHistory(String fileId) {
        if (fileId == null || fileId.isBlank()) {
            throw new DomainValidationException("File ID cannot be null or empty");
        }
        return versionRepository.findByFileID(fileId);
    }

    private void validateVersion(DomainEntityVersionRecord version) {
        if (version == null) {
            throw new DomainValidationException("Version cannot be null");
        }
        if (version.getFileID() == null || version.getFileID().isBlank()) {
            throw new DomainValidationException("Version must have a valid file ID");
        }
        if (version.getVersionNumber() < 1) {
            throw new DomainValidationException("Version number must be greater than 0");
        }
        if (version.getChecksum() == null || !version.getChecksum().validateChecksum()) {
            throw new DomainValidationException("Version must have a valid checksum");
        }
    }
}
