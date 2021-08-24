package com.intech.utils.antispam.services;

import com.intech.utils.antispam.exceptions.TooManyRequests;
import com.intech.utils.antispam.models.BlockedEntity;
import com.intech.utils.antispam.models.CheckProperties;
import com.intech.utils.antispam.models.Result;
import com.intech.utils.antispam.models.Strategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AntispamService {

    private final BlockedService blockedSubscribersService;
    private final QueryLogService queryLogService;

    public void checkRequest(String userId,
                             String queryType,
                             CheckProperties properties,
                             CheckProperties repeatProperties) {
        boolean checked = false;


        if (properties != CheckProperties.NONE && StringUtils.hasLength(userId)) {
            checkUserIdRequest(userId, queryType, properties, repeatProperties);
            checked = true;
        }


        if (checked) {
            logQuery(userId, queryType, Result.SUCCESS);
        }
    }

    public void logQuery(String userId, String queryType, Result result) {
        queryLogService.logQuery(userId, queryType, result);
    }


    private void checkUserIdRequest(String userId,
                                    String queryType,
                                    CheckProperties properties,
                                    CheckProperties repeatProperties) {
        log.info("Check userId request {}", userId)  ;

        var blockPeriod = properties.getBlockPeriod();
        var blockTimeUnit = properties.getBlockPeriodTimeUnit();

        blockedSubscribersService.findBlockedSubscriberByUserId(userId).ifPresent(sub -> {
            throwExceptionByClass(sub.getBlockType().getExtension());
        });

        var queriesCount = queryLogService.getUserIdQueriesCount(userId, queryType, LocalDateTime.now()
                .minus(blockPeriod, blockTimeUnit));
        log.info("checkMsisdnRequest({}) -> actual: {}, max: {}", userId, queriesCount, properties.getBlockCount());
        if (queriesCount >= properties.getBlockCount()) {
            if (repeatProperties != CheckProperties.NONE && blockedSubscribersService.wasBlockedByUserId(userId)) {
                blockPeriod = repeatProperties.getBlockPeriod();
                blockTimeUnit = repeatProperties.getBlockPeriodTimeUnit();

            }
            blockedSubscribersService.lock(BlockedEntity.builder().userId(userId).build(),
                                           queryType,
                                           blockPeriod,
                                           blockTimeUnit);
            throw new TooManyRequests();

        }
    }

    public void unlock(String userId) {
        log.info("Unlock subscriber with userId: {}", userId);
        deleteSubscriberQueries(userId);
        blockedSubscribersService.unlock(userId);
    }

    private void deleteSubscriberQueries(String userId) {
        log.info("Find and delete subscriber queries for userid: {}", userId);
        final var blockedSubscriberOpt = blockedSubscribersService.findBlockedSubscriberByUserId(userId);
        blockedSubscriberOpt.ifPresent(blockedSubscriber -> queryLogService.deleteUserIdQueries(userId));
    }

    private static void throwExceptionByClass(Class<? extends RuntimeException> tClass) {
        try {
            throw tClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
