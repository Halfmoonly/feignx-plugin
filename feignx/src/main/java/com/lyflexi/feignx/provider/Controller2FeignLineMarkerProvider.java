package com.lyflexi.feignx.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.lyflexi.feignx.cache.MyCacheManager;
import com.lyflexi.feignx.constant.MyIcons;
import com.lyflexi.feignx.model.ControllerInfo;
import com.lyflexi.feignx.utils.JavaSourceFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:55
 */
public class Controller2FeignLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        JavaSourceFileUtil.clear();
        if (element instanceof PsiMethod && isElementWithinController(element)) {
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
                    result.add(builder.createLineMarkerInfo(Objects.requireNonNull(psiMethod.getNameIdentifier())));
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
        List<ControllerInfo> feignInfos = JavaSourceFileUtil.scanFeignInterfaces(project);
        if (feignInfos != null) {
            // 遍历 Controller 类的所有方法
            for (ControllerInfo feignInfo : feignInfos) {
                if (isMethodMatch(feignInfo, controllerMethod)) {
                    elementList.add(feignInfo.getMethod());
                }
            }
        }

        return elementList;
    }

    private static boolean isMethodMatch(ControllerInfo feignInfo, PsiMethod controllerMethod) {
        String controllerPath = MyCacheManager.getControllerPath(controllerMethod);
        if (StringUtils.isNotBlank(controllerPath)) {
            return controllerPath.equals(feignInfo.getPath());
        }
        return false;
    }

    /**
     * 元素是否为FeignClient下的方法
     *
     * @param element 元素
     * @return boolean
     */
    private static boolean isElementWithinController(PsiElement element) {
        if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;

            // 检查类上是否存在 FeignClient 注解
            return JavaSourceFileUtil.isControllerClass(psiClass);
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return type != null && isElementWithinController(type);
    }
}