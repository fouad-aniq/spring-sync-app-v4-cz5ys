package ai.shreds.adapter.primary;

import ai.shreds.application.ports.ApplicationFileMetadataUseCasePort;
import ai.shreds.shared.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/metadata")
@Validated
@Tag(name = "File Metadata", description = "APIs for managing file metadata and versioning")
public class AdapterFileMetadataController {

    private final ApplicationFileMetadataUseCasePort fileMetadataUseCase;

    @Autowired
    public AdapterFileMetadataController(ApplicationFileMetadataUseCasePort fileMetadataUseCase) {
        this.fileMetadataUseCase = fileMetadataUseCase;
    }

    @PostMapping
    @Operation(summary = "Create or update file metadata",
              description = "Creates new metadata for a file or updates existing metadata")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metadata successfully created/updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Conflict detected during update"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SharedCreateUpdateMetadataResponseDTO> createOrUpdateMetadata(
            @Valid @RequestBody SharedFileMetadataCreateUpdateRequestDTO request) {
        return ResponseEntity.ok(fileMetadataUseCase.createOrUpdateMetadata(request));
    }

    @GetMapping("/{fileID}")
    @Operation(summary = "Retrieve file metadata",
              description = "Retrieves metadata for a specific file by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metadata successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "File metadata not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SharedFileMetadataDTO> retrieveMetadata(
            @PathVariable("fileID") @NotBlank(message = "File ID cannot be empty") String fileID) {
        return ResponseEntity.ok(fileMetadataUseCase.retrieveMetadata(fileID));
    }

    @GetMapping("/{fileID}/versions")
    @Operation(summary = "Retrieve version history",
              description = "Retrieves the version history for a specific file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Version history successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SharedVersionRecordDTO>> retrieveVersionHistory(
            @PathVariable("fileID") @NotBlank(message = "File ID cannot be empty") String fileID) {
        return ResponseEntity.ok(fileMetadataUseCase.retrieveVersionHistory(fileID));
    }

    @PostMapping("/{fileID}/conflict")
    @Operation(summary = "Resolve version conflict",
              description = "Resolves conflicts between different versions of a file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conflict successfully resolved"),
        @ApiResponse(responseCode = "400", description = "Invalid resolution request"),
        @ApiResponse(responseCode = "404", description = "File or version not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SharedConflictResolutionResponseDTO> resolveConflict(
            @PathVariable("fileID") @NotBlank(message = "File ID cannot be empty") String fileID,
            @Valid @RequestBody SharedConflictResolutionRequestDTO request) {
        return ResponseEntity.ok(fileMetadataUseCase.resolveConflict(fileID, request));
    }
}
