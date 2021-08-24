package com.intech.utils.antispam.services;

import com.intech.utils.antispam.models.QueryLogEntity;
import com.intech.utils.antispam.models.Result;
import com.intech.utils.antispam.models.repositories.QueryLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryLogService {

    private final QueryLogRepository queryLogRepository;

    public long getUserIdQueriesCount(String userId, String queryType, LocalDateTime time) {
        long queriesCount = queryLogRepository.countAllByUserIdAndQueryTypeAndDateAddedAfterAndResult(userId, queryType, time, Result.SUCCESS);

        log.debug("Queries count for userId:{} and query type:{} = {}", userId, queryType, queriesCount);
        return queriesCount;
    }

    public void logQuery(String userId, String queryType, Result result) {
        final var queryLog = QueryLogEntity.builder()
                .queryType(queryType)
                .dateAdded(LocalDateTime.now())
                .userId(userId)
                .result(result)
                .build();
        log.debug("Add query log. userId {}, query type {}", userId, queryType);
        queryLogRepository.save(queryLog);
    }

    public void deleteUserIdQueries(String userId) {

    }

}
