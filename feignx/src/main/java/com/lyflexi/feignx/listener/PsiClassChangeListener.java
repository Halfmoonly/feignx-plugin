package com.lyflexi.feignx.listener;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.lyflexi.feignx.cache.InitialPsiClassCacheManager;
import com.lyflexi.feignx.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: hmly
 * @Date: 2025/3/20 21:15
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: 感知新的类创建
 */
@Slf4j
public class PsiClassChangeListener implements PsiTreeChangeListener {
    private final Project project;

    public PsiClassChangeListener(Project project) {
        this.project = project;
    }

    /**
     *
     * @param event the event object describing the change.
     */
    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        PsiElement child = event.getChild();
        if (child instanceof PsiClass ) {
            PsiClass psiClass = (PsiClass) child;
            // 监听到新增类了，添加到缓存中或触发重新扫描
            System.out.println("监听到新增的psiclass类: {}" + psiClass.getQualifiedName());
            // 这里调用的 ProjectUtils 中缓存添加逻辑
            InitialPsiClassCacheManager.putPsiClassCache(project, psiClass);
        }
    }

    // 你也可以监听其他事件如 childRemoved、childReplaced、childrenChanged 等
    @Override public void beforeChildAddition(PsiTreeChangeEvent event) {}
    @Override public void beforeChildRemoval(PsiTreeChangeEvent event) {}
    @Override public void childRemoved(PsiTreeChangeEvent event) {}
    @Override public void childReplaced(PsiTreeChangeEvent event) {}
    @Override public void childrenChanged(PsiTreeChangeEvent event) {}

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override public void propertyChanged(PsiTreeChangeEvent event) {}
    @Override public void beforeChildrenChange(PsiTreeChangeEvent event) {}

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override public void beforeChildReplacement(PsiTreeChangeEvent event) {}

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {

    }

}
