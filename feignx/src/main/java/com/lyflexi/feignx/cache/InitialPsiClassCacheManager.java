package com.lyflexi.feignx.cache;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: hmly
 * @Date: 2025/3/15 17:21
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: 项目中的所有Java源文件，因为要做两次全盘扫描
 * eg. 一次是扫描全盘的Controller类，
 * eg. 一次是扫描全盘的Feign接口类
 * <p>
 * 缓存起来，只做一次全盘扫描即可
 */
public class InitialPsiClassCacheManager {
    private static final InitialPsiClassCacheManager INSTANCE = new InitialPsiClassCacheManager();

    //projectId(basePath) -> List<PsiClass>
    private static Map<String, List<PsiClass>> InitialPsiClassCacheMap = new HashMap<>();

    private InitialPsiClassCacheManager() {
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static InitialPsiClassCacheManager getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化当前项目缓存
     * @param projectId
     * @param psiClasses
     */
    public void initCurProjectPsiClassCache(String projectId, List<PsiClass> psiClasses) {
        InitialPsiClassCacheMap.put(projectId, psiClasses);
    }

    /**
     * 覆盖当前项目缓存
     * @param projectId
     * @param psiClasses
     */
    public void coverCurProjectPsiClassCache(String projectId, List<PsiClass> psiClasses) {
        InitialPsiClassCacheMap.put(projectId, psiClasses);
    }

    /**
     * 获取当前项目的全量缓存PsiClass
     *
     * @param projectId
     * @return
     */
    public List<PsiClass> queryCurProjectPsiClassesCache(String projectId) {
        return InitialPsiClassCacheMap.get(projectId);
    }

    /**
     * 新增一个psiclass缓存
     *
     * @param project
     * @param psiClass
     */
    public static void putPsiClassCache(Project project, PsiClass psiClass) {
        String projectId = project.getBasePath();
        List<PsiClass> psiClasses = InitialPsiClassCacheMap.get(projectId);
        if (psiClasses == null) {
            psiClasses = new ArrayList<>();
        }
        psiClasses.add(psiClass);
        InitialPsiClassCacheMap.put(projectId, psiClasses);
    }

    /**
     * 清理psiclass缓存
     *
     * @param project
     */
    public void clear(Project project) {
        String projectId = project.getBasePath();
        InitialPsiClassCacheMap.remove(projectId);
    }
}
