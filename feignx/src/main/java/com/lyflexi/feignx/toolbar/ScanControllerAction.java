package com.lyflexi.feignx.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lyflexi.feignx.model.ControllerInfo;
import com.lyflexi.feignx.model.CustomDialog;
import com.lyflexi.feignx.utils.JavaSourceFileUtil;
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
        List<ControllerInfo> controllerInfos = JavaSourceFileUtil.scanAllProjectControllerInfo();
        showControllerInfo(controllerInfos);

    }
    private void showControllerInfo(List<ControllerInfo> controllerInfos) {
        CustomDialog dialog = new CustomDialog(controllerInfos);
        dialog.show();
    }
}