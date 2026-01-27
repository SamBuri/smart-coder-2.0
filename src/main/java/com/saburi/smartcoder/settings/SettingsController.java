package com.saburi.smartcoder.settings;

import com.saburi.smartcoder.base.controller.BaseController;
import com.saburi.smartcoder.base.exceptions.KnownException;
import com.saburi.smartcoder.base.storage.db.DbStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/settings")
@RequiredArgsConstructor
public class SettingsController extends BaseController<Settings, Integer, Settings> {
    private final SettingRepo settingRepo;

    @Override
    protected DbStore<Settings, Integer, Settings> getDbStore() {
        return settingRepo;
    }

    @Override
    protected void validate(Settings model) {
      Settings settings =  settingRepo.findByProperty(model.getProperty()).orElseGet(null);
      if (settings != null) {
          model.setId(settings.getId());
      }
    }

    @GetMapping("property/{property}")
    public Optional<Settings> findByProperty(@PathVariable String property) {
        return settingRepo.findByProperty(property);
    }


}
