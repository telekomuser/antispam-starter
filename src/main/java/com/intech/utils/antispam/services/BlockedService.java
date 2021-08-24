package com.intech.utils.antispam.services;

import com.intech.utils.antispam.models.BlockedEntity;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class BlockedService {

    public Optional<BlockedEntity> findBlockedSubscriberByUserId(String userId) {

    }

    public void lock(BlockedEntity entity, String queryType, int blockPeriod, ChronoUnit blockTimeUnit) {

    }

    public void unlock(String userId) {

    }

    public boolean wasBlockedByUserId(String userId) {

    }
}
