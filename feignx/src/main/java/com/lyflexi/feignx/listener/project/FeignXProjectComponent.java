package com.lyflexi.feignx.listener.project;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiManager;

public class FeignXProjectComponent implements ProjectComponent {
    @Override
    public void projectOpened() {
        // 注册监听器
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : openProjects) {
            // 对每个项目进行git pull监听处理
            PsiManager.getInstance(project).removePsiTreeChangeListener(new PsiClassGitChangeListener(project));
            PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiClassGitChangeListener(project));
        }
    }
}
