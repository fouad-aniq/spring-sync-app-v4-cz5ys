package ai.shreds.application.services;

import ai.shreds.domain.services.DomainVersionService;
import ai.shreds.domain.entities.DomainEntityVersionRecord;
import ai.shreds.domain.value_objects.DomainValueChecksum;
import ai.shreds.shared.dtos.SharedFileMetadataDTO;
import ai.shreds.shared.dtos.SharedVersionRecordDTO;
import ai.shreds.application.mappers.ApplicationMetadataMapper;
import ai.shreds.application.exceptions.ApplicationValidationException;
import ai.shreds.application.exceptions.ApplicationNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ApplicationVersionService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationVersionService.class);

    private final DomainVersionService domainVersionService;
    private final ApplicationMetadataMapper mapper;

    public ApplicationVersionService(DomainVersionService domainVersionService, ApplicationMetadataMapper mapper) {
        this.domainVersionService = domainVersionService;
        this.mapper = mapper;
    }

    @Transactional
    public void createVersion(SharedFileMetadataDTO metadata) {
        try {
            logger.debug("Creating new version for file: {}", metadata.getFileID());

            DomainEntityVersionRecord versionRecord = new DomainEntityVersionRecord();
            versionRecord.setId(UUID.randomUUID().toString());
            versionRecord.setFileID(metadata.getFileID());
            versionRecord.setVersionNumber(metadata.getCurrentVersionNumber());
            versionRecord.setChecksum(new DomainValueChecksum(metadata.getChecksum()));

            domainVersionService.createVersion(versionRecord);
            logger.info("Successfully created version {} for file {}", 
                       metadata.getCurrentVersionNumber(), metadata.getFileID());

        } catch (Exception e) {
            logger.error("Error creating version for file: {}", metadata.getFileID(), e);
            throw new ApplicationValidationException("Failed to create version: " + e.getMessage());
        }
    }

    public List<SharedVersionRecordDTO> getVersionHistory(String fileId) {
        try {
            logger.debug("Retrieving version history for file: {}", fileId);

            List<DomainEntityVersionRecord> versions = domainVersionService.getVersionHistory(fileId);
            if (versions.isEmpty()) {
                logger.warn("No version history found for file: {}", fileId);
                throw new ApplicationNotFoundException("No version history found for file: " + fileId);
            }

            return mapper.toVersionDTOList(versions);

        } catch (ApplicationNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving version history for file: {}", fileId, e);
            throw new ApplicationValidationException("Failed to retrieve version history: " + e.getMessage());
        }
    }
}
