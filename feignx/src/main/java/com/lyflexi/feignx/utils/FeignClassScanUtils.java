package com.lyflexi.feignx.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.entity.HttpMappingInfo;
import com.lyflexi.feignx.enums.SpringCloudClassAnnotation;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author: hmly
 * @Date: 2025/3/14 20:51
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: feign类扫描工具类
 */
public class FeignClassScanUtils {

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
     * @param feignInfo
     * @param controllerMethod
     * @return
     */
    private static boolean match2F(HttpMappingInfo feignInfo, PsiMethod controllerMethod) {
        HttpMappingInfo controllerCache = BilateralCacheManager.getOrSetControllerCache(controllerMethod);
        if (Objects.isNull(controllerCache)){
            return false;
        }
        String path = controllerCache.getPath();
        return StringUtils.equals(path,feignInfo.getPath());
    }


    /**
     * 扫描Feign接口信息添加到缓存里面
     *
     * @param project 项目
     * @return {@link List}<{@link HttpMappingInfo}>
     */
    public static List<HttpMappingInfo> scanFeignInterfaces(Project project) {
        PsiManager psiManager = PsiManager.getInstance(project);
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
        PsiPackage rootPackage = JavaPsiFacade.getInstance(psiManager.getProject()).findPackage("");
        // 检查是否在 Dumb 模式下，以避免在项目构建期间执行代码
        if (DumbService.isDumb(project)) {
            return Collections.emptyList();
        }
        Map<String, HttpMappingInfo> feignCaches = BilateralCacheManager.queryFeignCaches(project);
        if (org.apache.commons.collections.MapUtils.isNotEmpty(feignCaches)) {
            return new ArrayList<>(feignCaches.values());
        }
        // 获取项目中的所有Feign源文件
        List<PsiClass> javaFiles = ProjectUtils.scanAllFeignClassesByPsiShortNamesCache(project,searchScope);
        List<HttpMappingInfo> feignInfos = new ArrayList<>();
        List<Future<List<HttpMappingInfo>>> futures = new ArrayList<>();
        //创建线程池
        ExecutorService executor = ThreadPoolUtils.createExecutor();
        for (PsiClass psiClass : javaFiles) {
            // 判断类是否带有@Controller或@RestController注解
            // java.lang.Throwable: Read access is allowed from inside read-action (or EDT) only (see com.intellij.openapi.application.Application.runReadAction())
            //            futures.add(executor.submit(() -> feignsOfPsiClass(psiClass)));
            // 问题本质上是：在非 ReadAction / 非主线程下访问 PSI Tree 或 PSI API，会被 IntelliJ 拦截报错。
            // 解决方案如下：
            futures.add(executor.submit(() ->
                    ApplicationManager.getApplication().runReadAction((Computable<List<HttpMappingInfo>>) () ->
                            feignsOfPsiClass(psiClass)
                    )
            ));
        }

        // 收集结果
        for (Future<List<HttpMappingInfo>> future : futures) {
            try {
                feignInfos.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("FeignClassScanUtils#executor并行扫描期间异常..."+e.getMessage()+ "进入兜底扫描方案");
                for (PsiClass psiClass : javaFiles) {
                    // 判断类是否带有@FeignClient注解
                    feignInfos.addAll(feignsOfPsiClass(psiClass));
                }
            }
        }

        // 将结果添加到缓存中
        BilateralCacheManager.initFeignCaches(project, feignInfos);

        executor.shutdown();
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
                    // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
                    if (StringUtils.isBlank(path)) {
                        return "";
                    }
                    // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
                    // 如果path不以/开头，添加/
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                    // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
                    // 如果path以/结尾，去除/
                    if (path.endsWith("/")) {
                        path = path.substring(0, path.length() - 1);
                    }
                    return path;
                }
            }
        }
        return "";
    }
}
