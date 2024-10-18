package com.lyflexi.feignx.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lyflexi.feignx.utils.JavaSourceFileUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 16:03
 */
public class RefreshCacheAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        JavaSourceFileUtil.clear();
        // 扫描项目中的Java源文件
        JavaSourceFileUtil.scanAllProjectControllerInfo();
        JavaSourceFileUtil.scanAllProjectFeignInfo();
    }

    public void refresh(){
        JavaSourceFileUtil.clear();
        // 扫描项目中的Java源文件
        JavaSourceFileUtil.scanAllProjectControllerInfo();
    }
}