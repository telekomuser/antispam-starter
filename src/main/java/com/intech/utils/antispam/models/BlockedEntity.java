package com.intech.utils.antispam.models;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Entity
@Data
@Builder
@Table(name = "antispam", schema = "antispam")
public class BlockedEntity {
    private long id;
    private String userId;
    private LocalDateTime dateAdded;
    private String queryType;
    @Enumerated(EnumType.STRING)
    private CheckProperties blockType;
    @Enumerated(EnumType.STRING)
    private ChronoUnit blockTimeUnit;
}
