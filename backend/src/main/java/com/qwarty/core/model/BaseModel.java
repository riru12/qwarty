package com.qwarty.core.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

public class BaseModel {
    @CreationTimestamp
    @NotNull
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @UpdateTimestamp
    @NotNull
    private Instant updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;
}
