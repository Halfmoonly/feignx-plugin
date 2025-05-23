//package com.lyflexi.feignx.entity;
//
//import com.intellij.openapi.ui.DialogWrapper;
//import com.lyflexi.feignx.utils.ControllerClassScanUtils;
//import com.lyflexi.feignx.utils.ToolBarUtils;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.List;
//
///**
// * @Description:
// * @Author: lyflexi
// * @project: feignx-plugin
// * @Date: 2024/10/18 16:04
// */
//public class CustomDialog extends DialogWrapper {
//    private final int dialogWidth = 600;  // 自定义对话框的宽度
//    private final int dialogHeight = 400; // 自定义对话框的高度
//    private List<HttpMappingInfo> httpMappingInfos;
//
//    @Override
//    protected void init() {
//        super.init();
//        setSize(dialogWidth, dialogHeight);
//    }
//
//    public CustomDialog(List<HttpMappingInfo> httpMappingInfos) {
//        super(true);
//        this.httpMappingInfos = httpMappingInfos;
//        init();
//        setTitle("接口信息");
//    }
//
//    @Nullable
//    @Override
//    protected JComponent createCenterPanel() {
//        // 创建内容面板
//        JPanel panel = new JPanel(new BorderLayout());
//        JTextArea textArea = new JTextArea();
//        textArea.setText(ToolBarUtils.showResult(httpMappingInfos));
//
//        // 将文本区域放入滚动面板中
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        // 添加导出列表按钮
//        JButton exportButton = new JButton("导出列表");
//        exportButton.addActionListener(e -> {
//            // 将列表导出到 CSV 文件
//            ControllerClassScanUtils.exportToCSV(httpMappingInfos);
//        });
//
//        panel.add(exportButton, BorderLayout.SOUTH);
//
//        return panel;
//    }
//
//}