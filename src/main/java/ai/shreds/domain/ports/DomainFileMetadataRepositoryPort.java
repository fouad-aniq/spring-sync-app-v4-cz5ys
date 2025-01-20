package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityFileMetadata;
import java.util.Optional;

public interface DomainFileMetadataRepositoryPort {

    DomainEntityFileMetadata save(DomainEntityFileMetadata entity);

    Optional<DomainEntityFileMetadata> findByFileID(String fileID);

    DomainEntityFileMetadata update(DomainEntityFileMetadata entity);

    void delete(String fileID);

}