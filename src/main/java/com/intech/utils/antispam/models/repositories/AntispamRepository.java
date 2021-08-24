package com.intech.utils.antispam.models.repositories;

import com.intech.utils.antispam.models.BlockedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AntispamRepository extends JpaRepository<BlockedEntity, Long> {
    Optional<BlockedEntity> findByUserId(String userId);
}
