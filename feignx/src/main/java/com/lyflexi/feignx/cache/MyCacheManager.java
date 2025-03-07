package com.lyflexi.feignx.cache;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.lyflexi.feignx.model.ControllerInfo;
import com.lyflexi.feignx.utils.JavaSourceFileUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 取消projectControllerCacheMap缓存
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:52
 */
public class MyCacheManager {

    private MyCacheManager() {
    }

    // 缓存controller接口数据使用
    private static Map<String, List<Pair<String, ControllerInfo>>> projectCacheMap = new HashMap<>();

    // 缓存controller接口数据,主键是方法的完全限定名（fully qualified name）,值为接口路径
//    private static Map<String, Map<String, String>> projectControllerCacheMap = new HashMap<>();

    // 缓存Feign接口数据使用
    private static Map<String, List<Pair<String, ControllerInfo>>> projectFeignCacheMap = new HashMap<>();

    public static void clear() {
        projectCacheMap = null;
        projectFeignCacheMap = null;
//        projectControllerCacheMap = null;
    }

    public static List<Pair<String, ControllerInfo>> getCacheData(Project project) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        return projectCacheMap.get(projectId);
    }

    public static void setCacheData(Project project, List<Pair<String, ControllerInfo>> controllerCacheData) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        projectCacheMap.put(projectId, controllerCacheData);
    }


    public static List<Pair<String, ControllerInfo>> getFeignCacheData(Project project) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        return projectFeignCacheMap.get(projectId);
    }

    public static void setFeignCacheData(Project project, List<Pair<String, ControllerInfo>> feignCacheData) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        projectFeignCacheMap.put(projectId, feignCacheData);
    }

    //    public static String getControllerPath(PsiMethod controllerMethod) {
//        String basePath = controllerMethod.getProject().getBasePath();
//        if (projectControllerCacheMap.get(basePath) == null) {
//            if(projectCacheMap.get(basePath) == null){
//                JavaSourceFileUtil.scanControllerPaths(controllerMethod.getProject());
//            }
//            Map<String, String> collect = projectCacheMap.get(basePath).stream()
//                    .map(Pair::getRight)
//                    .collect(Collectors.toMap(controllerInfo -> getKey(controllerInfo.getMethod()),
//                            ControllerInfo::getPath,
//                            (a1, a2) -> a1)
//                    );
//            projectControllerCacheMap.put(basePath, collect);
//        }
//        return projectControllerCacheMap.get(basePath).get(getKey(controllerMethod));
//
//    }

    /**
     *
     * @param controllerMethod
     * @return
     */
    public static String getControllerPath(PsiMethod controllerMethod) {
        String basePath = controllerMethod.getProject().getBasePath();
        if (projectCacheMap.get(basePath) == null) {
            JavaSourceFileUtil.scanControllerPaths(controllerMethod.getProject());
        }
        Map<String, String> collect = projectCacheMap.get(basePath).stream()
                .map(Pair::getRight)
                .collect(Collectors.toMap(controllerInfo -> getKey(controllerInfo.getMethod()),
                        ControllerInfo::getPath,
                        (a1, a2) -> a1)
                );
        return collect.get(getKey(controllerMethod));

    }

    @NotNull
    private static String getKey(PsiMethod controllerMethod) {
        return controllerMethod.getContainingClass().getQualifiedName() + controllerMethod.getName();
    }
}