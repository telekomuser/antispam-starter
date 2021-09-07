package com.intech.utils.antispam.services;

import com.intech.utils.antispam.annotations.Settings;
import com.intech.utils.antispam.models.*;
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
                             Settings properties,
                             Settings repeatProperties) {
        boolean checked = false;


        if (properties.blockCount() > 0 && StringUtils.hasLength(userId)) {
            checkUserIdRequest(userId, queryType, properties, repeatProperties);
            checked = true;
        }

        if (checked) {
            queryLogService.logQuery(userId, queryType, Result.SUCCESS);
        }
    }

    private void checkUserIdRequest(String userId,
                                    String queryType,
                                    Settings properties,
                                    Settings repeatProperties) {
        log.info("Check userId request {}", userId);

        var blockPeriod = properties.blockPeriod();
        var blockTimeUnit = properties.blockPeriodTimeUnit();
        boolean repeat = false;

        blockedSubscribersService.findBlockedSubscriberByUserId(userId, queryType).ifPresent(sub -> {
            if (sub.isRepeat()) {
                throwExceptionByClass(repeatProperties.exception());
            } else {
                throwExceptionByClass(properties.exception());
            }
        });

        var queriesCount = queryLogService.getUserIdQueriesCount(userId, queryType, LocalDateTime.now()
                .minus(blockPeriod, blockTimeUnit));
        log.info("checkMsisdnRequest({}) -> actual: {}, max: {}", userId, queriesCount, properties.blockCount());
        if (queriesCount >= properties.blockCount()) {
            if (repeatProperties.blockCount() > 0 && blockedSubscribersService.wasBlockedByUserId(userId)) {
                repeat = true;
                blockPeriod = repeatProperties.blockPeriod();
                blockTimeUnit = repeatProperties.blockPeriodTimeUnit();

            }
            BlockedEntity blocked = blockedSubscribersService.lock(userId, queryType, blockPeriod, blockTimeUnit, repeat);
            if (blocked.isRepeat()) {
                throwExceptionByClass(repeatProperties.exception());
            } else {
                throwExceptionByClass(properties.exception());
            }
        }
    }

    public void unlock(String userId, String queryType) {
        log.info("Unlock subscriber with userId: {}", userId);
        deleteSubscriberQueries(userId, queryType);
        blockedSubscribersService.unlock(userId, queryType);
    }

    private void deleteSubscriberQueries(String userId, String queryType) {
        log.info("Find and delete subscriber queries for userid: {}", userId);
        final var blockedSubscriberOpt = blockedSubscribersService.findBlockedSubscriberByUserId(userId, queryType);
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
