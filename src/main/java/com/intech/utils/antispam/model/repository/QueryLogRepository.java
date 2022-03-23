package com.intech.utils.antispam.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

import com.intech.utils.antispam.model.entity.QueryLogEntity;
import com.intech.utils.antispam.model.type.ResultType;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLogEntity, Long> {

    long countAllByUserIdAndQueryTypeAndDateAddedAfterAndResult(String userId, String queryType, OffsetDateTime time, ResultType result);

    List<QueryLogEntity> findByUserId(String userId);
}
