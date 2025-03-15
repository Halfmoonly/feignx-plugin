package com.lyflexi.feignx.provider;

import com.intellij.codeInsight.daemon.*;
import com.intellij.notification.NotificationGroupManager;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.constant.MyIcons;
import com.lyflexi.feignx.entity.HttpMappingInfo;
import com.lyflexi.feignx.utils.AnnotationParserUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.util.*;

import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.Nullable;

/**
 * @Author: hmly
 * @Date: 2025/3/9 17:42
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: 将拷贝功能的Gutter挂在注解上
 */

/*
 * 复制操作不属于RelatedItemLineMarkerProvider，IntelliJ 平台在新版 SDK 中推荐实现LineMarkerProviderDescriptor
 *
 * 1. 实现接口：LineMarkerProviderDescriptor
 * 2. 自定义构造LineMarkerInfo<PsiElement>：由于点击图标时执行复制操作不属于跳转导航操作NavigationGutterIconBuilder，而是需要手动构建 LineMarkerInfo
 *
 * */
public class CopyControllerUrlLineMarkerProvider extends LineMarkerProviderDescriptor {
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    //    @Override
//    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
//        if (element instanceof PsiMethod && JavaResourceUtil.isElementWithinController(element)) {
//            PsiMethod psiMethod = (PsiMethod) element;
//            String url = CacheManager.getControllerPath(psiMethod);
//            if (StringUtils.isBlank(url)) {
//                return null;
//            }
//            // 查找目标注解
//            PsiAnnotation targetAnnotation = AnnotationUtil.findTargetMappingAnnotation(psiMethod);
//            if (targetAnnotation == null) {
//                return null;
//            }
//
//            GutterIconNavigationHandler<PsiElement> handler = (mouseEvent, elt) -> {
//                // 复制到剪贴板
//                CopyPasteManager.getInstance().setContents(new StringSelection(url));
//                // 显示通知（可选）
//                NotificationGroupManager.getInstance()
//                        .getNotificationGroup("Navigator4URL OpenFeign RestController")
//                        .createNotification("URL copied to clipboard:\n" + url, NotificationType.INFORMATION)
//                        .notify(psiMethod.getProject());
//            };
//            return new LineMarkerInfo<>(
//                    targetAnnotation, // icon 放在哪个元素上，这里是注解名
//                    targetAnnotation.getTextRange(),
//                    MyIcons.STATEMENT_LINE_CLIPBOARD_ICON,
//                    psi -> "Click to copy Controller-URL: " + url, // tooltip
//                    handler,
//                    GutterIconRenderer.Alignment.CENTER,
//                    () -> "Copy Controller URL"
//            );
//        }
//
//        return null;
//    }

    /**
     * 当用户在写注释/***的一瞬间，LineMarkerProviderDescriptor.getLineMarkerInfo() 在调用时传入的 element 可能不再是 PsiMethod 或目标注解了，
     *
     * 而是 JavaDoc 里的某个 PsiElement（比如 PsiDocComment）或者换行符、空格、标签等。
     *
     * 因此仅仅实现getLineMarkerInfo判断一个element是不够的
     *
     * 要实现collectSlowLineMarkers遍历所有的elements，
     * @param elements
     * @param result
     */
    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements,
                                       @NotNull Collection<? super LineMarkerInfo<?>> result) {

        for (PsiElement element : elements) {
            Project project = element.getProject();
            if (DumbService.isDumb(project)) {
                continue; // 索引未完成，跳过
            }
            if (!(element instanceof PsiMethod)) {
                continue;
            }

            PsiMethod method = (PsiMethod) element;

            if (!AnnotationParserUtils.isElementWithinController(method)) {
                continue;
            }

            PsiAnnotation targetAnnotation = AnnotationParserUtils.findRestfulAnnotation(method);
            if (targetAnnotation == null) {
                continue;
            }

            HttpMappingInfo controllerCache = BilateralCacheManager.getOrSetControllerCache(method);

            if (Objects.isNull(controllerCache)) {
                continue;
            }

            String url = controllerCache.getPath();
            if (StringUtils.isBlank(url)) {
                continue;
            }

            GutterIconNavigationHandler<PsiElement> handler = (mouseEvent, elt) -> {
                CopyPasteManager.getInstance().setContents(new StringSelection(url));
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("Navigator4URL OpenFeign RestController")
                        .createNotification("URL copied to clipboard:\n" + url, NotificationType.INFORMATION)
                        .notify(method.getProject());
            };

            LineMarkerInfo<PsiElement> markerInfo = new LineMarkerInfo<>(
                    targetAnnotation,
                    targetAnnotation.getTextRange(),
                    MyIcons.STATEMENT_LINE_CLIPBOARD_ICON,
                    psi -> "Click to copy Controller-URL: " + url,
                    handler,
                    GutterIconRenderer.Alignment.CENTER,
                    () -> "Copy Controller URL"
            );

            result.add(markerInfo);
        }
    }

    @Override
    public @Nullable("null means disabled") @GutterName String getName() {
        return "Copy Controller URL";
    }
}
