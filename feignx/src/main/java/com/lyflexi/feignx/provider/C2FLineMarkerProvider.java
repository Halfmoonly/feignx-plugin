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
import com.lyflexi.feignx.utils.FeignClassScanUtils;
import com.lyflexi.feignx.utils.ProjectUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 将导航Gutter绘制在注解旁, 目前只能跳转到当前项目下的文件否则会报Element from alien project错误
 *
 * 为什么gutter显示在注解旁，而不是方法旁？
 * 1. 注解一般不会在“回车”操作中被 PSI 拆解成新对象，避免了潜在的gutter失效的风险，稳定性好；
 * 2. 从用户视觉上来说，把跳转挂在注解处比挂在方法名上更直观；
 *
 *
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:55
 */
public class C2FLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (null==element) {
            return;
        }
        //排除三方依赖扫描
        if (!ProjectUtils.isBizElement(element)){
            return;
        }
        Project project = element.getProject();
        if (DumbService.isDumb(project)) {
            return; // 索引未完成，跳过
        }
        if (!(element instanceof PsiMethod)){
            return;
        }
        if (!AnnotationParserUtils.isElementWithinController(element)) {
          return;
        }
        PsiMethod method = (PsiMethod) element;
        PsiClass psiClass = method.getContainingClass();
        // 增加有效性校验
        if (null == psiClass || !psiClass.isValid()) {
            return;
        }

        //解析restfull注解，下面gutter挂在注解旁
        PsiAnnotation restfulAnnotation = AnnotationParserUtils.findRestfulAnnotation(method);
        if (Objects.isNull(restfulAnnotation)) {
            return;
        }
        // 先执行Feign的全盘初始化扫描
        FeignClassScanUtils.scanFeignInterfaces(method.getProject());
        // 为了支持用户对当前controller接口更新，无论缓存是否存在，设置或者覆盖缓存
        BilateralCacheManager.setOrCoverControllerCache(method);
        //计算匹配到的目标Feign集合
        List<PsiElement> resultList = FeignClassScanUtils.process(method);

        if (!resultList.isEmpty()) {
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                    .create(RestIcons.STATEMENT_LINE_CONTROLLER_ICON)
                    .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                    .setTargets(resultList)
                    .setTooltipTitle("Navigation to target in Feign");
            result.add(builder.createLineMarkerInfo(restfulAnnotation));
        }
    }

}