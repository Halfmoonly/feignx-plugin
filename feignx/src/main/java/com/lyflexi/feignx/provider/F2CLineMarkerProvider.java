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
import com.lyflexi.feignx.constant.RestIcons;
import com.lyflexi.feignx.recover.SmartPsiElementRecover;
import com.lyflexi.feignx.utils.AnnotationParserUtils;
import com.lyflexi.feignx.utils.ControllerClassScanUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 将导航Gutter绘制在方法旁, 目前只能跳转到当前项目下的文件否则会报Element from alien project错误
 * 为什么gutter显示在注解旁，而不是方法旁？
 * 1. 注解一般不会在“回车”操作中被 PSI 拆解成新对象，避免了潜在的gutter失效的风险，稳定性好；
 * 2. 从用户视觉上来说，把跳转挂在注解处比挂在方法名上更直观；
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:56
 */
public class F2CLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        if (DumbService.isDumb(project)) {
            return; // 索引未完成，跳过
        }
        if (!(element instanceof PsiMethod)) {
            return;
        }
        if (!AnnotationParserUtils.isElementWithinFeign(element)) {
            return;
        }
        PsiMethod method = (PsiMethod) element;
        PsiClass psiClass = method.getContainingClass();
        // 增加有效性校验
        if (null == psiClass) {
            return;
        }
        if (!psiClass.isValid()) {
            psiClass = SmartPsiElementRecover.recoverClassWithCache(project, psiClass);
        }
        if (null == psiClass || !psiClass.isValid()) {
            return;
        }

        //解析restfull注解，下面gutter挂在注解旁
        PsiAnnotation restfulAnnotation = AnnotationParserUtils.findRestfulAnnotation(method);
        if (Objects.isNull(restfulAnnotation)) {
            return;
        }

        // 先执行Controller的全盘初始化扫描
        ControllerClassScanUtils.scanControllerPaths(method.getProject());
        //为了支持用户对当前feign接口更新，无论缓存是否存在，设置或者覆盖缓存
        BilateralCacheManager.setOrCoverFeignCache(method);
        //计算匹配到的目标Controller集合
        List<PsiElement> resultList = ControllerClassScanUtils.process(method);
        if (!resultList.isEmpty()) {
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                    .create(RestIcons.STATEMENT_LINE_FEIGN_ICON)
                    .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                    .setTargets(resultList)
                    .setTooltipTitle("Navigation to target in Controller");
            result.add(builder.createLineMarkerInfo(Objects.requireNonNull(restfulAnnotation)));
        }

    }
}

