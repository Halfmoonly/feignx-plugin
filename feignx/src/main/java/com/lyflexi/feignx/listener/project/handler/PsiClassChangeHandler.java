package com.lyflexi.feignx.listener.project.handler;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

/**
 * PsiClass变动监听处理器
 */
public interface PsiClassChangeHandler {
    /**
     *
     * @param project 当前项目
     * @param psiClass 发生变动的类
     */
    public void handle(Project project, PsiClass psiClass);
}
