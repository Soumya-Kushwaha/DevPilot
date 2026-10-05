package devPilot.backend.dto;

import devPilot.backend.entity.IndexStatus;

import java.time.Instant;
import java.util.UUID;

public record IndexStatusResponse(
        UUID repositoryId,
        IndexStatus indexStatus,
        int totalFiles,
        int processedFiles,
        int chunkCount,
        Instant indexedAt,
        String errorMessage) {
}