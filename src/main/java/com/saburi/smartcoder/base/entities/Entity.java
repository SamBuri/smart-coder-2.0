/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.base.entities;

import jakarta.persistence.MappedSuperclass;

/**
 * @author Sam Buriima
 */


@MappedSuperclass

//@SuperBuilder
public abstract class Entity<I> {

    public abstract I getId();

    public abstract void setId(I id);

    public abstract String getDisplay();


}
