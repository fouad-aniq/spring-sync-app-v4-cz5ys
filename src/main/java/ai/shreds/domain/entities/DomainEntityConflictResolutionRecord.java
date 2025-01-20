package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainValidationException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a conflict resolution record.
 * This entity maintains information about how conflicts between different versions were resolved.
 */
public class DomainEntityConflictResolutionRecord {

    private String id;
    private List<String> conflictingVersionIDs;
    private String resolutionStrategy;
    private Date resolutionTimestamp;
    private String resolvedByUser;
    private String resolutionDetails;
    private String resultingVersionId;
    private boolean successful;

    public DomainEntityConflictResolutionRecord() {
        this.id = UUID.randomUUID().toString();
        this.resolutionTimestamp = new Date();
    }

    public DomainEntityConflictResolutionRecord(String id, List<String> conflictingVersionIDs,
            String resolutionStrategy, Date resolutionTimestamp, String resolvedByUser) {
        this.id = id;
        this.conflictingVersionIDs = conflictingVersionIDs;
        this.resolutionStrategy = resolutionStrategy;
        this.resolutionTimestamp = resolutionTimestamp;
        this.resolvedByUser = resolvedByUser;
    }

    public void validate() {
        if (conflictingVersionIDs == null || conflictingVersionIDs.isEmpty()) {
            throw new DomainValidationException("Conflicting version IDs cannot be null or empty");
        }
        if (resolutionStrategy == null || resolutionStrategy.trim().isEmpty()) {
            throw new DomainValidationException("Resolution strategy cannot be null or empty");
        }
        if (resolutionTimestamp == null) {
            throw new DomainValidationException("Resolution timestamp cannot be null");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getConflictingVersionIDs() {
        return conflictingVersionIDs;
    }

    public void setConflictingVersionIDs(List<String> conflictingVersionIDs) {
        this.conflictingVersionIDs = conflictingVersionIDs;
    }

    public String getResolutionStrategy() {
        return resolutionStrategy;
    }

    public void setResolutionStrategy(String resolutionStrategy) {
        this.resolutionStrategy = resolutionStrategy;
    }

    public Date getResolutionTimestamp() {
        return resolutionTimestamp;
    }

    public void setResolutionTimestamp(Date resolutionTimestamp) {
        this.resolutionTimestamp = resolutionTimestamp;
    }

    public String getResolvedByUser() {
        return resolvedByUser;
    }

    public void setResolvedByUser(String resolvedByUser) {
        this.resolvedByUser = resolvedByUser;
    }

    public String getResolutionDetails() {
        return resolutionDetails;
    }

    public void setResolutionDetails(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }

    public String getResultingVersionId() {
        return resultingVersionId;
    }

    public void setResultingVersionId(String resultingVersionId) {
        this.resultingVersionId = resultingVersionId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public DomainEntityConflictResolutionRecord markAsSuccessful(String resultingVersionId) {
        this.successful = true;
        this.resultingVersionId = resultingVersionId;
        return this;
    }

    public DomainEntityConflictResolutionRecord markAsFailed(String details) {
        this.successful = false;
        this.resolutionDetails = details;
        return this;
    }

    @Override
    public String toString() {
        return String.format(
            "ConflictResolution[id=%s, strategy=%s, timestamp=%s, successful=%s]",
            id, resolutionStrategy, resolutionTimestamp, successful
        );
    }
}
