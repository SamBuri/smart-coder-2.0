package com.saburi.smartcoder.settings;

import com.saburi.smartcoder.base.entities.AutoIncEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name="settings", uniqueConstraints = {@UniqueConstraint(name="uk_settings_key", columnNames = {"property"})})
public class Settings extends AutoIncEntity {
    @Column(name="property")
    private String property;
    @Column(name="property_value")
    private String propertyValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Objects.equals(property, settings.property);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(property);
    }

    @Override
    public String getDisplay() {
        return String.format("%s: %s", property, propertyValue);
    }


}
