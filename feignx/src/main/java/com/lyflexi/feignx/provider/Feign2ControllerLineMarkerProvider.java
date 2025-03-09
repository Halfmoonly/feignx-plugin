package com.lyflexi.feignx.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.lyflexi.feignx.cache.CacheManager;
import com.lyflexi.feignx.constant.MyIcons;
import com.lyflexi.feignx.utils.JavaResourceUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:56
 */
public class Feign2ControllerLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        CacheManager.clearControllerCache(project);
        if (element instanceof PsiMethod && JavaResourceUtil.isElementWithinFeign(element)) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass psiClass = psiMethod.getContainingClass();
            if (psiClass != null) {
                List<PsiElement> resultList = JavaResourceUtil.process(psiMethod);
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


}

