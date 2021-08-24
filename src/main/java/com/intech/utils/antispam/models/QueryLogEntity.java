package com.intech.utils.antispam.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private String userId;
    private String queryType;
    @Enumerated(EnumType.STRING)
    private Result result;
    @Column(name = "date_added")
    private LocalDateTime dateAdded;
}
