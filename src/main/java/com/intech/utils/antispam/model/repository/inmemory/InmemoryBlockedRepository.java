package com.intech.utils.antispam.model.repository.inmemory;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityNotFoundException;

import com.intech.utils.antispam.model.entity.BlockedEntity;
import com.intech.utils.antispam.model.repository.BlockedRepository;

@Repository
public class InmemoryBlockedRepository implements BlockedRepository {

    private final Map<Long, BlockedEntity> repository = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);
    private final Comparator<BlockedEntity> comparatorByBlockEndDesc = (first, second) -> first.getBlockEnd().compareTo(second.getBlockEnd());

    @Override
    public List<BlockedEntity> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public List<BlockedEntity> findAll(Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BlockedEntity> findAllById(Iterable<Long> ids) {
        return findAll().stream()
        .filter(log -> StreamSupport.stream(ids.spliterator(), false)
            .anyMatch(id -> log.getId().equals(id)))
        .collect(Collectors.toList());
    }

    @Override
    public <S extends BlockedEntity> List<S> saveAll(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
        .map(block -> {
            if(block.getId() == null) block.setId(seq.incrementAndGet());
            return block;
        }).peek(block -> repository.put(block.getId(), block))
        .collect(Collectors.toList());
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends BlockedEntity> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends BlockedEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<BlockedEntity> entities) {
        deleteAll(entities);        
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        ids.forEach(repository::remove);
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll();
    }

    @Override
    public BlockedEntity getOne(Long id) {
        BlockedEntity result = getById(id);
        if(result == null) throw new EntityNotFoundException();
        else return result;
    }

    @Override
    public BlockedEntity getById(Long id) {
        return repository.get(id);
    }

    @Override
    public <S extends BlockedEntity> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends BlockedEntity> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Page<BlockedEntity> findAll(Pageable pageable) {
        List<BlockedEntity> result = findAll();
        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public <S extends BlockedEntity> S save(S entity) {
        if(entity.getId() == null) entity.setId(seq.incrementAndGet());
        repository.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<BlockedEntity> findById(Long id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return repository.get(id) != null;
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public void deleteById(Long id) {
        repository.remove(id);
    }

    @Override
    public void delete(BlockedEntity entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(repository::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends BlockedEntity> entities) {
        entities.forEach(log -> repository.remove(log.getId()));
        
    }

    @Override
    public void deleteAll() {
        repository.clear();
    }

    @Override
    public <S extends BlockedEntity> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends BlockedEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends BlockedEntity> long count(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends BlockedEntity> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<BlockedEntity> findFirstByUserIdAndQueryType(String userId, String queryType) {
        return findAll().stream()
                .filter(block -> block.getUserId().equals(userId))
                .filter(block -> block.getQueryType().equals(queryType))
                .findFirst();

    }

    @Override
    public boolean existsByUserIdAndBlockStartAfter(String userId, LocalDateTime lastDay) {
        return findAll().stream()
                .filter(block -> block.getUserId().equals(userId))
                .anyMatch(block -> block.getBlockStart().isAfter(lastDay));
    }

    @Override
    public Optional<BlockedEntity> findFirstByUserIdAndQueryTypeAndBlockEndAfterOrderByBlockEndDesc(String userId,
            String queryType, LocalDateTime currentDate) {
        return findAll().stream()
                .filter(block -> block.getUserId().equals(userId))
                .filter(block -> block.getQueryType().equals(queryType))
                .filter(block -> block.getBlockEnd().isAfter(currentDate))
                .sorted(comparatorByBlockEndDesc)
                .findFirst();
    }

    @Override
    public List<BlockedEntity> findByUserIdAndBlockEndAfterAndQueryType(String userId,
            LocalDateTime currentDate, String queryType) {
        return findAll().stream()
                .filter(block -> block.getUserId().equals(userId))
                .filter(block -> block.getBlockEnd().isAfter(currentDate))
                .filter(block -> block.getQueryType().equals(queryType))
                .collect(Collectors.toList());
    }


}
