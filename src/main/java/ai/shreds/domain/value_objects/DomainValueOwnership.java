package ai.shreds.domain.value_objects;

import ai.shreds.domain.exceptions.DomainValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class DomainValueOwnership {
    private String owner;
    private String group;
    private Map<String, Object> additionalDetails;

    public DomainValueOwnership() {
    }

    public DomainValueOwnership(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(jsonString, Map.class);
            this.owner = (String) map.get("owner");
            this.group = (String) map.get("group");
            map.remove("owner");
            map.remove("group");
            this.additionalDetails = map;
        } catch (Exception e) {
            throw new DomainValidationException("Invalid ownership details format: " + e.getMessage());
        }
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, Object> getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(Map<String, Object> additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public boolean validate() {
        return owner != null && !owner.isBlank();
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = additionalDetails != null ? additionalDetails : Map.of();
            map.put("owner", owner);
            if (group != null) {
                map.put("group", group);
            }
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new DomainValidationException("Failed to serialize ownership details: " + e.getMessage());
        }
    }
}
