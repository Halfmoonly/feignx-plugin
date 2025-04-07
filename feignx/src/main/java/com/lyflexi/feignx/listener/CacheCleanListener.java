package com.lyflexi.feignx.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class CacheCleanListener implements ProjectManagerListener {

    @Override
    public void projectClosing(@NotNull Project project) {
        // 在这里执行清除缓存的操作
        clearCustomCache(project);
    }

    private void clearCustomCache(Project project) {
        Clear.clear(project);
    }
}