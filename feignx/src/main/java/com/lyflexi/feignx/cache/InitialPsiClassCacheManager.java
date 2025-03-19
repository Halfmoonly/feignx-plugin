package com.lyflexi.feignx.cache;

import com.intellij.psi.PsiClass;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @Author: hmly
 * @Date: 2025/3/15 17:21
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: 项目中的所有Java源文件，因为要做两次全盘扫描
 * eg. 一次是扫描全盘的Controller类，
 * eg. 一次是扫描全盘的Feign接口类
 *
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
     * @return
     */
    public static InitialPsiClassCacheManager getInstance() {
        return INSTANCE;
    }

    /**
     *
     * @param projectId
     * @param psiClasses
     */
    public void init(String projectId, List<PsiClass> psiClasses) {
        InitialPsiClassCacheMap.put(projectId, psiClasses);
    }

    /**
     * 获取全量缓存PsiClass
     * @param projectId
     * @return
     */
    public List<PsiClass> queryAllClassesCache(String projectId) {
        return InitialPsiClassCacheMap.get(projectId);
    }


}
