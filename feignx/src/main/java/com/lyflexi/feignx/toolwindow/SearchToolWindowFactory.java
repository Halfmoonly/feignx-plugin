package com.lyflexi.feignx.toolwindow;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.lyflexi.feignx.toolbar.RefreshCacheAction;
import com.lyflexi.feignx.entity.HttpMappingInfo;
import com.lyflexi.feignx.utils.ToolBarUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 19:57
 */
public class SearchToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SearchToolWindowContent toolWindowContent = new SearchToolWindowContent(toolWindow);
        Content content = ContentFactory.SERVICE.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private class SearchToolWindowContent {

        JTextArea resultTextArea = new JTextArea();


        private final JPanel contentPanel = new JPanel();

        public SearchToolWindowContent(ToolWindow toolWindow) {
            contentPanel.setLayout(new BorderLayout(0, 20));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
            contentPanel.add(createControlsPanel(toolWindow), BorderLayout.PAGE_START);
        }


        @NotNull
        private JPanel createControlsPanel(ToolWindow toolWindow) {
            JPanel parentPanel = new JPanel();
            // 扫描项目中的Java源文件
            java.util.List<HttpMappingInfo> httpMappingInfos = ToolBarUtils.scanAllProjectControllerInfo();
            // 执行搜索
            parentPanel.add(startSearch(httpMappingInfos));

            return parentPanel;
        }

        private JPanel startSearch(java.util.List<HttpMappingInfo> httpMappingInfos) {
            resultTextArea = new JTextArea();
            resultTextArea.setText(" 刚开始打开项目时idea建立索引前功能不可用！\n 按回车跳转第一个接口\n 可以通过空格+数字传递行数，例如：\n /user/list 2\n 可以自定义快捷键");
            resultTextArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(resultTextArea);

            JTextField searchField = new JTextField();
            searchField.setToolTipText("按回车跳转");
            searchField.setEditable(true); // 启用编辑功能
            searchField.setTransferHandler(new TextFieldTransferHandler()); // 设置默认的传输处理程序

            searchField.setPreferredSize(new Dimension(300, 30));
            searchField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    performSearch(httpMappingInfos);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    performSearch(httpMappingInfos);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    performSearch(httpMappingInfos);
                }

                private void performSearch(java.util.List<HttpMappingInfo> httpMappingInfos) {
                    String searchText = searchField.getText().strip();
                    if(httpMappingInfos.isEmpty()){
                        httpMappingInfos = ToolBarUtils.getControllerInfos();
                    }
                    java.util.List<HttpMappingInfo> searchResults = searchControllerInfos(httpMappingInfos, searchText.split(" ")[0]);
                    showControllerInfo(searchResults, resultTextArea);
                }
            });
            searchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        navigateToFirstControllerCode(httpMappingInfos, searchField.getText().strip());
                    }
                }
            });

            JPanel contentPane = new JPanel(new BorderLayout());
            JPanel searchPanel = new JPanel(new FlowLayout());
            searchPanel.add(new JLabel("Search:"));
            searchPanel.add(searchField);
            JButton refreshDateAndTimeButton = new JButton("刷新");
            refreshDateAndTimeButton.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    refreshData();
                });
            });

            searchPanel.add(refreshDateAndTimeButton);
//            JButton hideToolWindowButton = new JButton("Hide");
//            hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
//            searchPanel.add(hideToolWindowButton);
            contentPane.add(searchPanel, BorderLayout.NORTH);

            contentPane.add(scrollPane, BorderLayout.CENTER);
            return  contentPane;
        }

        private void refreshData() {
            new RefreshCacheAction().refresh();
        }

        public JPanel getContentPanel() {
            return contentPanel;
        }

    }

    private static void showControllerInfo(java.util.List<HttpMappingInfo> httpMappingInfos, JTextArea resultTextArea) {
        resultTextArea.setText(ToolBarUtils.showResult(httpMappingInfos));
        resultTextArea.setCaretPosition(0);
    }

    static class TextFieldTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable transferable = support.getTransferable();
            try {
                String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                JTextField textField = (JTextField) support.getComponent();
                textField.setText(data);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }


    private static java.util.List<HttpMappingInfo> searchControllerInfos(java.util.List<HttpMappingInfo> httpMappingInfos, String searchText) {
        return httpMappingInfos.stream()
                .filter(info -> isMatched(info, searchText))
                .collect(Collectors.toList());
    }
    private static void navigateToFirstControllerCode(java.util.List<HttpMappingInfo> httpMappingInfos, String searchText) {
        List<HttpMappingInfo> searchResults = null;
        int i = 0;
        String[] s = searchText.split(" ");
        if(s.length == 1){
            searchResults = searchControllerInfos(httpMappingInfos, searchText);
        }else if(s.length == 2){
            searchResults = searchControllerInfos(httpMappingInfos, s[0]);
            i = Integer.parseInt(s[1])-1;
        }
        if (CollectionUtils.isNotEmpty(searchResults)) {
            HttpMappingInfo iResult = searchResults.get(i);
            navigateToControllerCode(iResult);
        }
    }

    private static void navigateToControllerCode(HttpMappingInfo httpMappingInfo) {
        PsiFile file = httpMappingInfo.getPsiMethod().getContainingFile();
        if (file instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) file;
            PsiClass[] classes = javaFile.getClasses();
            if (classes.length > 0) {
                PsiClass psiClass = classes[0];
                psiClass.navigate(true);
                // 定位到对应的方法
                PsiMethod targetMethod = httpMappingInfo.getPsiMethod();
                if (targetMethod != null) {
                    int offset = targetMethod.getTextOffset();
                    Editor editor = PsiUtilBase.findEditor(file);
                    if (editor != null) {
                        editor.getCaretModel().moveToOffset(offset);
                        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
                    }
                }
            }
        }
    }

    private static boolean isMatched(HttpMappingInfo httpMappingInfo, String searchText) {
        String lowerCase = searchText.toLowerCase();
        if(httpMappingInfo.getRequestMethod().toLowerCase().contains(lowerCase)){
            return true;
        }
        if(httpMappingInfo.getPath().toLowerCase().contains(lowerCase)){
            return true;
        }
        if(httpMappingInfo.getSwaggerInfo() != null && httpMappingInfo.getSwaggerInfo().toLowerCase().contains(lowerCase)){
            return true;
        }
        if(httpMappingInfo.getSwaggerNotes() != null && httpMappingInfo.getSwaggerNotes().toLowerCase().contains(lowerCase)){
            return true;
        }
        return false;
    }

}