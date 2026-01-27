package com.saburi.smartcoder.settings;

import com.saburi.smartcoder.base.storage.db.DbStore;

import java.util.Optional;

public interface SettingRepo extends DbStore<Settings, Integer, Settings> {

    Optional<Settings> findByProperty(String property);
}
