package com.lyflexi.feignx.ui;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.lyflexi.feignx.constant.RestIcons;
import com.lyflexi.feignx.utils.AnnotationParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Author: liuyanoutsee@outlook.com
 * @Date: 2025/4/2 20:34
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description:
 */
public class RestClassIconProvider extends IconProvider {

    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        if (!(element instanceof PsiClass)) {
            return null;
        }


        PsiClass psiClass = (PsiClass) element;
        //开启FeignClient文件图标
        if (UserFeignSettings.getInstance().isIconEnabled()) {
            if (AnnotationParserUtils.isFeignInterface(psiClass)) {
                return RestIcons.STATEMENT_LINE_FEIGN_ICON;
            }
        }
        //开启RestController文件图标
        if (UserControllerSettings.getInstance().isIconEnabled()) {
            if (AnnotationParserUtils.isControllerClass(psiClass)) {
                return RestIcons.STATEMENT_LINE_CONTROLLER_ICON;
            }
        }

        return null;
    }
}
