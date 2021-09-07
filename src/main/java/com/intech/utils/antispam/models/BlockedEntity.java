package com.intech.utils.antispam.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

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
    private LocalDateTime dateAdded;
    @Column(name = "query_type")
    private String queryType;
    @Column(name = "block_period")
    private Integer blockPeriod;
    @Column(name = "block_time_unit")
    @Enumerated(EnumType.STRING)
    private ChronoUnit blockTimeUnit;
    @Column(name = "block_start")
    private LocalDateTime blockStart;
    @Column(name = "block_end")
    private LocalDateTime blockEnd;
    @Column(name = "repeat")
    private boolean repeat;
}
