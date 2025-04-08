package com.lyflexi.feignx.listener.app;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiManager;
import com.lyflexi.feignx.listener.project.PsiClassGitChangeListener;

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