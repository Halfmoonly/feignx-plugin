//package com.lyflexi.feignx.toolbar;
//
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.project.Project;
//import com.lyflexi.feignx.entity.HttpMappingInfo;
//import com.lyflexi.feignx.entity.CustomDialog;
//import com.lyflexi.feignx.utils.ToolBarUtils;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//
///**
// * @Description:
// * @Author: lyflexi
// * @project: feignx-plugin
// * @Date: 2024/10/18 16:03
// */
//public class ScanControllerAction extends AnAction {
//
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent event) {
//        Project project = event.getProject();
//        List<HttpMappingInfo> httpMappingInfos = ToolBarUtils.scanAllProjectControllerInfo(project);
//        showControllerInfo(httpMappingInfos);
//
//    }
//    private void showControllerInfo(List<HttpMappingInfo> httpMappingInfos) {
//        CustomDialog dialog = new CustomDialog(httpMappingInfos);
//        dialog.show();
//    }
//}