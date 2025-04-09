package com.lyflexi.feignx.utils;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.cache.InitialPsiClassCacheManager;
import com.lyflexi.feignx.entity.HttpMappingInfo;
import com.lyflexi.feignx.enums.SpringCloudClassAnnotation;
import com.lyflexi.feignx.recover.SmartPsiElementRecover;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: hmly
 * @Date: 2025/3/14 20:51
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: feign类扫描工具类
 */
public class FeignClassScanUtils {
    // 初始化PsiClass缓存管理器
    private static final InitialPsiClassCacheManager initialPsiClassCacheManager = InitialPsiClassCacheManager.getInstance();

    /**
     * 当前controller，扫描待跳转的所有目标Feign
     *
     * @param controllerMethod psi方法
     * @return {@link List}<{@link PsiElement}>
     */
    public static List<PsiElement> process(PsiMethod controllerMethod) {
        List<PsiElement> elementList = new ArrayList<>();
        // 获取当前项目
        Project project = controllerMethod.getProject();
        List<HttpMappingInfo> feignInfos = scanFeignInterfaces(project);
        if (feignInfos != null) {
            // 遍历 Controller 类的所有方法
            for (HttpMappingInfo feignInfo : feignInfos) {
                if (match2F(feignInfo, controllerMethod)) {
                    elementList.add(feignInfo.getPsiMethod());
                }
            }
        }

        return elementList;
    }

    /**
     * 用当前controller接口匹配目标feign接口
     *
     * @param feignInfo
     * @param controllerMethod
     * @return
     */
    private static boolean match2F(HttpMappingInfo feignInfo, PsiMethod controllerMethod) {
        HttpMappingInfo controllerCache = BilateralCacheManager.getOrSetControllerCache(controllerMethod);
        if (Objects.isNull(controllerCache)) {
            return false;
        }
        String path = controllerCache.getPath();
        return StringUtils.equals(path, feignInfo.getPath());
    }


    /**
     * 扫描Feign接口信息添加到缓存里面
     *
     * @param project 项目
     * @return {@link List}<{@link HttpMappingInfo}>
     */
    public static List<HttpMappingInfo> scanFeignInterfaces(Project project) {
        // 检查是否在 Dumb 模式下，以避免在项目构建期间执行代码
        if (DumbService.isDumb(project)) {
            return Collections.emptyList();
        }

        PsiManager psiManager = PsiManager.getInstance(project);
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
        PsiPackage rootPackage = JavaPsiFacade.getInstance(psiManager.getProject()).findPackage("");

        // 获取项目ID
        String projectId = project.getBasePath();

        List<PsiClass> javaFiles = initialPsiClassCacheManager.queryCurProjectPsiClassesCache(projectId);

        if (CollectionUtils.isEmpty(javaFiles)) {
            javaFiles = ProjectUtils.scanAllClasses(rootPackage, searchScope);
            initialPsiClassCacheManager.initCurProjectPsiClassCache(projectId, javaFiles);
        }

        //Feign接口缓存查询
        Map<String, HttpMappingInfo> feignCaches = BilateralCacheManager.queryFeignCaches(project);

        if (MapUtils.isNotEmpty(feignCaches)) {
            return new ArrayList<>(feignCaches.values());
        }
        //获取项目中的所有Feign源文件
        List<HttpMappingInfo> feignInfos = new ArrayList<>();
        //创建全部的Feign接口信息
        for (PsiClass psiClass : javaFiles) {
            // 校验 psiClass 的有效性，毕竟有可能psiClass是从缓存中获取的，但是被RestClassIconProvider修改了
            if (null == psiClass || !psiClass.isValid()) {
                continue;
            }
            feignInfos.addAll(feignsOfPsiClass(psiClass));
        }
        // 将结果添加到缓存中
        BilateralCacheManager.initFeignCaches(project, feignInfos);

        return feignInfos;
    }

    /**
     * 获取当前feign类中的所有方法对应的HttpMappingInfo
     *
     * @param psiClass
     * @return
     */
    public static List<HttpMappingInfo> feignsOfPsiClass(PsiClass psiClass) {
        List<HttpMappingInfo> rs = new ArrayList<>();
        if (AnnotationParserUtils.isFeignInterface(psiClass)) {
            // 解析类中的方法，提取接口路径
            PsiMethod[] methods = psiClass.getMethods();
            String parentPath = extractFeignParentPathFromClassAnnotation(psiClass);
            for (PsiMethod method : methods) {
                HttpMappingInfo feignInfo = HttpMappingInfo.of(parentPath, method);
                if (feignInfo != null) {
                    // 设置方法信息
                    feignInfo.setPsiMethod(method);
                    rs.add(feignInfo);
                }
            }
        }
        return rs;
    }

    /**
     * 获取当前feign类中的, 指定PsiMethod方法的HttpMappingInfo
     *
     * @param psiClass
     * @param method
     * @return
     */
    public static HttpMappingInfo feignOfPsiMethod(PsiClass psiClass, PsiMethod method) {
        HttpMappingInfo httpMappingInfo = null;
        if (AnnotationParserUtils.isFeignInterface(psiClass)) {
            // 解析类中的方法，提取接口路径
            String parentPath = extractFeignParentPathFromClassAnnotation(psiClass);
            httpMappingInfo = HttpMappingInfo.of(parentPath, method);
            if (Objects.nonNull(httpMappingInfo)) {
                // 设置方法信息
                httpMappingInfo.setPsiMethod(method);
            }
        }
        return httpMappingInfo;
    }

    /**
     * 提取@FeignClient path属性值
     */
    public static String extractFeignParentPathFromClassAnnotation(PsiClass psiClass) {
        PsiAnnotation annotation = psiClass.getAnnotation(SpringCloudClassAnnotation.FEIGNCLIENT.getQualifiedName());
        PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
        for (PsiNameValuePair attribute : attributes) {
            if ("path".equals(attribute.getName())) {
                PsiAnnotationMemberValue value = attribute.getValue();
                if (value instanceof PsiLiteralExpression) {
                    String path = ((PsiLiteralExpression) value).getValue().toString();
                    return handlePath(path);
                } else if (value instanceof PsiReferenceExpression) {
                    // 处理引用常量的情况
                    PsiElement resolvedElement = ((PsiReferenceExpression) value).resolve();
                    if (resolvedElement instanceof PsiField) {
                        PsiField field = (PsiField) resolvedElement;
                        PsiExpression initializer = field.getInitializer();
                        if (initializer instanceof PsiLiteralExpression) {
                            Object path = ((PsiLiteralExpression) initializer).getValue();
                            if (path instanceof String) {
                                String pathStr = (String) path;
                                return handlePath(pathStr);
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    private static @NotNull String handlePath(String pathStr) {
        // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
        if (StringUtils.isBlank(pathStr)) {
            return "";
        }
        // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
        // 如果path不以/开头，添加/
        if (!pathStr.startsWith("/")) {
            pathStr = "/" + pathStr;
        }
        // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
        // 如果path以/结尾，去除/
        if (pathStr.endsWith("/")) {
            pathStr = pathStr.substring(0, pathStr.length() - 1);
        }
        return pathStr;
    }
}
