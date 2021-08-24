package com.intech.utils.antispam.models.repositories;

import com.intech.utils.antispam.models.QueryLogEntity;
import com.intech.utils.antispam.models.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLogEntity, Long> {

    long countAllByUserIdAndQueryTypeAndDateAddedAfterAndResult(String userId, String queryType, LocalDateTime time, Result result);
}
