package com.lyflexi.feignx.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.lyflexi.feignx.cache.CacheManager;
import com.lyflexi.feignx.constant.MyIcons;
import com.lyflexi.feignx.model.HttpMappingInfo;
import com.lyflexi.feignx.utils.ProviderUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:56
 */
public class Feign2ControllerLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof PsiMethod && isElementWithinFeign(element)) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass psiClass = psiMethod.getContainingClass();
            if (psiClass != null) {
                List<PsiElement> resultList = processF2C(psiMethod);
                if (!resultList.isEmpty()) {
                    NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                            .create(MyIcons.STATEMENT_LINE_FEIGN_ICON)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTargets(resultList)
                            .setTooltipTitle("Navigation to target in Controller");
                    result.add(builder.createLineMarkerInfo(Objects.requireNonNull(psiMethod.getNameIdentifier())));
                }
            }
        }

    }

    /**
     * 元素是否为FeignClient下的方法
     *
     * @param element 元素
     * @return boolean
     */
    public static boolean isElementWithinFeign(PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            PsiClass psiClass = (PsiClass) element;

            // 检查类上是否存在 FeignClient 注解
            PsiAnnotation feignAnnotation = psiClass.getAnnotation("org.springframework.cloud.openfeign.FeignClient");
            if (feignAnnotation != null) {
                return true;
            }
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return type != null && isElementWithinFeign(type);
    }

    /**
     * 目前只能跳转到当前项目下的文件否则会报Element from alien project错误
     *
     * @param psiMethod psi方法
     * @return {@link List}<{@link PsiElement}>
     */
    public static List<PsiElement> processF2C(PsiMethod psiMethod) {
        List<PsiElement> elementList = new ArrayList<>();

        // 获取当前项目
        Project project = psiMethod.getProject();

        List<HttpMappingInfo> httpMappingInfos = ProviderUtil.scanControllerPaths(project);

        if (httpMappingInfos != null) {
            // 遍历 Controller 类的所有方法
            for (HttpMappingInfo httpMappingInfo : httpMappingInfos) {
                if (ProviderUtil.matchController(httpMappingInfo, psiMethod)) {
                    elementList.add(httpMappingInfo.getMethod());
                }
            }
        }

        return elementList;
    }
}

