package ai.shreds.domain.value_objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import lombok.Builder;

/**
 * Immutable value object representing ownership details of a file.
 */
@Value
@Builder
public class DomainValueOwnership {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @NotBlank(message = "Owner cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,50}$", message = "Owner must be 3-50 characters long and contain only letters, numbers, underscores, and hyphens")
    String owner;

    @NotBlank(message = "GroupId cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,50}$", message = "GroupId must be 3-50 characters long and contain only letters, numbers, underscores, and hyphens")
    String groupId;

    /**
     * Creates a JSON representation of the ownership details.
     *
     * @return JSON string representation
     */
    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // Fallback to simple JSON format if Jackson fails
            return String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", 
                "owner", owner, "groupId", groupId);
        }
    }

    /**
     * Creates an ownership value object from a JSON string.
     *
     * @param json JSON string representation of ownership details
     * @return DomainValueOwnership instance
     * @throws IllegalArgumentException if the JSON is invalid
     */
    public static DomainValueOwnership fromJson(String json) {
        try {
            return objectMapper.readValue(json, DomainValueOwnership.class);
        } catch (JsonProcessingException e) {
            // Fallback to simple JSON parsing if Jackson fails
            try {
                String cleanJson = json.replaceAll("[{}\"\\"]|\\s", "");
                String[] pairs = cleanJson.split(",");
                String owner = null;
                String groupId = null;

                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        if ("owner".equals(keyValue[0])) {
                            owner = keyValue[1];
                        } else if ("groupId".equals(keyValue[0])) {
                            groupId = keyValue[1];
                        }
                    }
                }

                if (owner == null || groupId == null) {
                    throw new IllegalArgumentException("Invalid ownership JSON format");
                }

                DomainValueOwnership ownership = DomainValueOwnership.builder()
                        .owner(owner)
                        .groupId(groupId)
                        .build();

                if (!ownership.isValid()) {
                    throw new IllegalArgumentException("Invalid ownership data");
                }

                return ownership;
            } catch (Exception ex) {
                throw new IllegalArgumentException("Failed to parse ownership JSON: " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Validates if the ownership details are valid.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return owner != null && !owner.trim().isEmpty() 
            && owner.matches("^[a-zA-Z0-9_-]{3,50}$")
            && groupId != null && !groupId.trim().isEmpty()
            && groupId.matches("^[a-zA-Z0-9_-]{3,50}$");
    }

    /**
     * Checks if this ownership has administrative privileges.
     *
     * @return true if the owner or group has admin rights
     */
    public boolean hasAdminRights() {
        return "admin".equals(owner) || "admin-group".equals(groupId);
    }

    /**
     * Checks if this ownership belongs to the same group as another ownership.
     *
     * @param other The other ownership to compare with
     * @return true if both ownerships belong to the same group
     */
    public boolean isSameGroup(DomainValueOwnership other) {
        if (other == null) {
            return false;
        }
        return this.groupId.equals(other.groupId);
    }
}
