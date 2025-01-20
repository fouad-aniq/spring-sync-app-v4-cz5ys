package ai.shreds.shared.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for version record response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedVersionRecordResponse {

    @Min(value = 1, message = "Version number must be at least 1")
    private int versionNumber;

    @NotBlank(message = "Timestamp cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})?Z$", 
            message = "Timestamp must be in ISO-8601 format (e.g., 2023-01-01T12:00:00Z)")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String timestamp;

    private String checksum;
    private String additionalDetails;

    /**
     * Checks if this version is newer than another version.
     *
     * @param other The other version to compare with
     * @return true if this version is newer
     */
    public boolean isNewerThan(SharedVersionRecordResponse other) {
        if (other == null) {
            return true;
        }
        return this.timestamp.compareTo(other.timestamp) > 0;
    }

    /**
     * Checks if this version has the same content as another version.
     *
     * @param other The other version to compare with
     * @return true if both versions have the same checksum
     */
    public boolean hasSameContent(SharedVersionRecordResponse other) {
        if (other == null || this.checksum == null || other.checksum == null) {
            return false;
        }
        return this.checksum.equals(other.checksum);
    }

    /**
     * Creates a builder pre-populated with this response's values.
     *
     * @return A builder initialized with current values
     */
    public SharedVersionRecordResponseBuilder toBuilder() {
        return builder()
                .versionNumber(this.versionNumber)
                .timestamp(this.timestamp)
                .checksum(this.checksum)
                .additionalDetails(this.additionalDetails);
    }

    /**
     * Creates a copy of this response with an incremented version number.
     *
     * @return A new response with incremented version
     */
    public SharedVersionRecordResponse withIncrementedVersion() {
        return this.toBuilder()
                .versionNumber(this.versionNumber + 1)
                .build();
    }

    /**
     * Creates a copy of this response with updated details.
     *
     * @param newDetails The new additional details
     * @return A new response with updated details
     */
    public SharedVersionRecordResponse withDetails(String newDetails) {
        return this.toBuilder()
                .additionalDetails(newDetails)
                .build();
    }
}
