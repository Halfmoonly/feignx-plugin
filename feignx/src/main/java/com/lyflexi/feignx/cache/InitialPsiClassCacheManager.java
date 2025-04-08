package com.lyflexi.feignx.cache;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.lyflexi.feignx.recover.SmartPsiElementRecover;
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
     * @param projectId
     * @param psiClasses
     */
    public void init(String projectId, List<PsiClass> psiClasses) {
        InitialPsiClassCacheMap.put(projectId, psiClasses);
    }

    /**
     * 获取全量缓存PsiClass
     *
     * @param projectId
     * @return
     */
    public List<PsiClass> queryAllClassesCache(String projectId) {
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
     * 通过PsiListener覆盖一个psiclass缓存，先删除再新增
     *
     * @param project
     * @param psiClass
     */
    public static void coverByPsiListener(Project project, PsiClass psiClass) {
        String projectId = project.getBasePath();
        List<PsiClass> psiClasses = InitialPsiClassCacheMap.get(projectId);
        if (psiClasses == null) {
            return;
        }
        // 增加有效性校验
        if (null == psiClass) {
            return;
        }
        // 验证 psiClass 是否有效
        if (!psiClass.isValid()) {
            // 尝试恢复
            psiClass = SmartPsiElementRecover.genericRecoverClass(project,psiClass);
        }
        // 恢复失败则返回
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
        InitialPsiClassCacheMap.put(projectId, psiClasses);
    }

    /**
     * 通过PsiListener新增一个psiclass缓存
     *
     * @param project
     * @param psiClass
     */
    public static void addByPsiListener(Project project, PsiClass psiClass) {
        String projectId = project.getBasePath();
        List<PsiClass> psiClasses = InitialPsiClassCacheMap.get(projectId);
        if (psiClasses == null) {
            return;
        }
        // 验证 psiClass 是否有效
        if (!psiClass.isValid()) {
            // 尝试恢复
            psiClass = SmartPsiElementRecover.genericRecoverClass(project,psiClass);
        }
        // 恢复失败则返回
        if (null == psiClass || !psiClass.isValid()) {
            return;
        }
        //新增逻辑
        List<String> alreadyClass = psiClasses.stream().map(PsiClass::getQualifiedName).collect(Collectors.toList());
        if (!alreadyClass.contains(psiClass.getQualifiedName())) {
            psiClasses.add(psiClass);
        }
        //覆盖全局psiclass缓存
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
