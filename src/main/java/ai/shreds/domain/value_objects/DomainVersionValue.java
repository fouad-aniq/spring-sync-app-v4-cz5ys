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
public class DomainVersionValue {

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
     * @return DomainVersionValue
     */
    public static DomainVersionValue createNew(int versionNumber, String checksum) {
        return DomainVersionValue.builder()
                .versionNumber(versionNumber)
                .timestamp(Instant.now())
                .checksum(checksum)
                .build();
    }

    /**
     * Creates a version value object with a specific timestamp.
     *
     * @param versionNumber The version number
     * @param timestamp The version timestamp
     * @param checksum The version checksum
     * @return DomainVersionValue
     */
    public static DomainVersionValue createWithTimestamp(int versionNumber, Instant timestamp, String checksum) {
        return DomainVersionValue.builder()
                .versionNumber(versionNumber)
                .timestamp(timestamp)
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
    public boolean isNewerThan(DomainVersionValue other) {
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
    public boolean hasSameContent(DomainVersionValue other) {
        if (other == null) {
            return false;
        }
        return this.checksum.equals(other.checksum);
    }

    /**
     * Checks if this version is sequential to another version.
     *
     * @param other The other version to compare with
     * @return true if this version number is exactly one more than the other version
     */
    public boolean isSequentialTo(DomainVersionValue other) {
        if (other == null) {
            return this.versionNumber == 1;
        }
        return this.versionNumber == other.versionNumber + 1;
    }

    /**
     * Creates a new version based on this version with an incremented version number.
     *
     * @param newChecksum The checksum for the new version
     * @return A new DomainVersionValue with incremented version number
     */
    public DomainVersionValue createNextVersion(String newChecksum) {
        return DomainVersionValue.builder()
                .versionNumber(this.versionNumber + 1)
                .timestamp(Instant.now())
                .checksum(newChecksum)
                .build();
    }
}
