package ai.shreds.adapter.primary;

import ai.shreds.application.ports.ApplicationInputPortConflictResolution;
import ai.shreds.application.ports.ApplicationInputPortFileMetadata;
import ai.shreds.shared.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/metadata")
@Tag(name = "File Metadata", description = "APIs for managing file metadata and version history")
public class AdapterFileMetadataController {

    private final ApplicationInputPortFileMetadata fileMetadataInputPort;
    private final ApplicationInputPortConflictResolution conflictResolutionInputPort;

    @Operation(summary = "Create or update file metadata",
            description = "Creates new metadata for a file or updates existing metadata")
    @ApiResponse(responseCode = "200", description = "Metadata successfully created or updated")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping
    public ResponseEntity<SharedCreateUpdateMetadataResponse> createOrUpdateMetadata(
            @Valid @RequestBody SharedFileMetadataRequest request) {
        log.info("Received create/update metadata request for fileID: {}", request.getFileID());
        SharedCreateUpdateMetadataResponse response = fileMetadataInputPort.createOrUpdateMetadata(request);
        log.info("Completed create/update metadata request for fileID: {} with status: {}", 
                response.getFileID(), response.getStatus());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Retrieve file metadata",
            description = "Retrieves metadata for a specific file by ID")
    @ApiResponse(responseCode = "200", description = "Metadata successfully retrieved")
    @ApiResponse(responseCode = "404", description = "File metadata not found")
    @GetMapping("/{fileID}")
    public ResponseEntity<SharedFileMetadataResponse> retrieveMetadata(
            @PathVariable("fileID") String fileID) {
        log.info("Retrieving metadata for fileID: {}", fileID);
        SharedFileMetadataResponse response = fileMetadataInputPort.retrieveMetadata(fileID);
        log.info("Retrieved metadata for fileID: {}", fileID);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Retrieve version history",
            description = "Retrieves the version history for a specific file")
    @ApiResponse(responseCode = "200", description = "Version history successfully retrieved")
    @ApiResponse(responseCode = "404", description = "File not found")
    @GetMapping("/{fileID}/versions")
    public ResponseEntity<List<SharedVersionRecordResponse>> retrieveVersionHistory(
            @PathVariable("fileID") String fileID) {
        log.info("Retrieving version history for fileID: {}", fileID);
        List<SharedVersionRecordResponse> response = fileMetadataInputPort.retrieveVersionHistory(fileID);
        log.info("Retrieved {} versions for fileID: {}", response.size(), fileID);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Resolve file version conflict",
            description = "Resolves conflicts between different versions of a file")
    @ApiResponse(responseCode = "200", description = "Conflict successfully resolved")
    @ApiResponse(responseCode = "400", description = "Invalid resolution request")
    @ApiResponse(responseCode = "404", description = "File or version not found")
    @PostMapping("/{fileID}/conflict")
    public ResponseEntity<SharedConflictResolutionResponse> resolveConflict(
            @PathVariable("fileID") String fileID,
            @Valid @RequestBody SharedConflictResolutionRequest request) {
        log.info("Resolving conflict for fileID: {} with strategy: {}", fileID, request.getResolutionStrategy());
        SharedConflictResolutionResponse response = conflictResolutionInputPort.resolveConflict(fileID, request);
        log.info("Conflict resolution completed for fileID: {} with result: {}", fileID, response.isConflictResolved());
        return ResponseEntity.ok(response);
    }
}
