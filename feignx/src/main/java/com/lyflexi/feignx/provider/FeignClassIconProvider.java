package com.lyflexi.feignx.provider;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.lyflexi.feignx.constant.RestIcons;
import com.lyflexi.feignx.user.UserFeignSettings;
import com.lyflexi.feignx.utils.AnnotationParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Author: liuyanoutsee@outlook.com
 * @Date: 2025/4/2 20:34
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: FeignClient文件图标
 */
public class FeignClassIconProvider extends IconProvider {
    /**
     * FeignClient文件图标
     */
    Icon STATEMENT_LINE_FEIGN_ICON = IconLoader.getIcon("/icons/feignAction.svg");

    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        if (!(element instanceof PsiClass)) {
            return null;
        }


        PsiClass psiClass = (PsiClass) element;
        // 只对接口类做处理
        if (!psiClass.isInterface()) {
            return null;
        }

        //开启FeignClient文件图标
        if (!UserFeignSettings.getInstance().isIconEnabled()) {
            return null;
        }

        if (AnnotationParserUtils.isFeignInterface(psiClass)) {
            return STATEMENT_LINE_FEIGN_ICON;
        }


//        //开启RestController文件图标
//        if (UserControllerSettings.getInstance().isIconEnabled()) {
//            if (AnnotationParserUtils.isControllerClass(psiClass)) {
//                return RestIcons.STATEMENT_LINE_CONTROLLER_ICON;
//            }
//        }

        return null;
    }
}
