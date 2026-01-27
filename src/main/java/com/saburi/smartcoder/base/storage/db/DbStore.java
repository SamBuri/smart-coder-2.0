package com.saburi.smartcoder.base.storage.db;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface DbStore<T, I, M>  extends JpaRepository<T, I> {
    List<M> findAllBy();
    List<T> findAll(Sort sort);
}

