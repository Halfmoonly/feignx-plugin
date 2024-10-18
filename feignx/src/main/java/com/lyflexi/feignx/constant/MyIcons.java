package com.lyflexi.feignx.constant;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:56
 */
public interface MyIcons {
    Icon STATEMENT_LINE_FEIGN_ICON = IconLoader.getIcon("/images/feign_method", MyIcons.class);
    Icon STATEMENT_LINE_CONTROLLER_ICON = IconLoader.getIcon("/images/controller_method", MyIcons.class);
}

