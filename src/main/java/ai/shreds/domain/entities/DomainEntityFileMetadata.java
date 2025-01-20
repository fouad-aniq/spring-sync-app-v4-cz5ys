package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainValueChecksum;
import ai.shreds.domain.value_objects.DomainValueOwnership;
import java.util.Date;

public class DomainEntityFileMetadata {

    private String fileID;
    private String path;
    private DomainValueChecksum checksum;
    private Date creationTimestamp;
    private Date lastModifiedTimestamp;
    private DomainValueOwnership ownershipDetails;
    private int currentVersionNumber;

    public DomainEntityFileMetadata() {
    }

    public DomainEntityFileMetadata(String fileID, String path, DomainValueChecksum checksum,
                                    Date creationTimestamp, Date lastModifiedTimestamp,
                                    DomainValueOwnership ownershipDetails, int currentVersionNumber) {
        this.fileID = fileID;
        this.path = path;
        this.checksum = checksum;
        this.creationTimestamp = creationTimestamp;
        this.lastModifiedTimestamp = lastModifiedTimestamp;
        this.ownershipDetails = ownershipDetails;
        this.currentVersionNumber = currentVersionNumber;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DomainValueChecksum getChecksum() {
        return checksum;
    }

    public void setChecksum(DomainValueChecksum checksum) {
        this.checksum = checksum;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    public DomainValueOwnership getOwnershipDetails() {
        return ownershipDetails;
    }

    public void setOwnershipDetails(DomainValueOwnership ownershipDetails) {
        this.ownershipDetails = ownershipDetails;
    }

    public int getCurrentVersionNumber() {
        return currentVersionNumber;
    }

    public void setCurrentVersionNumber(int currentVersionNumber) {
        this.currentVersionNumber = currentVersionNumber;
    }

    public DomainEntityFileMetadata toDomainEntityFileMetadata() {
        // For demonstration, returns this directly
        return this;
    }
}