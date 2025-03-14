package com.lyflexi.feignx.utils;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.containers.ContainerUtil;
import com.lyflexi.feignx.enums.SpringCloudClassAnnotation;
import com.lyflexi.feignx.enums.SpringBootClassAnnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: hmly
 * @Date: 2025/3/14 19:42
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description:
 */
public class ProjectUtils {
    /**
     * 获取所有打开的项目列表
     *
     * @return {@link Project[]}
     */
    public static Project[] getOpenProjects() {
        // 获取ProjectManager实例
        ProjectManager projectManager = ProjectManager.getInstance();
        // 获取所有打开的项目列表
        return projectManager.getOpenProjects();
    }

    /**
     * 获取工程中所有的class
     * @param rootPackage
     * @param searchScope
     * @return
     */
    @Deprecated
    public static List<PsiClass> getAllClasses(PsiPackage rootPackage, GlobalSearchScope searchScope) {
        List<PsiClass> javaFiles = new ArrayList<>();
        processPackage(rootPackage, searchScope, javaFiles);
        return javaFiles;
    }

    /**
     * 递归方法processPackage
     * @param psiPackage
     * @param searchScope
     * @param classesToCheck
     */
    @Deprecated
    private static void processPackage(PsiPackage psiPackage, GlobalSearchScope searchScope, List<PsiClass> classesToCheck) {
        for (PsiClass psiClass : psiPackage.getClasses()) {
            classesToCheck.add(psiClass);
        }

        for (PsiPackage subPackage : psiPackage.getSubPackages(searchScope)) {
            processPackage(subPackage, searchScope, classesToCheck);
        }
    }


    /**
     * 扫描所有controller类,使用 IntelliJ 的 类快速索引缓存系统PsiShortNamesCache
     * 方式	                            推荐场景	                                                           性能
     * 手写全类递归	                    小项目或通用工具	                                                   ❌慢(最笨的)
     * AnnotatedElementsSearch         专注指定注解类比如Controller和RestController	                           🚀快(基于直接查询所有类并检查注解的方式。如果项目非常大，这可能会比较耗时)
     * PsiShortNamesCache	          ✅最推荐，快速按类名索引	                                               🚀🚀快
     * 多模块并发	                        大型项目	                                                           🚀🚀🚀
     * @param project
     *
     * @param scope
     * @return 注意需要自己再判断目标psiClass类型：是否是Controller类
     */
    public static List<PsiClass> scanAllControllerClassesByPsiShortNamesCache(Project project, GlobalSearchScope scope) {
        List<PsiClass> controllerClasses = new ArrayList<>();
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);

        String[] allClassNames = shortNamesCache.getAllClassNames();

        for (String className : allClassNames) {
            PsiClass[] classes = shortNamesCache.getClassesByName(className, scope);
            for (PsiClass psiClass : classes) {
                if (AnnotationParserUtils.isControllerClass(psiClass)) {
                    controllerClasses.add(psiClass);
                }
            }
        }
        return controllerClasses;
    }

    /**
     * 扫描所有feign类,使用 IntelliJ 的 类快速索引缓存系统PsiShortNamesCache
     * 方式	                            推荐场景	                         性能
     * 手写全类递归	                    小项目或通用工具	                  ❌慢（最笨的）
     * AnnotatedElementsSearch         专注注解类,比如FeignClient	          🚀快(基于直接查询所有类并检查注解的方式。如果项目非常大，这可能会比较耗时)
     * PsiShortNamesCache	          ✅最推荐，快速按类名索引	              🚀🚀快
     * 多模块并发	                        大型项目	                          🚀🚀🚀
     * @param project
     *
     * @param scope
     * @return 注意需要自己再判断目标psiClass类型：是否是Feign类
     */
    public static List<PsiClass> scanAllFeignClassesByPsiShortNamesCache(Project project, GlobalSearchScope scope) {
        List<PsiClass> feignClass = new ArrayList<>();
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);

        String[] allClassNames = shortNamesCache.getAllClassNames();

        for (String className : allClassNames) {
            PsiClass[] classes = shortNamesCache.getClassesByName(className, scope);
            for (PsiClass psiClass : classes) {
                if (AnnotationParserUtils.isFeignInterface(psiClass)) {
                    feignClass.add(psiClass);
                }
            }
        }

        return feignClass;
    }


}
