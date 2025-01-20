package ai.shreds.shared;

import ai.shreds.shared.enums.SharedEnumMetadataStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for metadata creation or update operation response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedCreateUpdateMetadataResponse {

    @NotNull(message = "FileID cannot be null")
    private String fileID;

    @NotNull(message = "Metadata cannot be null")
    private SharedFileMetadataResponse metadata;

    @NotNull(message = "Status cannot be null")
    private SharedEnumMetadataStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String operationTimestamp;

    private String operationDetails;

    /**
     * Checks if the operation was successful.
     *
     * @return true if the operation succeeded
     */
    public boolean isSuccessful() {
        return status == SharedEnumMetadataStatus.CREATED 
            || status == SharedEnumMetadataStatus.UPDATED;
    }

    /**
     * Checks if this was a creation operation.
     *
     * @return true if the metadata was created
     */
    public boolean isCreation() {
        return status == SharedEnumMetadataStatus.CREATED;
    }

    /**
     * Checks if this was an update operation.
     *
     * @return true if the metadata was updated
     */
    public boolean isUpdate() {
        return status == SharedEnumMetadataStatus.UPDATED;
    }

    /**
     * Checks if there was a conflict.
     *
     * @return true if a conflict was detected
     */
    public boolean hasConflict() {
        return status == SharedEnumMetadataStatus.CONFLICT_DETECTED;
    }

    /**
     * Creates a builder pre-populated with this response's values.
     *
     * @return A builder initialized with current values
     */
    public SharedCreateUpdateMetadataResponseBuilder toBuilder() {
        return builder()
                .fileID(this.fileID)
                .metadata(this.metadata)
                .status(this.status)
                .operationTimestamp(this.operationTimestamp)
                .operationDetails(this.operationDetails);
    }

    /**
     * Creates a copy of this response with a new status.
     *
     * @param newStatus The new status
     * @return A new response with updated status
     */
    public SharedCreateUpdateMetadataResponse withStatus(SharedEnumMetadataStatus newStatus) {
        return this.toBuilder()
                .status(newStatus)
                .operationTimestamp(java.time.Instant.now().toString())
                .build();
    }

    /**
     * Creates a copy of this response with updated metadata.
     *
     * @param newMetadata The new metadata
     * @return A new response with updated metadata
     */
    public SharedCreateUpdateMetadataResponse withMetadata(SharedFileMetadataResponse newMetadata) {
        return this.toBuilder()
                .metadata(newMetadata)
                .operationTimestamp(java.time.Instant.now().toString())
                .build();
    }

    /**
     * Creates a copy of this response with operation details.
     *
     * @param details The operation details
     * @return A new response with updated details
     */
    public SharedCreateUpdateMetadataResponse withDetails(String details) {
        return this.toBuilder()
                .operationDetails(details)
                .build();
    }

    /**
     * Creates a failure response.
     *
     * @param fileID The file ID
     * @param details The failure details
     * @return A new response indicating failure
     */
    public static SharedCreateUpdateMetadataResponse createFailureResponse(String fileID, String details) {
        return builder()
                .fileID(fileID)
                .status(SharedEnumMetadataStatus.FAILED)
                .operationTimestamp(java.time.Instant.now().toString())
                .operationDetails(details)
                .build();
    }
}
