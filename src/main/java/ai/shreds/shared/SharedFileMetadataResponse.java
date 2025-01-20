package ai.shreds.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for file metadata response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedFileMetadataResponse {

    @NotNull(message = "FileID cannot be null")
    private String fileID;

    @NotNull(message = "Path cannot be null")
    private String path;

    @NotNull(message = "Checksum cannot be null")
    private String checksum;

    @NotNull(message = "Creation timestamp cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String creationTimestamp;

    @NotNull(message = "Last modified timestamp cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String lastModifiedTimestamp;

    @NotNull(message = "Ownership details cannot be null")
    private String ownershipDetails;

    @NotNull(message = "Version number cannot be null")
    private int currentVersionNumber;

    /**
     * Checks if this metadata is newer than another metadata response.
     *
     * @param other The other metadata response to compare with
     * @return true if this metadata is newer
     */
    public boolean isNewerThan(SharedFileMetadataResponse other) {
        if (other == null) {
            return true;
        }
        return this.lastModifiedTimestamp.compareTo(other.lastModifiedTimestamp) > 0;
    }

    /**
     * Checks if this metadata has the same content as another metadata response.
     *
     * @param other The other metadata response to compare with
     * @return true if both have the same content
     */
    public boolean hasSameContent(SharedFileMetadataResponse other) {
        if (other == null) {
            return false;
        }
        return this.checksum.equals(other.checksum);
    }

    /**
     * Creates a builder pre-populated with this response's values.
     *
     * @return A builder initialized with current values
     */
    public SharedFileMetadataResponseBuilder toBuilder() {
        return builder()
                .fileID(this.fileID)
                .path(this.path)
                .checksum(this.checksum)
                .creationTimestamp(this.creationTimestamp)
                .lastModifiedTimestamp(this.lastModifiedTimestamp)
                .ownershipDetails(this.ownershipDetails)
                .currentVersionNumber(this.currentVersionNumber);
    }

    /**
     * Creates a copy of this response with updated timestamps.
     *
     * @param newLastModifiedTimestamp The new last modified timestamp
     * @return A new response with updated timestamp
     */
    public SharedFileMetadataResponse withUpdatedTimestamp(String newLastModifiedTimestamp) {
        return this.toBuilder()
                .lastModifiedTimestamp(newLastModifiedTimestamp)
                .build();
    }

    /**
     * Creates a copy of this response with an incremented version number.
     *
     * @return A new response with incremented version
     */
    public SharedFileMetadataResponse withIncrementedVersion() {
        return this.toBuilder()
                .currentVersionNumber(this.currentVersionNumber + 1)
                .build();
    }

    /**
     * Creates a copy of this response with new ownership details.
     *
     * @param newOwnershipDetails The new ownership details JSON
     * @return A new response with updated ownership
     */
    public SharedFileMetadataResponse withNewOwnership(String newOwnershipDetails) {
        return this.toBuilder()
                .ownershipDetails(newOwnershipDetails)
                .build();
    }

    /**
     * Validates that the timestamps are logically consistent.
     *
     * @return true if timestamps are valid
     */
    public boolean hasValidTimestamps() {
        try {
            return lastModifiedTimestamp.compareTo(creationTimestamp) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}
