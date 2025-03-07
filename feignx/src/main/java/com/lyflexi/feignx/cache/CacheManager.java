package com.lyflexi.feignx.cache;

import com.intellij.psi.PsiMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: hmly
 * @Date: 2025/3/7 20:40
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description:
 */
public class CacheManager {
    private static final Map<PsiMethod, String> controllerCache = new HashMap<>();
    private static final Map<PsiMethod, String> feignCache = new HashMap<>();

    public static void cacheController(PsiMethod method, String path) {
        if (method != null && StringUtils.isNotBlank(path)) {
            controllerCache.put(method, path);
        }
    }

    public static String getControllerPath(PsiMethod method) {
        return controllerCache.get(method);
    }

    public static void cacheFeign(PsiMethod method, String path) {
        if (method != null && StringUtils.isNotBlank(path)) {
            feignCache.put(method, path);
        }
    }

    public static String getFeignPath(PsiMethod method) {
        return feignCache.get(method);
    }

    public static void clearAll() {
        controllerCache.clear();
        feignCache.clear();
    }

    /**
     * controller->feign执行扫描之前，清理所有的目标feign缓存，重新扫描feign接口
     *
     * 原因是用户会修改目标feign类
     */
    public static void clearAllFeigns() {
        feignCache.clear();
    }
    /**
     * feign->controller执行扫描之前，清理所有的目标controller缓存，重新扫描controller接口
     *
     * 原因是用户会修改目标controller类
     */
    public static void clearAllControllers() {
        controllerCache.clear();
    }

//    /**
//     * 在当前Feign跳去别的Controller之后，顺带清理当前Feign缓存
//     *
//     * 原因在于当前Feign可能用户会更新其中的接口
//     * @param method
//     */
//    public static void clearLocalFeign(PsiMethod method) {
//        feignCache.remove(method);
//    }
//    /**
//     * 在当前Controller跳去别的Feign之后，顺带清理当前Controller缓存
//     *
//     * 原因在于当前Controller可能用户会更新其中的接口
//     * @param method
//     */
//    public static void clearLocalController(PsiMethod method) {
//        controllerCache.remove(method);
//    }
}
