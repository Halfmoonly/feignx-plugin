package com.lyflexi.feignx.recover;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.cache.InitialPsiClassCacheManager;
import com.lyflexi.feignx.utils.AnnotationParserUtils;

/**
 * @Author: liuyanoutsee@outlook.com
 * @Date: 2025/4/3 22:00
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description:
 *
 * PsiElement 的生命周期受 IDE 缓存和 PSI 树变化影响，当元素失效（isValid() 返回 false）时，直接访问会报错。但 SmartPsiElementPointer 会追踪元素变化并在可能的情况下自动恢复：
 *
 * 元素未被彻底删除（如重命名、重构）,比如收到FeignClassIconProvider影响
 *
 * 重新解析文档后，仍能匹配到原始元素
 */
public class SmartPsiElementRecover {
    /**
     * 恢复失效的类
     * @param project
     * @param psiClass
     * @return
     */
    public static PsiClass recoverClass(Project project, PsiClass psiClass) {
        if (psiClass == null) {
            return null;
        }
        // 创建 SmartPsiElementPointer
        SmartPsiElementPointer<PsiClass> classPointer = SmartPointerManager.getInstance(project).createSmartPsiElementPointer(psiClass);

        // 验证 psiClass 是否有效
        if (!psiClass.isValid()) {
            // 尝试恢复
            psiClass = classPointer.getElement();
        }
        // 恢复失败则返回null
        if (null==psiClass||!psiClass.isValid()) {
            return null;
        }
        //恢复某个class缓存
        InitialPsiClassCacheManager.putPsiClassCache(project, psiClass);
        return psiClass;
    }
    /**
     * 恢复失效的方法
     * @param project
     * @param method
     * @return
     */
    public static PsiMethod recoverMethod(Project project, PsiMethod method) {
        if (method == null) {
            return null;
        }
        // 创建 SmartPsiElementPointer
        SmartPsiElementPointer<PsiMethod> methodPointer = SmartPointerManager.getInstance(project).createSmartPsiElementPointer(method);

        // 验证 psiClass 是否有效
        if (!method.isValid()) {
            // 尝试恢复
            method = methodPointer.getElement();
        }
        // 恢复失败则返回null
        if (null==method||!method.isValid()) {
            return null;
        }
        //恢复某个method缓存, feign接口的方法
        if (AnnotationParserUtils.isElementWithinFeign(method)) {
            BilateralCacheManager.setOrCoverFeignCache(method);
        }
        //恢复某个method缓存, controller接口的方法
        if (AnnotationParserUtils.isElementWithinController(method)) {
            BilateralCacheManager.setOrCoverControllerCache(method);
        }
        return method;
    }
}
