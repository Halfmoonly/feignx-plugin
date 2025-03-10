package com.lyflexi.feignx.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lyflexi.feignx.model.HttpMappingInfo;
import com.lyflexi.feignx.model.CustomDialog;
import com.lyflexi.feignx.utils.JavaResourceUtil;
import com.lyflexi.feignx.utils.ToolBarUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 16:03
 */
public class ScanControllerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        List<HttpMappingInfo> httpMappingInfos = ToolBarUtil.scanAllProjectControllerInfo();
        showControllerInfo(httpMappingInfos);

    }
    private void showControllerInfo(List<HttpMappingInfo> httpMappingInfos) {
        CustomDialog dialog = new CustomDialog(httpMappingInfos);
        dialog.show();
    }
}