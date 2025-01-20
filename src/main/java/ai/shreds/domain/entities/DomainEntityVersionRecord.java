package ai.shreds.domain.entities;

import ai.shreds.shared.dtos.SharedVersionRecordResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DomainEntityVersionRecord {

    @NotBlank(message = "Version ID cannot be blank")
    private String id;

    @NotBlank(message = "FileID cannot be blank")
    private String fileID;

    @Min(value = 1, message = "Version number must be at least 1")
    private int versionNumber;

    @NotNull(message = "Timestamp cannot be null")
    private Instant timestamp;

    @NotBlank(message = "Checksum cannot be blank")
    private String checksum;

    private String additionalDetails;

    /**
     * Converts this domain entity to a shared response DTO.
     *
     * @return SharedVersionRecordResponse
     */
    public SharedVersionRecordResponse toSharedVersionRecordResponse() {
        return SharedVersionRecordResponse.builder()
                .versionNumber(this.versionNumber)
                .timestamp(this.timestamp.toString())
                .build();
    }

    /**
     * Creates a new version record.
     *
     * @param fileID The ID of the file
     * @param versionNumber The version number
     * @param checksum The checksum of the file version
     * @return DomainEntityVersionRecord
     */
    public static DomainEntityVersionRecord createNew(
            String fileID,
            int versionNumber,
            String checksum) {
        return DomainEntityVersionRecord.builder()
                .id(java.util.UUID.randomUUID().toString())
                .fileID(fileID)
                .versionNumber(versionNumber)
                .timestamp(Instant.now())
                .checksum(checksum)
                .build();
    }

    /**
     * Creates a new version record with additional details.
     *
     * @param fileID The ID of the file
     * @param versionNumber The version number
     * @param checksum The checksum of the file version
     * @param additionalDetails Additional version details
     * @return DomainEntityVersionRecord
     */
    public static DomainEntityVersionRecord createNewWithDetails(
            String fileID,
            int versionNumber,
            String checksum,
            String additionalDetails) {
        return DomainEntityVersionRecord.builder()
                .id(java.util.UUID.randomUUID().toString())
                .fileID(fileID)
                .versionNumber(versionNumber)
                .timestamp(Instant.now())
                .checksum(checksum)
                .additionalDetails(additionalDetails)
                .build();
    }

    /**
     * Validates if this version record is valid.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return id != null && !id.trim().isEmpty()
                && fileID != null && !fileID.trim().isEmpty()
                && versionNumber >= 1
                && timestamp != null
                && checksum != null && !checksum.trim().isEmpty();
    }
}
