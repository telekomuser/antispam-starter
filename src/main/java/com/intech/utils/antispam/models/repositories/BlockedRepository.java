package com.intech.utils.antispam.models.repositories;

import com.intech.utils.antispam.models.BlockedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockedRepository extends JpaRepository<BlockedEntity, Long> {

    Optional<BlockedEntity> findFirstByUserIdAndQueryType(String userId, String queryType);

    boolean existsByUserIdAndBlockStartAfter(String userId, LocalDateTime lastDay);

    Optional<BlockedEntity> findFirstByUserIdAndQueryTypeAndBlockEndAfterOrderByBlockEndDesc(String userId, String queryType, LocalDateTime currentDate);

    Optional<List<BlockedEntity>> findFirstByUserIdAndBlockEndAfterAndQueryType(String userId, LocalDateTime currentDate, String queryType);

}
