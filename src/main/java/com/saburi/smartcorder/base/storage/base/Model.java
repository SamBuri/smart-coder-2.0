/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcorder.base.storage.base;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author Hp
 */
@SuperBuilder
@Data
public  abstract class Model<T> {
     protected T id;

    public abstract String getDisplay();
    
    
  
}
