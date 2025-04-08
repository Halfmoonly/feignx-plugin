package com.lyflexi.feignx.listener.project;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.lyflexi.feignx.cache.InitialPsiClassCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * git pull之后，或者git pull origin other-branch之后
 * 
 * 由于会触发psi索引树的变更，一旦存在与feign接口或者controller接口相关的改动，就会导致gutter失效
 */
@Slf4j
public class PsiClassGitChangeListener implements PsiTreeChangeListener {
    Project project;
    public PsiClassGitChangeListener(Project project){
        this.project = project;
    }

    //aftered事件
    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        handleAddEvent(event);
    }

    @Override public void childRemoved(PsiTreeChangeEvent event) {
        
    }

    /**
     * 监听被git替换的类
     * @param event the event object describing the change.
     */
    @Override public void childReplaced(PsiTreeChangeEvent event) {
        handleModifyEvent(event);
    }

    /**
     * 监听被git修改的类
     * @param event the event object describing the change.
     */
    @Override public void childrenChanged(PsiTreeChangeEvent event) {
        handleModifyEvent(event);
    }

    @Override public void propertyChanged(PsiTreeChangeEvent event) {
        handleModifyEvent(event);
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
        handleModifyEvent(event);
    }

    //before事件
    @Override public void beforeChildAddition(PsiTreeChangeEvent event) {}
    @Override public void beforeChildRemoval(PsiTreeChangeEvent event) {}
    @Override public void beforeChildrenChange(PsiTreeChangeEvent event) {}

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override public void beforeChildReplacement(PsiTreeChangeEvent event) {}

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {

    }
    /**
     * 处理修改事件变动,先删除再新增
     * @param event
     */
    private void handleModifyEvent(@NotNull PsiTreeChangeEvent event) {
        PsiElement child = event.getChild();
        if (child instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) child;
            // 监听到新增类了，添加到缓存中或触发重新扫描
            System.out.println("监听到psi引擎变动的psiclass类: {}" + psiClass.getQualifiedName());
            // 这里调用的 ProjectUtils 中缓存添加逻辑
            InitialPsiClassCacheManager.coverByPsiListener(project, psiClass);
        }
    }

    /**
     * 处理新增事件变动
     * @param event
     */
    private void handleAddEvent(@NotNull PsiTreeChangeEvent event) {
        PsiElement child = event.getChild();
        if (child instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) child;
            // 监听到新增类了，添加到缓存中或触发重新扫描
            System.out.println("监听到psi引擎变动的psiclass类: {}" + psiClass.getQualifiedName());
            // 这里调用的 ProjectUtils 中缓存添加逻辑
            InitialPsiClassCacheManager.addByPsiListener(project, psiClass);
        }
    }
}
