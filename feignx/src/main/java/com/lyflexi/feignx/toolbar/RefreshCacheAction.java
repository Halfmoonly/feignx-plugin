package com.lyflexi.feignx.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lyflexi.feignx.cache.CacheManager;
import com.lyflexi.feignx.utils.JavaResourceUtil;
import com.lyflexi.feignx.utils.ToolBarUtil;
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
        CacheManager.clear();
        // 扫描项目中的Java源文件
        ToolBarUtil.scanAllProjectControllerInfo();
        ToolBarUtil.scanAllProjectFeignInfo();
    }

    public void refresh(){
        CacheManager.clear();
        // 扫描项目中的Java源文件
        ToolBarUtil.scanAllProjectControllerInfo();
    }
}