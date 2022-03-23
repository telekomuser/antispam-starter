package com.intech.utils.antispam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.intech.utils.antispam.model.entity.BlockedEntity;
import com.intech.utils.antispam.model.repository.BlockedRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockedService {

    private final BlockedRepository blockedRepository;

    Optional<BlockedEntity> findBlockedSubscriberByUserId(String userId, String queryType) {
        return blockedRepository.findFirstByUserIdAndQueryTypeAndBlockEndAfterOrderByBlockEndDesc(userId, queryType, OffsetDateTime.now());

    }

    BlockedEntity lock(String userId, String queryType, int blockPeriod, ChronoUnit blockTimeUnit, boolean repeat) {
        log.info("Lock {} for {} {}, was blocked ? {}", userId, blockPeriod, blockTimeUnit, repeat);
        final BlockedEntity blockedSubscriber = blockedRepository.findFirstByUserIdAndQueryType(userId, queryType)
                .orElse(emptyBlockSubscriber(userId, queryType));
        blockedSubscriber.setUserId(userId);
        blockedSubscriber.setDateAdded(OffsetDateTime.now());
        blockedSubscriber.setBlockTimeUnit(blockTimeUnit);
        blockedSubscriber.setBlockStart(OffsetDateTime.now());
        blockedSubscriber.setBlockPeriod(blockPeriod);
        blockedSubscriber.setRepeat(repeat);
        blockedSubscriber.setBlockEnd(OffsetDateTime.now().plus(blockPeriod, blockTimeUnit));

        return blockedRepository.save(blockedSubscriber);

    }

    void unlock(String userId, String queryType) {
        log.info("Trying unlock subscriber with userId: {}", userId);
        blockedRepository.deleteAll(
            blockedRepository.findByUserIdAndBlockEndAfterAndQueryType(userId, OffsetDateTime.now(), queryType));
    }

    boolean wasBlockedByUserId(String userId) {
        return blockedRepository.existsByUserIdAndBlockStartAfter(userId, OffsetDateTime.now().minusHours(24));
    }

    private BlockedEntity emptyBlockSubscriber(String userId, String queryType) {
        return BlockedEntity.builder()
                .userId(userId)
                .queryType(queryType)
                .build();
    }
}
