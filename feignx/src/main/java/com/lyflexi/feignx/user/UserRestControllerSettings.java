package com.lyflexi.feignx.user;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;

/**
 * @Author: liuyanoutsee@outlook.com
 * @Date: 2025/4/2 21:17
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: 用户配置存储在本地的FeignClient-Assistant-Settings.xml
 */
@Service(Service.Level.APP)
@State(name = "ControllerPluginSettings", storages = @Storage("FeignClient-Assistant-Settings.xml"))
public final class UserRestControllerSettings implements PersistentStateComponent<UserRestControllerSettings.State> {

    private State state = new State();

    public static UserRestControllerSettings getInstance() {
        return ApplicationManager.getApplication().getService(UserRestControllerSettings.class);
    }

    public static class State {
        public boolean iconEnabled = true;
    }

    @Override
    public @Nullable State getState() {
        return state;
    }

    @Override
    public void loadState(@Nullable State state) {
        if (state != null) {
            this.state = state;
        }
    }

    public boolean isIconEnabled() {
        return state.iconEnabled;
    }

    public void setIconEnabled(boolean enabled) {
        state.iconEnabled = enabled;
    }
}
