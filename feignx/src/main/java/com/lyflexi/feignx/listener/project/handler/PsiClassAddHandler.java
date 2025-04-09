package com.lyflexi.feignx.listener.project.handler;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.lyflexi.feignx.cache.InitialPsiClassCacheManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通过PsiListener新增一个psiclass缓存
 */
public class PsiClassAddHandler implements PsiClassChangeHandler{
    // 获取单例PsiClass缓存管理器
    private static final InitialPsiClassCacheManager initialPsiClassCacheManager = InitialPsiClassCacheManager.getInstance();

    @Override
    public void handle(Project project, PsiClass psiClass) {
        String projectId = project.getBasePath();
        List<PsiClass> psiClasses = initialPsiClassCacheManager.queryCurProjectPsiClassesCache(projectId);
        if (psiClasses == null) {
            return;
        }

        // java.lang.Throwable: Smart pointers must not be created during PSI changes
        if (null == psiClass || !psiClass.isValid()) {
            return;
        }

        //新增逻辑
        List<String> alreadyClass = psiClasses.stream().map(PsiClass::getQualifiedName).collect(Collectors.toList());
        if (!alreadyClass.contains(psiClass.getQualifiedName())) {
            psiClasses.add(psiClass);
        }
        //覆盖全局psiclass缓存
        initialPsiClassCacheManager.coverCurProjectPsiClassCache(projectId, psiClasses);
    }
}
