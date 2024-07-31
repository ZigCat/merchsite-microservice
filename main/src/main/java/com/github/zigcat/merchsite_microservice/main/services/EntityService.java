package com.github.zigcat.merchsite_microservice.main.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class EntityService<T> {
    protected final JpaRepository<T, Integer> repository;
    private final Class<T> type;

    public EntityService(JpaRepository<T, Integer> repository,
                         Class<T> type) {
        this.repository = repository;
        this.type = type;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public List<T> getAll(){
        return repository.findAll();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<T> getById(Integer id){
        return repository.findById(id);
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public void delete(Integer id){
        T object = getById(id)
                .orElseThrow(() -> new EntityNotFoundException(type.getName()+" not found"));
        repository.deleteById(id);
    }
}
