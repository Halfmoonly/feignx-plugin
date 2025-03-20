package com.lyflexi.feignx.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeListener;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: hmly
 * @Date: 2025/3/20 21:23
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description:
 */
public class PluginStartupActivity implements StartupActivity.DumbAware {
    @Override
    public void runActivity(@NotNull Project project) {
        PsiTreeChangeListener listener = new PsiClassChangeListener(project);
        PsiManager.getInstance(project).addPsiTreeChangeListener(listener, project);
    }
}
