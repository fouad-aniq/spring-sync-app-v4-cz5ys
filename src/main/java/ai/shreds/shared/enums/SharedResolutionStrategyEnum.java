package ai.shreds.shared.enums;

public enum SharedResolutionStrategyEnum {
    LAST_MODIFIED,    // Keep the most recently modified version
    FIRST_MODIFIED,   // Keep the earliest modified version
    MERGE,            // Attempt to merge the conflicting versions
    MANUAL,           // Require manual intervention to resolve
    KEEP_LONGEST,     // Keep the version with more content
    KEEP_ALL          // Maintain all versions and create a new branch
}
