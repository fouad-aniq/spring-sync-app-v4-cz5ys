package ai.shreds.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for file metadata creation or update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedFileMetadataRequest {

    @NotBlank(message = "FileID cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9-_]{1,64}$", 
            message = "FileID must be 1-64 characters long and contain only letters, numbers, hyphens, and underscores")
    private String fileID;

    @NotBlank(message = "Path cannot be blank")
    @Pattern(regexp = "^/[a-zA-Z0-9-_/.]+$", 
            message = "Path must start with / and contain only valid path characters")
    @Size(max = 512, message = "Path cannot be longer than 512 characters")
    private String path;

    @NotBlank(message = "Checksum cannot be blank")
    @Pattern(regexp = "^[a-fA-F0-9]{32,128}$", 
            message = "Checksum must be a valid hash value")
    private String checksum;

    @NotBlank(message = "Creation timestamp cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})?Z$", 
            message = "Creation timestamp must be in ISO-8601 format (e.g., 2023-01-01T12:00:00Z)")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String creationTimestamp;

    @NotBlank(message = "Last modified timestamp cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})?Z$", 
            message = "Last modified timestamp must be in ISO-8601 format (e.g., 2023-01-01T12:00:00Z)")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String lastModifiedTimestamp;

    @NotBlank(message = "Ownership details cannot be blank")
    @Pattern(regexp = "^\\{.*\\}$", message = "Ownership details must be a valid JSON object")
    private String ownershipDetails;

    @Min(value = 1, message = "Version number must be at least 1")
    private int currentVersionNumber;

    /**
     * Validates that the last modified timestamp is not before the creation timestamp.
     *
     * @return true if the timestamps are valid, false otherwise
     */
    public boolean isValidTimestamps() {
        try {
            return lastModifiedTimestamp.compareTo(creationTimestamp) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates that the path is properly formatted.
     *
     * @return true if the path is valid, false otherwise
     */
    public boolean isValidPath() {
        return path != null 
            && path.startsWith("/") 
            && !path.contains("//") 
            && !path.contains("..") 
            && (path.length() == 1 || !path.endsWith("/"));
    }

    /**
     * Creates a builder pre-populated with this request's values.
     *
     * @return A builder initialized with current values
     */
    public SharedFileMetadataRequestBuilder toBuilder() {
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
     * Creates a copy of this request with an incremented version number.
     *
     * @return A new SharedFileMetadataRequest with incremented version
     */
    public SharedFileMetadataRequest createNextVersion() {
        return this.toBuilder()
                .currentVersionNumber(this.currentVersionNumber + 1)
                .build();
    }

    /**
     * Creates a copy of this request with updated timestamps.
     *
     * @param newLastModifiedTimestamp The new last modified timestamp
     * @return A new request with updated timestamp
     */
    public SharedFileMetadataRequest withUpdatedTimestamp(String newLastModifiedTimestamp) {
        return this.toBuilder()
                .lastModifiedTimestamp(newLastModifiedTimestamp)
                .build();
    }

    /**
     * Creates a copy of this request with new ownership details.
     *
     * @param newOwnershipDetails The new ownership details JSON
     * @return A new request with updated ownership
     */
    public SharedFileMetadataRequest withNewOwnership(String newOwnershipDetails) {
        return this.toBuilder()
                .ownershipDetails(newOwnershipDetails)
                .build();
    }
}
