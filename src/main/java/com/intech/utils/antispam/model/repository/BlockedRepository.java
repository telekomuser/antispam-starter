package com.intech.utils.antispam.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import com.intech.utils.antispam.model.entity.BlockedEntity;

@Repository
public interface BlockedRepository extends JpaRepository<BlockedEntity, Long> {

    Optional<BlockedEntity> findFirstByUserIdAndQueryType(String userId, String queryType);

    boolean existsByUserIdAndBlockStartAfter(String userId, LocalDateTime lastDay);

    Optional<BlockedEntity> findFirstByUserIdAndQueryTypeAndBlockEndAfterOrderByBlockEndDesc(String userId, String queryType, LocalDateTime currentDate);

    List<BlockedEntity> findByUserIdAndBlockEndAfterAndQueryType(String userId, LocalDateTime currentDate, String queryType);

}
