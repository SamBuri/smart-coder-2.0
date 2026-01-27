package com.saburi.smartcoder.base.entities;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class AutoIncEntity extends Entity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // BEST for H2
    @Column(updatable = false, nullable = false)
    protected Integer id;

    @Override
    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }
}
