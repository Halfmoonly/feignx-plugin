package com.lyflexi.feignx.listener.project.handler;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.lyflexi.feignx.cache.InitialPsiClassCacheManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通过PsiListener覆盖一个psiclass缓存，先删除再新增
 */
public class PsiClassModifyHandler implements PsiClassChangeHandler{
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

        Iterator<PsiClass> iterator = psiClasses.iterator();
        //覆盖逻辑
        while (iterator.hasNext()) {
            PsiClass next = iterator.next();
            if (StringUtils.equals(psiClass.getQualifiedName(), next.getQualifiedName())) {
                iterator.remove();
                psiClasses.add(psiClass);
                break;
            }
        }
        //覆盖全局psiclass缓存
        initialPsiClassCacheManager.coverCurProjectPsiClassCache(projectId, psiClasses);
    }
}
