//package com.lyflexi.feignx.toolbar;
//
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.editor.ScrollType;
//import com.intellij.openapi.project.Project;
//import com.intellij.psi.PsiClass;
//import com.intellij.psi.PsiFile;
//import com.intellij.psi.PsiJavaFile;
//import com.intellij.psi.PsiMethod;
//import com.intellij.psi.util.PsiUtilBase;
//import com.lyflexi.feignx.entity.HttpMappingInfo;
//import com.lyflexi.feignx.utils.ToolBarUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.jetbrains.annotations.NotNull;
//
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import java.awt.*;
//import java.awt.datatransfer.DataFlavor;
//import java.awt.datatransfer.Transferable;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @Description:
// * @Author: lyflexi
// * @project: feignx-plugin
// * @Date: 2024/10/18 16:05
// */
//public class SearchControllerAction extends AnAction {
//
//    //仅弹出一个窗口
//    private JFrame searchFrame = null;
//
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent event) {
//        Project project = event.getProject();
//        // 扫描项目中的Java源文件
//        List<HttpMappingInfo> httpMappingInfos = ToolBarUtils.scanAllProjectControllerInfo(project);
//        // 执行搜索
//        startSearch(httpMappingInfos);
//    }
//    private void startSearch(List<HttpMappingInfo> httpMappingInfos) {
//        if(searchFrame == null){
//            searchFrame = new JFrame("搜索");
//        }
////        JFrame searchFrame = new JFrame("搜索");
//        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        searchFrame.setSize(1000, 400);
//
//        JTextArea resultTextArea = new JTextArea();
//        resultTextArea.setText(" 按回车跳转第一个接口\n 可以通过空格+数字传递行数，例如：\n /user/list 2\n 可以自定义快捷键");
//        resultTextArea.setEditable(false);
//        JScrollPane scrollPane = new JScrollPane(resultTextArea);
//
//        JTextField searchField = new JTextField();
//        searchField.setToolTipText("按回车跳转");
//        searchField.setEditable(true); // 启用编辑功能
//        searchField.setTransferHandler(new TextFieldTransferHandler()); // 设置默认的传输处理程序
//        searchField.setPreferredSize(new Dimension(300, 30));
//        searchField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                performSearch();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                performSearch();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                performSearch();
//            }
//
//            private void performSearch() {
//                String searchText = searchField.getText().strip();
//                List<HttpMappingInfo> searchResults = searchControllerInfos(httpMappingInfos, searchText.split(" ")[0]);
//                showControllerInfo(searchResults, resultTextArea);
//            }
//        });
//
//        searchField.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    navigateToFirstControllerCode(httpMappingInfos, searchField.getText().strip());
//                }
//            }
//        });
//
//        JPanel contentPane = new JPanel(new BorderLayout());
//        JPanel searchPanel = new JPanel(new FlowLayout());
//        searchPanel.add(new JLabel("Search:"));
//        searchPanel.add(searchField);
//        contentPane.add(searchPanel, BorderLayout.NORTH);
//        contentPane.add(scrollPane, BorderLayout.CENTER);
//
//        searchFrame.setContentPane(contentPane);
//        searchFrame.setVisible(true);
//    }
//    static class TextFieldTransferHandler extends TransferHandler {
//        @Override
//        public boolean canImport(TransferSupport support) {
//            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
//        }
//
//        @Override
//        public boolean importData(TransferSupport support) {
//            if (!canImport(support)) {
//                return false;
//            }
//
//            Transferable transferable = support.getTransferable();
//            try {
//                String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
//                JTextField textField = (JTextField) support.getComponent();
//                textField.setText(data);
//                return true;
//            } catch (Exception e) {
//                return false;
//            }
//        }
//    }
//
//    private void showControllerInfo(List<HttpMappingInfo> httpMappingInfos, JTextArea resultTextArea) {
//        resultTextArea.setText(ToolBarUtils.showResult(httpMappingInfos));
//        resultTextArea.setCaretPosition(0);
//    }
//
//
//    private List<HttpMappingInfo> searchControllerInfos(List<HttpMappingInfo> httpMappingInfos, String searchText) {
//        return httpMappingInfos.stream()
//                .filter(info -> isMatched(info, searchText))
//                .collect(Collectors.toList());
//    }
//    private void navigateToFirstControllerCode(List<HttpMappingInfo> httpMappingInfos, String searchText) {
//        List<HttpMappingInfo> searchResults = null;
//        int i = 0;
//        String[] s = searchText.split(" ");
//        if(s.length == 1){
//            searchResults = searchControllerInfos(httpMappingInfos, searchText);
//        }else if(s.length == 2){
//            searchResults = searchControllerInfos(httpMappingInfos, s[0]);
//            i = Integer.parseInt(s[1])-1;
//        }
//        if (CollectionUtils.isNotEmpty(searchResults)) {
//            HttpMappingInfo iResult = searchResults.get(i);
//            navigateToControllerCode(iResult);
//        }
//    }
//    private void navigateToControllerCode(HttpMappingInfo httpMappingInfo) {
//        PsiFile file = httpMappingInfo.getPsiMethod().getContainingFile();
//        if (file instanceof PsiJavaFile) {
//            PsiJavaFile javaFile = (PsiJavaFile) file;
//            PsiClass[] classes = javaFile.getClasses();
//            if (classes.length > 0) {
//                PsiClass psiClass = classes[0];
//                psiClass.navigate(true);
//                // 定位到对应的方法
//                PsiMethod targetMethod = httpMappingInfo.getPsiMethod();
//                if (targetMethod != null) {
//                    int offset = targetMethod.getTextOffset();
//                    //Invocation of unresolved method PsiEditorUtil.findEditor(PsiElement) (1 problem)
//                    //Method SearchControllerAction.navigateToControllerCode(...) contains an invokestatic instruction
//                    // referencing an unresolved method PsiEditorUtil.findEditor(PsiElement).
//                    // This can lead to NoSuchMethodError exception at runtime.
////                    Editor editor = PsiEditorUtil.findEditor(file);
//                    //临时替换为PsiUtilBase.findEditor(file);
//                    Editor editor = PsiUtilBase.findEditor(file);
//                    if (editor != null) {
//                        editor.getCaretModel().moveToOffset(offset);
//                        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
//                    }
//                }
//            }
//        }
//    }
//    // 添加辅助方法isMatched：
//    private boolean isMatched(HttpMappingInfo httpMappingInfo, String searchText) {
//        String lowerCase = searchText.toLowerCase();
//        if(httpMappingInfo.getRequestMethod().toLowerCase().contains(lowerCase)){
//            return true;
//        }
//        if(httpMappingInfo.getPath().toLowerCase().contains(lowerCase)){
//            return true;
//        }
//        if(httpMappingInfo.getSwaggerInfo() != null && httpMappingInfo.getSwaggerInfo().toLowerCase().contains(lowerCase)){
//            return true;
//        }
//        if(httpMappingInfo.getSwaggerNotes() != null && httpMappingInfo.getSwaggerNotes().toLowerCase().contains(lowerCase)){
//            return true;
//        }
//        return false;
//    }
//
//}