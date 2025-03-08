package com.lyflexi.feignx.cache;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiMethod;
import com.lyflexi.feignx.model.HttpMappingInfo;
import com.lyflexi.feignx.utils.JavaResourceUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 缓存管理器
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:52
 */
public class CacheManager {

    private CacheManager() {
    }

    // 缓存接口数据使用(controller)  projectid - map
    private static Map<String, List<Pair<String, HttpMappingInfo>>> projectControllerCacheMap = new HashMap<>();
    // 缓存Feign接口数据使用     projectid - map
    private static Map<String, List<Pair<String, HttpMappingInfo>>> projectFeignCacheMap = new HashMap<>();



//    //缓存controller接口数据,主键是方法的完全限定名（fully qualified name）,值为接口路径
//    private static Map<String, Map<String, String>> projectControllerCacheMap = new HashMap<>();

//    public static void clear() {
//        projectControllerCacheMap = null;
//        projectFeignCacheMap = null;
////        projectControllerCacheMap = null;
//    }

    //清除所有打开项目的缓存
    public static void clear() {
        Project[] openProjects = getOpenProjects();
        for (Project project : openProjects) {
            // 扫描项目中的Java源文件
            CacheManager.setControllerCacheData(project,null);
            CacheManager.setFeignCacheData(project,null);
        }
    }

    /**
     * 清除指定项目的缓存
     * @param project
     */
    public static void clear(Project project) {
        // 扫描项目中的Java源文件
        CacheManager.setControllerCacheData(project,null);
        CacheManager.setFeignCacheData(project,null);
    }
    /**
     * 获取所有打开的项目列表
     *
     * @return {@link Project[]}
     */
    private static Project[] getOpenProjects() {
        // 获取ProjectManager实例
        ProjectManager projectManager = ProjectManager.getInstance();
        // 获取所有打开的项目列表
        return projectManager.getOpenProjects();
    }


    public static List<Pair<String, HttpMappingInfo>> getCacheData(Project project) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        return projectControllerCacheMap.get(projectId);
    }

    public static void setControllerCacheData(Project project, List<Pair<String, HttpMappingInfo>> controllerCacheData) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        projectControllerCacheMap.put(projectId, controllerCacheData);
    }


    public static List<Pair<String, HttpMappingInfo>> getFeignCacheData(Project project) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        return projectFeignCacheMap.get(projectId);
    }

    public static void setFeignCacheData(Project project, List<Pair<String, HttpMappingInfo>> feignCacheData) {
        String projectId = project.getBasePath(); // 以项目路径作为唯一标识符
        projectFeignCacheMap.put(projectId, feignCacheData);
    }

//    public static String getControllerPath(PsiMethod controllerMethod) {
//        String basePath = controllerMethod.getProject().getBasePath();
//        if (projectControllerCacheMap.get(basePath) == null) {
//            if (projectCacheMap.get(basePath) == null) {
//                JavaSourceFileUtil.scanControllerPaths(controllerMethod.getProject());
//            }
//            Map<String, String> collect = projectCacheMap.get(basePath).stream()
//                    .map(Pair::getRight)
//                    .collect(Collectors.toMap(controllerInfo -> getKey(controllerInfo.getMethod()),
//                            HttpMappingInfo::getPath,
//                            (a1, a2) -> a1)
//                    );
//            projectControllerCacheMap.put(basePath, collect);
//        }
//        return projectControllerCacheMap.get(basePath).get(getKey(controllerMethod));
//    }

    /**
     *获取controller缓存
     * @param controllerMethod
     * @return
     */
    public static String getControllerPath(PsiMethod controllerMethod) {
        String basePath = controllerMethod.getProject().getBasePath();
        if (projectControllerCacheMap.get(basePath) == null) {
            JavaResourceUtil.scanControllerPaths(controllerMethod.getProject());
        }
        Map<String, String> collect = projectControllerCacheMap.get(basePath).stream()
                .map(Pair::getRight)
                .collect(Collectors.toMap(controllerInfo -> getKey(controllerInfo.getMethod()),
                        HttpMappingInfo::getPath,
                        (a1, a2) -> a1)
                );
        return collect.get(getKey(controllerMethod));

    }

    /**
     * 获取feign缓存
     * @param feignMethod
     * @return
     */
    public static String getFeignPath(PsiMethod feignMethod) {
        String basePath = feignMethod.getProject().getBasePath();
        if (projectFeignCacheMap.get(basePath) == null) {
            JavaResourceUtil.scanFeignInterfaces(feignMethod.getProject());
        }
        Map<String, String> collect = projectFeignCacheMap.get(basePath).stream()
                .map(Pair::getRight)
                .collect(Collectors.toMap(feignInfo -> getKey(feignInfo.getMethod()),
                        HttpMappingInfo::getPath,
                        (a1, a2) -> a1)
                );
        return collect.get(getKey(feignMethod));

    }

    @NotNull
    private static String getKey(PsiMethod controllerMethod) {
        return controllerMethod.getContainingClass().getQualifiedName() + controllerMethod.getName();
    }
}