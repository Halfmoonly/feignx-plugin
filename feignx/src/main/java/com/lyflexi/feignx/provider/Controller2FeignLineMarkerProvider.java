package com.lyflexi.feignx.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.constant.MyIcons;
import com.lyflexi.feignx.utils.AnnotationParserUtils;
import com.lyflexi.feignx.utils.FeignClassScanUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 将导航Gutter绘制在注解旁, 目前只能跳转到当前项目下的文件否则会报Element from alien project错误
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:55
 */
public class Controller2FeignLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        if (DumbService.isDumb(project)) {
            return; // 索引未完成，跳过
        }
        if (element instanceof PsiMethod && AnnotationParserUtils.isElementWithinController(element)) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass psiClass = psiMethod.getContainingClass();
            //为了支持用户对当前controller接口更新，无论缓存是否存在，设置或者覆盖缓存
            BilateralCacheManager.setOrCoverControllerCache(psiMethod);
            if (psiClass != null) {
                List<PsiElement> resultList = FeignClassScanUtils.process(psiMethod);
                if (!resultList.isEmpty()) {
                    NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                            .create(MyIcons.STATEMENT_LINE_CONTROLLER_ICON)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTargets(resultList)
                            .setTooltipTitle("Navigation to target in Feign");
                    PsiAnnotation targetAnnotation = AnnotationParserUtils.findRestfulAnnotation(psiMethod);
                    //在用户打注释/***/期间，psiMethod会有一瞬间不再拥有注解
                    if (Objects.nonNull(targetAnnotation)) {
                        result.add(builder.createLineMarkerInfo(Objects.requireNonNull(targetAnnotation)));
                    }
                }
            }
        }
    }

}