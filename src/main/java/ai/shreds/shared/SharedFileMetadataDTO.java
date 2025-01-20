package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object representing file metadata")
public class SharedFileMetadataDTO {
    @NotBlank(message = "File ID cannot be empty")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "File ID must be a valid UUID")
    @Schema(description = "Unique identifier of the file", example = "123e4567-e89b-12d3-a456-426614174000")
    private String fileID;

    @NotBlank(message = "File path cannot be empty")
    @Pattern(regexp = "^(/[^/]+)+/?$", message = "File path must be a valid absolute path")
    @Schema(description = "Path to the file in the system", example = "/documents/reports/2023/")
    private String path;

    @NotBlank(message = "Checksum cannot be empty")
    @Pattern(regexp = "^[a-fA-F0-9]{32}$", message = "Checksum must be a valid MD5 hash")
    @Schema(description = "File checksum for integrity verification", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String checksum;

    @NotBlank(message = "Creation timestamp cannot be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
            message = "Creation timestamp must be in ISO-8601 format")
    @Schema(description = "Timestamp when the file was created", example = "2023-01-01T10:00:00Z")
    private String creationTimestamp;

    @NotBlank(message = "Last modified timestamp cannot be empty")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
            message = "Last modified timestamp must be in ISO-8601 format")
    @Schema(description = "Timestamp when the file was last modified", example = "2023-01-01T10:00:00Z")
    private String lastModifiedTimestamp;

    @Schema(description = "Details about file ownership",
           example = "{\"owner\": \"john.doe\", \"group\": \"finance\", \"permissions\": \"rw-r--r--\"}")
    private String ownershipDetails;

    @Min(value = 1, message = "Version number must be at least 1")
    @Schema(description = "Current version number of the file", example = "1")
    private int currentVersionNumber;

    @Schema(description = "Size of the file in bytes", example = "1048576")
    private Long fileSize;

    @Schema(description = "MIME type of the file", example = "application/pdf")
    private String mimeType;

    @Schema(description = "Tags associated with the file", example = "[\"report\", \"2023\", \"financial\"]")
    private List<String> tags;

    @Schema(description = "Additional metadata properties",
           example = "{\"department\": \"finance\", \"classification\": \"confidential\"}")
    private Map<String, String> additionalProperties;

    public static SharedFileMetadataDTO createNew(String path, String checksum) {
        return SharedFileMetadataDTO.builder()
                .fileID(UUID.randomUUID().toString())
                .path(path)
                .checksum(checksum)
                .creationTimestamp(Instant.now().toString())
                .lastModifiedTimestamp(Instant.now().toString())
                .currentVersionNumber(1)
                .build();
    }

    public SharedFileMetadataDTO withUpdatedTimestamp() {
        this.lastModifiedTimestamp = Instant.now().toString();
        return this;
    }

    public SharedFileMetadataDTO incrementVersion() {
        this.currentVersionNumber++;
        return this.withUpdatedTimestamp();
    }
}
