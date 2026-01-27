package com.saburi.smartcoder.base.controller;

import com.saburi.smartcoder.base.ResponseObj;
import com.saburi.smartcoder.base.entities.Entity;
import com.saburi.smartcoder.base.exceptions.KnownException;
import com.saburi.smartcoder.base.storage.db.DbStore;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

public abstract class BaseController<T extends Entity, I, M> {

    protected abstract DbStore<T, I, M> getDbStore();


    @GetMapping
    public List<T> getAll() {
        return getDbStore().findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @GetMapping("mini")
    public List<M> getAllBy() {
        return getDbStore().findAllBy();
    }

    @GetMapping("{id}")
    public Optional<T> getById(@PathVariable I id) {
        return getDbStore().findById(id);
    }

    @PostMapping
    public ResponseObj<T> save(@RequestBody T model) {


         validate(model);

        return ResponseObj.<T>builder()
                .data(getDbStore().save(model))
                .message("Project saved successfully")
                .success(true)
                .build();
    }

    protected void validate( T model) {
        if (model.getId() != null &&
                getDbStore().existsById((I) model.getId())) {
            throw new KnownException("Already exists");
        }
    }


    @PostMapping("saveall")
    public ResponseObj<List<T>> saveAll(@RequestBody List<T> models) {
        return ResponseObj.<List<T>>builder()
                .data(getDbStore().saveAll(models))
                .message("Projects saved successfully")
                .success(true)
                .build();


    }

    @DeleteMapping("{id}")
    public ResponseObj deleteById(@PathVariable I id) {
        getDbStore().deleteById(id);
        return ResponseObj.<T>builder()
                .success(true)
                .message("Project deleted successfully")
                .build();
    }
    @PutMapping("{id}")

    public ResponseObj<T> update(@RequestBody T model, @PathVariable I id) {
      getDbStore().findById(id).orElseThrow(() -> new KnownException("Not found"));
        model.setId(id);
        return ResponseObj
                .<T>builder()
                .message("Project updated successfully")
                .data(getDbStore().save(model))
                .success(true)
                .build();
    }


}
