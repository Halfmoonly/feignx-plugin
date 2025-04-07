package com.lyflexi.feignx.listener;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.ProjectManager;

public class FeignXPluginInitializer implements ApplicationComponent {

    @Override
    public void initComponent() {
        // 注册监听器
        ProjectManager.getInstance().addProjectManagerListener(new CacheCleanListener());
    }

    @Override
    public void disposeComponent() {
        // 清理资源
    }

    @Override
    public String getComponentName() {
        return "FeignXPluginInitializer";
    }
}