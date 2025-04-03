package com.lyflexi.feignx.provider;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.GutterName;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.constant.RestIcons;
import com.lyflexi.feignx.entity.HttpMappingInfo;
import com.lyflexi.feignx.recover.SmartPsiElementRecover;
import com.lyflexi.feignx.utils.AnnotationParserUtils;
import com.lyflexi.feignx.utils.FeignClassScanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.StringSelection;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
public class CopyFeignUrlLineMarkerProvider extends LineMarkerProviderDescriptor {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (null == element) {
            return null;
        }
        Project project = element.getProject();
        if (DumbService.isDumb(project)) {
            return null; // 索引未完成，跳过
        }
        if (!(element instanceof PsiMethod)) {
            return null;
        }

        PsiMethod method = (PsiMethod) element;
        // 增加有效性校验
        if (null == method) {
            return null;
        }
        if (!method.isValid()) {
            method = SmartPsiElementRecover.recoverMethod(project, method);
        }
        if (null == method || !method.isValid()) {
            return null;
        }

        // 判断是不是 Feign 方法
        if (!AnnotationParserUtils.isElementWithinFeign(element)) {
            return null;
        }

        //提前初始化feign接口缓存
        FeignClassScanUtils.scanFeignInterfaces(method.getProject());

        // 获取拼接后的 Feign URL
        HttpMappingInfo feignCache = BilateralCacheManager.setOrCoverFeignCache(method);
        if (Objects.isNull(feignCache)) {
            return null;
        }
        String url = feignCache.getPath();

        if (StringUtils.isBlank(url)) {
            return null;
        }

        // 构建图标点击逻辑
        PsiMethod finalMethod = method;
        GutterIconNavigationHandler<PsiElement> handler = (mouseEvent, elt) -> {
            CopyPasteManager.getInstance().setContents(new StringSelection(url));
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("Navigator4URL OpenFeign RestController")
                    .createNotification("URL copied to clipboard:\n" + url, NotificationType.INFORMATION)
                    .notify(finalMethod.getProject());
        };
        // 构建图标信息，挂在方法上
        LineMarkerInfo<PsiElement> marker = new LineMarkerInfo<>(
                method.getNameIdentifier(),
                method.getNameIdentifier().getTextRange(),
                RestIcons.STATEMENT_LINE_CLIPBOARD_FEIGN_ICON,
                psi -> "Click to copy Feign-URL: " + url,
                handler,
                GutterIconRenderer.Alignment.RIGHT,
                () -> "Copy Feign URL"
        );

        return marker;
    }

    @Override
    public @Nullable @GutterName String getName() {
        return "Copy Feign URL";
    }
}
