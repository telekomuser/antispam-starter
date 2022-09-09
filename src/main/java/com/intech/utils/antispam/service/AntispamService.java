package com.intech.utils.antispam.service;

import com.intech.utils.antispam.annotation.Settings;
import com.intech.utils.antispam.model.entity.BlockedEntity;
import com.intech.utils.antispam.model.type.ResultType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
            queryLogService.logQuery(userId, queryType, ResultType.SUCCESS);
        }
    }

    private void checkUserIdRequest(String userId,
                            String queryType,
                            Settings properties,
                            Settings repeatProperties) {

        long blockPeriod = properties.blockPeriod();
        ChronoUnit blockTimeUnit = properties.blockPeriodTimeUnit();
        boolean repeat = false;

        blockedSubscribersService.findBlockedSubscriberByUserId(userId, queryType).ifPresent(sub -> {
            if (sub.isRepeat()) {
                throwExceptionByClass(repeatProperties.exception());
            } else {
                throwExceptionByClass(properties.exception());
            }
        });

        long queriesCount = queryLogService.getUserIdQueriesCount(userId, queryType, OffsetDateTime.now()
                .minus(blockPeriod, blockTimeUnit));
        log.info("checkMsisdnRequest({}) -> actual: {}, max: {}, between {} {}", 
                userId, queriesCount, properties.blockCount(), blockPeriod, blockTimeUnit, repeat);
        if (queriesCount >= properties.blockCount()) {
            int userBlockPeriod = properties.userBlockPeriod();
            ChronoUnit userBlockTimeUnit = properties.userBlockPeriodTimeUnit();
            if (blockedSubscribersService.wasBlockedByUserId(userId)) {
                repeat = true;
                userBlockPeriod = repeatProperties.userBlockPeriod();
                userBlockTimeUnit = repeatProperties.userBlockPeriodTimeUnit();
                log.info("user {} was blocked -> repeat blocking on {} {}", userId, userBlockPeriod, userBlockTimeUnit);
            }
            BlockedEntity blocked = blockedSubscribersService.lock(userId, queryType, userBlockPeriod, userBlockTimeUnit, repeat);
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
        final Optional<BlockedEntity> blockedSubscriberOpt =
            blockedSubscribersService.findBlockedSubscriberByUserId(userId, queryType);
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
