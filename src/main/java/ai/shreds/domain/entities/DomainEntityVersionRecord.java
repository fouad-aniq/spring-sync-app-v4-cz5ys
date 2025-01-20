package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainValueChecksum;
import java.util.Date;

public class DomainEntityVersionRecord {

    private String id;
    private String fileID;
    private int versionNumber;
    private Date timestamp;
    private DomainValueChecksum checksum;
    private String additionalDetails;

    public DomainEntityVersionRecord() {
    }

    public DomainEntityVersionRecord(String id, String fileID, int versionNumber,
                                     Date timestamp, DomainValueChecksum checksum,
                                     String additionalDetails) {
        this.id = id;
        this.fileID = fileID;
        this.versionNumber = versionNumber;
        this.timestamp = timestamp;
        this.checksum = checksum;
        this.additionalDetails = additionalDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public DomainValueChecksum getChecksum() {
        return checksum;
    }

    public void setChecksum(DomainValueChecksum checksum) {
        this.checksum = checksum;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public DomainEntityVersionRecord toDomainEntityVersionRecord() {
        // For demonstration, returns this directly
        return this;
    }
}