package com.intech.utils.antispam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.intech.utils.antispam.model.entity.QueryLogEntity;
import com.intech.utils.antispam.model.repository.QueryLogRepository;
import com.intech.utils.antispam.model.type.ResultType;

@Service
@RequiredArgsConstructor
public class QueryLogService {

    private final QueryLogRepository queryLogRepository;

    long getUserIdQueriesCount(String userId, String queryType, LocalDateTime time) {
        return queryLogRepository.countAllByUserIdAndQueryTypeAndDateAddedAfterAndResult(userId, queryType, time, ResultType.SUCCESS);
    }

    void logQuery(String userId, String queryType, ResultType result) {
        final QueryLogEntity queryLog = QueryLogEntity.builder()
                .queryType(queryType)
                .dateAdded(LocalDateTime.now())
                .userId(userId)
                .result(result)
                .build();

        queryLogRepository.save(queryLog);
    }

    List<QueryLogEntity> findAllQueryByuserId(String userId) {
        return queryLogRepository.findByUserId(userId);
    }

    void deleteUserIdQueries(String userId) {
        queryLogRepository.deleteAll(
            queryLogRepository.findByUserId(userId)
        );
    }

}
