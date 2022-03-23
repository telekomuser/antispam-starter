package com.intech.utils.antispam.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blocked", schema = "antispam")
public class BlockedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "date_added")
    private OffsetDateTime dateAdded;
    @Column(name = "query_type")
    private String queryType;
    @Column(name = "block_period")
    private Integer blockPeriod;
    @Column(name = "block_time_unit")
    @Enumerated(EnumType.STRING)
    private ChronoUnit blockTimeUnit;
    @Column(name = "block_start")
    private OffsetDateTime blockStart;
    @Column(name = "block_end")
    private OffsetDateTime blockEnd;
    @Column(name = "repeat")
    private boolean repeat;
}
