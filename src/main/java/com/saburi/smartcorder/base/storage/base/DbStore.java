package com.saburi.smartcorder.base.storage.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DbStore<T, I>  extends JpaRepository<T, I> {
}
