package ai.shreds.domain.value_objects;

public class DomainValueChecksum {

    private String checksumValue;

    public DomainValueChecksum() {
    }

    public DomainValueChecksum(String checksumValue) {
        this.checksumValue = checksumValue;
    }

    public String getChecksumValue() {
        return checksumValue;
    }

    public void setChecksumValue(String checksumValue) {
        this.checksumValue = checksumValue;
    }

    public boolean validateChecksum() {
        // Example validation logic
        return checksumValue != null && !checksumValue.isBlank();
    }

    public DomainValueChecksum toDomainValueChecksum() {
        // For demonstration, returns this directly
        return this;
    }
}