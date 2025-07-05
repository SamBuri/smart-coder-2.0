package com.saburi.smartcorder.base.storage;

import com.saburi.smartcorder.base.Model;
import com.saburi.smartcorder.base.exceptions.KnownException;

import java.util.List;
import java.util.Optional;

public interface StorageService<T extends Model, I> {
    T save(T t) throws KnownException;

    List<T> saveAll(List<T> entities) throws KnownException;

    Optional<T> findById(I id);

    List<T> findAll();

    boolean deleteById(I id);


    void deleteAll();


    boolean existsById(I id);

    long count();
    // Add other necessary methods
}
