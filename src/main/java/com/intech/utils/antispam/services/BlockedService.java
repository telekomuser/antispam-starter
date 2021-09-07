package com.intech.utils.antispam.services;

import com.intech.utils.antispam.models.BlockedEntity;
import com.intech.utils.antispam.models.repositories.BlockedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockedService {

    private final BlockedRepository blockedRepository;

    Optional<BlockedEntity> findBlockedSubscriberByUserId(String userId, String queryType) {
        return blockedRepository.findFirstByUserIdAndQueryTypeAndBlockEndAfterOrderByBlockEndDesc(userId, queryType, LocalDateTime.now());

    }

    BlockedEntity lock(String userId, String queryType, int blockPeriod, ChronoUnit blockTimeUnit, boolean repeat) {
        log.info("Lock {} for {} {}", userId, blockPeriod, blockTimeUnit);
        final BlockedEntity blockedSubscriber = blockedRepository.findFirstByUserIdAndQueryType(userId, queryType)
                .orElse(emptyBlockSubscriber(userId, queryType));
        blockedSubscriber.setUserId(userId);
        blockedSubscriber.setDateAdded(LocalDateTime.now());
        blockedSubscriber.setBlockTimeUnit(blockTimeUnit);
        blockedSubscriber.setBlockStart(LocalDateTime.now());
        blockedSubscriber.setBlockPeriod(blockPeriod);
        blockedSubscriber.setRepeat(repeat);
        blockedSubscriber.setBlockEnd(LocalDateTime.now().plus(blockPeriod, blockTimeUnit));

        return blockedRepository.save(blockedSubscriber);

    }

    void unlock(String userId, String queryType) {
        log.info("Trying unlock subscriber with userId: {}", userId);
        blockedRepository.findFirstByUserIdAndBlockEndAfterAndQueryType(userId, LocalDateTime.now(), queryType)
                .ifPresent(blockedRepository::deleteAll);
    }

    boolean wasBlockedByUserId(String userId) {
        return blockedRepository.existsByUserIdAndBlockStartAfter(userId, LocalDateTime.now().minusHours(24));
    }

    private BlockedEntity emptyBlockSubscriber(String userId, String queryType) {
        return BlockedEntity.builder()
                .userId(userId)
                .queryType(queryType)
                .build();
    }
}
