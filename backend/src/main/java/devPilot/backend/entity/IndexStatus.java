package devPilot.backend.entity;

import jakarta.persistence.Entity;

public enum IndexStatus {
    PENDING,
    INDEXING,
    READY,
    FAILED
}
