package ai.shreds.domain.value_objects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.Builder;

import java.time.Instant;

/**
 * Immutable value object representing version information.
 */
@Value
@Builder
public class DomainValueVersion {

    @Min(value = 1, message = "Version number must be at least 1")
    int versionNumber;

    @NotNull(message = "Timestamp cannot be null")
    Instant timestamp;

    @NotBlank(message = "Checksum cannot be blank")
    String checksum;

    /**
     * Creates a new version value object with current timestamp.
     *
     * @param versionNumber The version number
     * @param checksum The version checksum
     * @return DomainValueVersion
     */
    public static DomainValueVersion createNew(int versionNumber, String checksum) {
        return DomainValueVersion.builder()
                .versionNumber(versionNumber)
                .timestamp(Instant.now())
                .checksum(checksum)
                .build();
    }

    /**
     * Validates if this version value object is valid.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return versionNumber >= 1
                && timestamp != null
                && checksum != null && !checksum.trim().isEmpty();
    }

    /**
     * Compares this version with another version.
     *
     * @param other The other version to compare with
     * @return true if this version is newer than the other version
     */
    public boolean isNewerThan(DomainValueVersion other) {
        if (other == null) {
            return true;
        }
        return this.timestamp.isAfter(other.timestamp);
    }

    /**
     * Checks if this version has the same content as another version.
     *
     * @param other The other version to compare with
     * @return true if both versions have the same checksum
     */
    public boolean hasSameContent(DomainValueVersion other) {
        if (other == null) {
            return false;
        }
        return this.checksum.equals(other.checksum);
    }
}
