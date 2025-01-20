package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import ai.shreds.domain.ports.DomainFileMetadataRepositoryPort;

public class DomainMetadataService {

    private final DomainFileMetadataRepositoryPort metadataRepository;

    public DomainMetadataService(DomainFileMetadataRepositoryPort metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public DomainEntityFileMetadata createMetadata(DomainEntityFileMetadata metadata) {
        // Basic validation or domain logic here.
        // e.g., metadata.getChecksum().validateChecksum() etc.
        return metadataRepository.save(metadata);
    }

    public DomainEntityFileMetadata updateMetadata(DomainEntityFileMetadata metadata) {
        // Additional domain logic.
        return metadataRepository.update(metadata);
    }

    public DomainEntityFileMetadata getMetadata(String fileId) {
        return metadataRepository.findByFileID(fileId)
                .orElseThrow(() -> new RuntimeException("Metadata not found for fileID: " + fileId));
    }
}