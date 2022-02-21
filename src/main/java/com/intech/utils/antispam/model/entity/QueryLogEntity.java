package com.intech.utils.antispam.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.intech.utils.antispam.model.type.ResultType;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "query_log", schema = "antispam")
public class QueryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "query_type")
    private String queryType;
    @Enumerated(EnumType.STRING)
    private ResultType result;
    @Column(name = "date_added")
    private LocalDateTime dateAdded;
}
