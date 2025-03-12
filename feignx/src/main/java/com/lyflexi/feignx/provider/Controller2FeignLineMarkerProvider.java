package com.lyflexi.feignx.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.constant.MyIcons;
import com.lyflexi.feignx.model.HttpMappingInfo;
import com.lyflexi.feignx.utils.MappingAnnotationUtil;
import com.lyflexi.feignx.utils.JavaResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 将导航Gutter绘制在注解旁
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:55
 */
public class Controller2FeignLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        BilateralCacheManager.clearFeignCache(project);
        if (element instanceof PsiMethod && JavaResourceUtil.isElementWithinController(element)) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass psiClass = psiMethod.getContainingClass();
            if (psiClass != null) {
                List<PsiElement> resultList = process(psiMethod);
                if (!resultList.isEmpty()) {
                    NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                            .create(MyIcons.STATEMENT_LINE_CONTROLLER_ICON)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTargets(resultList)
                            .setTooltipTitle("Navigation to target in Feign");
                    PsiAnnotation targetAnnotation = MappingAnnotationUtil.findTargetMappingAnnotation(psiMethod);
                    result.add(builder.createLineMarkerInfo(Objects.requireNonNull(targetAnnotation)));
                }
            }
        }
    }

    /**
     * 调转到调用该controller的Feign
     *
     * @param controllerMethod psi方法
     * @return {@link List}<{@link PsiElement}>
     */
    private List<PsiElement> process(PsiMethod controllerMethod) {
        List<PsiElement> elementList = new ArrayList<>();
        // 获取当前项目
        Project project = controllerMethod.getProject();
        List<HttpMappingInfo> feignInfos = JavaResourceUtil.scanFeignInterfaces(project);
        if (feignInfos != null) {
            // 遍历 Controller 类的所有方法
            for (HttpMappingInfo feignInfo : feignInfos) {
                if (match2F(feignInfo, controllerMethod)) {
                    elementList.add(feignInfo.getMethod());
                }
            }
        }

        return elementList;
    }

    private static boolean match2F(HttpMappingInfo feignInfo, PsiMethod controllerMethod) {
        String controllerPath = BilateralCacheManager.getControllerPath(controllerMethod);
        if (StringUtils.isNotBlank(controllerPath)) {
            return controllerPath.equals(feignInfo.getPath());
        }
        return false;
    }
}