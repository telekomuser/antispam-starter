package com.intech.utils.antispam.model.repository.inmemory;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.*;

import javax.persistence.EntityNotFoundException;

import com.intech.utils.antispam.model.entity.QueryLogEntity;
import com.intech.utils.antispam.model.repository.QueryLogRepository;
import com.intech.utils.antispam.model.type.ResultType;

@Repository
public class InmemoryQueryLogRepository implements  QueryLogRepository {

    private final Map<Long, QueryLogEntity> repository = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public long countAllByUserIdAndQueryTypeAndDateAddedAfterAndResult(String userId, String queryType, LocalDateTime time, ResultType result) {
        return findAll().stream()
                .filter(log -> log.getUserId().equals(userId))
                .filter(log -> log.getQueryType().equals(queryType))
                .filter(log -> log.getDateAdded().isAfter(time))
                .filter(log -> log.getResult().equals(result))
                .count();
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> arg0) {
        arg0.forEach(repository::remove);
        
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll();
        
    }

    @Override
    public void deleteAllInBatch(Iterable<QueryLogEntity> arg0) {
        deleteAll(arg0);
        
    }

    @Override
    public List<QueryLogEntity> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public List<QueryLogEntity> findAll(Sort arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends QueryLogEntity> List<S> findAll(Example<S> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends QueryLogEntity> List<S> findAll(Example<S> arg0, Sort arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<QueryLogEntity> findAllById(Iterable<Long> arg0) {
        return findAll().stream()
                .filter(log -> StreamSupport.stream(arg0.spliterator(), false)
                    .anyMatch(id -> log.getId().equals(id)))
                .collect(Collectors.toList());
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryLogEntity getById(Long arg0) {
        return repository.get(arg0);
    }

    @Override
    public QueryLogEntity getOne(Long arg0) {
        QueryLogEntity result = getById(arg0);
        if(result == null) throw new EntityNotFoundException();
        else return result;
    }

    @Override
    public <S extends QueryLogEntity> List<S> saveAll(Iterable<S> arg0) {
        return StreamSupport.stream(arg0.spliterator(), false).map(log -> {
            if(log.getId() == null) log.setId(seq.incrementAndGet());
            return log;
        }).peek(log -> repository.put(log.getId(), log))
        .collect(Collectors.toList());
    }

    @Override
    public <S extends QueryLogEntity> List<S> saveAllAndFlush(Iterable<S> arg0) {
       return saveAll(arg0);
    }

    @Override
    public <S extends QueryLogEntity> S saveAndFlush(S arg0) {
        return save(arg0);
    }

    @Override
    public Page<QueryLogEntity> findAll(Pageable arg0) {
        List<QueryLogEntity> result = findAll();
        return new PageImpl<>(result, arg0, result.size());
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public void delete(QueryLogEntity arg0) {
        repository.remove(arg0.getId());
        
    }

    @Override
    public void deleteAll() {
        repository.clear();
    }

    @Override
    public void deleteAll(Iterable<? extends QueryLogEntity> arg0) {
        arg0.forEach(log -> repository.remove(log.getId()));
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> arg0) {
        arg0.forEach(repository::remove);
    }

    @Override
    public void deleteById(Long arg0) {
        repository.remove(arg0);
    }

    @Override
    public boolean existsById(Long arg0) {
        return repository.get(arg0) != null;
    }

    @Override
    public Optional<QueryLogEntity> findById(Long arg0) {
        return Optional.ofNullable(repository.get(arg0));
    }

    @Override
    public <S extends QueryLogEntity> S save(S arg0) {
        if(arg0.getId() == null) arg0.setId(seq.incrementAndGet());
        repository.put(arg0.getId(), arg0);
        return arg0;
    }

    @Override
    public List<QueryLogEntity> findByUserId(String userId) {
        return repository.values().stream()
                .filter(query -> query.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public <S extends QueryLogEntity> long count(Example<S> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends QueryLogEntity> boolean exists(Example<S> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends QueryLogEntity> Page<S> findAll(Example<S> arg0, Pageable arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends QueryLogEntity> Optional<S> findOne(Example<S> arg0) {
        throw new UnsupportedOperationException();
    }
}
