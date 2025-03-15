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
import com.lyflexi.feignx.enums.SpringCloudClassAnnotation;
import com.lyflexi.feignx.enums.SpringBootClassAnnotation;

import java.util.*;
import java.util.stream.Collectors;

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
     *
     * @param rootPackage
     * @param searchScope
     * @return
     */
    public static List<PsiClass> scanAllClasses(PsiPackage rootPackage, GlobalSearchScope searchScope) {
        List<PsiClass> javaFiles = new ArrayList<>();
        processPackage(rootPackage, searchScope, javaFiles);
        return javaFiles;
    }

    /**
     * 递归方法processPackage
     *
     * @param psiPackage
     * @param searchScope
     * @param classesToCheck
     */
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
     *
     * @param project
     * @param scope
     * @return 注意需要自己再判断目标psiClass类型：是否是Controller类
     *
     * TODO 但是目前返回为空？原因未知
     */
    @Deprecated
    public static List<PsiClass> scanAllControllerClassesByPsiShortNamesCache(Project project, GlobalSearchScope scope) {
        List<PsiClass> controllerClasses = new ArrayList<>();
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            //这里总是会报异常，      catch (ProcessCanceledException | IndexNotReadyException e) {
            //        throw e;
            //      }
            // 触发场景：项目刚启动、索引还没完成，或索引被重建时调用 PSI 索引相关 API（比如 getAllClassNames()）就会抛这个。
            //解决方案，包装到runReadActionInSmartMode模式内使用
            String[] allClassNames = shortNamesCache.getAllClassNames();
            for (String className : allClassNames) {
                PsiClass[] classes = shortNamesCache.getClassesByName(className, scope);
                for (PsiClass psiClass : classes) {
                    if (AnnotationParserUtils.isControllerClass(psiClass)) {
                        controllerClasses.add(psiClass);
                    }
                }
            }
        });
        return controllerClasses;
    }

    /**
     * 扫描所有feign类,使用 IntelliJ 的 类快速索引缓存系统PsiShortNamesCache
     * 方式	                            推荐场景	                         性能
     * 手写全类递归	                    小项目或通用工具	                  ❌慢（最笨的）
     * AnnotatedElementsSearch         专注注解类,比如FeignClient	          🚀快(基于直接查询所有类并检查注解的方式。如果项目非常大，这可能会比较耗时)
     * PsiShortNamesCache	          ✅最推荐，快速按类名索引	              🚀🚀快
     * 多模块并发	                        大型项目	                          🚀🚀🚀
     *
     * @param project
     * @param scope
     * @return 注意需要自己再判断目标psiClass类型：是否是Feign类
     *
     * TODO 但是目前返回为空？原因未知
     */
    @Deprecated
    public static List<PsiClass> scanAllFeignClassesByPsiShortNamesCache(Project project, GlobalSearchScope scope) {
        List<PsiClass> feignClass = new ArrayList<>();
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            //这里总是会报异常，      catch (ProcessCanceledException | IndexNotReadyException e) {
            //        throw e;
            //      }
            // 触发场景：项目刚启动、索引还没完成，或索引被重建时调用 PSI 索引相关 API（比如 getAllClassNames()）就会抛这个。
            //解决方案，包装到runReadActionInSmartMode模式内使用
            String[] allClassNames = shortNamesCache.getAllClassNames();

            for (String className : allClassNames) {
                PsiClass[] classes = shortNamesCache.getClassesByName(className, scope);
                for (PsiClass psiClass : classes) {
                    if (AnnotationParserUtils.isFeignInterface(psiClass)) {
                        feignClass.add(psiClass);
                    }
                }
            }
        });
        return feignClass;
    }


    /**
     * 使用 AnnotatedElementsSearch 精确扫描所有 Controller 类（包含 @Controller 和 @RestController）
     *
     * @param project
     * @param scope
     * @return
     *
     * TODO 但是目前返回为空？原因未知
     */
    @Deprecated
    public static List<PsiClass> scanAllControllerClassesByAnnotationSearch(Project project, GlobalSearchScope scope) {
        List<PsiClass> controllerClasses = new ArrayList<>();
        if (DumbService.isDumb(project)) {
            return Collections.emptyList(); // 索引未完成，跳过
        }

        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);

        PsiClass controllerAnnotation = facade.findClass(SpringBootClassAnnotation.CONTROLLER.getQualifiedName(), scope);
        PsiClass restControllerAnnotation = facade.findClass(SpringBootClassAnnotation.RESTCONTROLLER.getQualifiedName(), scope);

        if (Objects.nonNull(controllerAnnotation)) {
            Collection<PsiClass> classes = AnnotatedElementsSearch.searchPsiClasses(controllerAnnotation, scope).findAll();
            controllerClasses.addAll(classes);
        }

        if (Objects.nonNull(restControllerAnnotation)) {
            Collection<PsiClass> classes = AnnotatedElementsSearch.searchPsiClasses(restControllerAnnotation, scope).findAll();
            controllerClasses.addAll(classes);
        }

        // 可选：去重（如果某个类同时标注了两个注解）
        controllerClasses = controllerClasses.stream()
                .distinct()
                .collect(Collectors.toList());

        return controllerClasses;
    }

    /**
     * 使用 AnnotatedElementsSearch 精确扫描所有 Feign 类（包含 @FeignClient）
     * @param project
     * @param scope
     * @return
     *
     * TODO 但是目前返回为空？原因未知
     */
    @Deprecated
    public static List<PsiClass> scanAllFeignClassesByAnnotationSearch(Project project, GlobalSearchScope scope) {
        List<PsiClass> feignClasss = new ArrayList<>();
        if (DumbService.isDumb(project)) {
            return Collections.emptyList(); // 索引未完成，跳过
        }

        JavaPsiFacade facade = JavaPsiFacade.getInstance(project);

        PsiClass controllerAnnotation = facade.findClass(SpringCloudClassAnnotation.FEIGNCLIENT.getQualifiedName(), scope);

        if (Objects.nonNull(controllerAnnotation)) {
            Collection<PsiClass> classes = AnnotatedElementsSearch.searchPsiClasses(controllerAnnotation, scope).findAll();
            feignClasss.addAll(classes);
        }

        return feignClasss;
    }

}
