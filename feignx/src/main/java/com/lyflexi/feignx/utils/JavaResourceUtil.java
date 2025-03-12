package com.lyflexi.feignx.utils;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.enums.SpringRequestMethodAnnotation;
import com.lyflexi.feignx.model.HttpMappingInfo;
import com.lyflexi.feignx.properties.ConfigReader;
import com.lyflexi.feignx.properties.ServerParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.lyflexi.feignx.enums.SpringControllerClassAnnotation.CONTROLLER;
import static com.lyflexi.feignx.enums.SpringControllerClassAnnotation.RESTCONTROLLER;
import static com.lyflexi.feignx.enums.SpringRequestMethodAnnotation.REQUEST_MAPPING;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:50
 */
public class JavaResourceUtil {


    private static final String SPRINGBOOT_SERVER_PATH= "server.servlet.context-path";
    private static final String SPRINGMVC_PATH= "spring.mvc.servlet.path";

    private JavaResourceUtil(){};


    public static List<HttpMappingInfo> scanControllerPaths(Project project) {
        PsiManager psiManager = PsiManager.getInstance(project);
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
        PsiPackage rootPackage = JavaPsiFacade.getInstance(psiManager.getProject()).findPackage("");
        // 检查是否在 Dumb 模式下，以避免在项目构建期间执行代码
        if (DumbService.isDumb(project)) {
            return Collections.emptyList();
        }
//        List<Pair<String, ControllerInfo>> cachedControllerInfos = MyCacheManager.getCacheData(project);
//        if (CollectionUtils.isNotEmpty(cachedControllerInfos)) {
//            return cachedControllerInfos.stream()
//                    .map(Pair::getRight)
//                    .collect(Collectors.toList());
//        }
//        cachedControllerInfos = new ArrayList<>();

        List<Pair<String, HttpMappingInfo>> cachedControllerInfos = new ArrayList<>();
        List<HttpMappingInfo> httpMappingInfos = new ArrayList<>();

        // 获取项目中的所有Java源文件
        List<PsiClass> javaFiles = getAllClasses(rootPackage, searchScope);

        for (PsiClass psiClass : javaFiles) {
            // 判断类是否带有@Controller或@RestController注解
            if (isControllerClass(psiClass)) {
                StringBuilder parentPath = new StringBuilder();
                String serverPath = extractSpringProperties(psiClass,project,SPRINGBOOT_SERVER_PATH);
                String mvcPath = extractSpringProperties(psiClass,project,SPRINGMVC_PATH);
                String controllerPath = extractControllerPath(psiClass);
                parentPath.append(serverPath).append(mvcPath).append(controllerPath);
                // 解析类中的方法，提取接口路径和Swagger注解信息
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod method : methods) {
                    HttpMappingInfo httpMappingInfo = extractControllerInfo(parentPath.toString(), method);
                    if (httpMappingInfo != null) {
                        // 设置方法信息
                        httpMappingInfo.setMethod(method);
                        httpMappingInfos.add(httpMappingInfo);
                    }
                }
            }
        }

        // 将结果添加到缓存中
        cachedControllerInfos.addAll(httpMappingInfos.stream()
                .map(info -> Pair.of(info.getPath(), info))
                .collect(Collectors.toList()));
        BilateralCacheManager.setControllerCacheData(project, cachedControllerInfos);
        return httpMappingInfos;
    }
    /**
     * resolve：
     * eg.server.servlet.context-path=/hello 
     * eg .spring.mvc.servlet.path=/world
     * @param psiClass
     * @param project
     * @param configKey
     * @return 
     */

    public static String extractSpringProperties(PsiClass psiClass, Project project, String configKey) {
        Optional<PsiDirectory> serviceModuleDirectory = ServerParser.getServiceModuleResourcesDirectory(psiClass, project);
        String propertyPath = null;

        if (serviceModuleDirectory.isPresent()) {
            // 读取 properties 文件
            Properties properties = ConfigReader.readProperties(serviceModuleDirectory.get());
            if (properties != null && properties.containsKey(configKey)) {
                propertyPath = properties.getProperty(configKey);
            }

            // 如果在 properties 文件中未找到，继续在 yml 或 yaml 文件中查找
            if (propertyPath == null) {
                Map<String, Object> yml = ConfigReader.readYmlOrYaml(serviceModuleDirectory.get());
                if (yml != null) {
                    propertyPath = extractValueFromYml(yml, configKey);
                }
            }
        }

        return propertyPath==null?"":propertyPath;
    }

    // 从 YAML Map 中提取目标值，支持嵌套键
    private static String extractValueFromYml(Map<String, Object> yml, String configKey) {
        String[] keys = configKey.split("\\.");
        Object value = yml;

        for (String key : keys) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(key);
            } else {
                return null;
            }
        }

        return value != null ? value.toString() : null;
    }



    public static List<PsiClass> getAllClasses(PsiPackage rootPackage, GlobalSearchScope searchScope) {
        List<PsiClass> javaFiles = new ArrayList<>();
        processPackage(rootPackage, searchScope, javaFiles);
        return javaFiles;
    }

    private static void processPackage(PsiPackage psiPackage, GlobalSearchScope searchScope, List<PsiClass> classesToCheck) {
        for (PsiClass psiClass : psiPackage.getClasses()) {
            classesToCheck.add(psiClass);
        }

        for (PsiPackage subPackage : psiPackage.getSubPackages(searchScope)) {
            processPackage(subPackage, searchScope, classesToCheck);
        }
    }


    public static String showResult(List<HttpMappingInfo> httpMappingInfos) {
        StringBuilder message = new StringBuilder();
        // 表头信息
        int i = 0;
        message.append(String.format("%-3s", "Num")).append("\t");
        message.append(String.format("%-7s", "Request")).append("\t");
        message.append(String.format("%-52s", "Path")).append("\t");
        message.append(String.format("%-25s", "Swagger Info")).append("\t");
        message.append(String.format("%-25s", "Swagger Notes")).append("\n");
        for (HttpMappingInfo info : httpMappingInfos) {
            message.append(String.format("%-3d", ++i)).append("\t");
            message.append(String.format("%-7s", info.getRequestMethod())).append("\t");
            // 接口路径
            message.append(String.format("%-52s", info.getPath())).append("\t");
            // Swagger Info
            message.append(String.format("%-25s", info.getSwaggerInfo())).append("\t");
            // Swagger Notes
            message.append(String.format("%-25s", info.getSwaggerNotes())).append("\n");
        }
        return message.toString();
    }



    public static boolean isControllerClass(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String annotationName = annotation.getQualifiedName();
            if (annotationName != null && (annotationName.equals(CONTROLLER.getQualifiedName())
                    || annotationName.equals(RESTCONTROLLER.getQualifiedName()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据方法提取完整的接口信息
     *
     * @param parentPath 父路径
     * @param method     方法
     * @return {@link HttpMappingInfo}
     */
    public static HttpMappingInfo extractControllerInfo(String parentPath, PsiMethod method) {
        HttpMappingInfo httpMappingInfo = new HttpMappingInfo();
        httpMappingInfo.setPath(parentPath);
        PsiAnnotation[] annotations = method.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String annotationName = annotation.getQualifiedName();
            // 处理 @RequestMapping 注解
            if (annotationName != null && annotationName.equals(REQUEST_MAPPING.getQualifiedName())) {
                httpMappingInfo.setRequestMethod("REQUEST");
                // 提取 method 属性值
                PsiAnnotationMemberValue methodValue = annotation.findAttributeValue("method");
                if (methodValue instanceof PsiReferenceExpression) {
                    PsiElement resolvedElement = ((PsiReferenceExpression) methodValue).resolve();
                    if (resolvedElement instanceof PsiField) {
                        String methodName = ((PsiField) resolvedElement).getName();
                        // 使用字典映射设置请求方法
                        httpMappingInfo.setRequestMethod(getRequestMethodFromMethodName(methodName));
                    }
                }
                return getValue(annotation, httpMappingInfo, method);
            } else if (SpringRequestMethodAnnotation.getByQualifiedName(annotationName) != null) {
                // 处理其他常用注解
                SpringRequestMethodAnnotation requestMethod = SpringRequestMethodAnnotation.getByQualifiedName(annotationName);
                httpMappingInfo.setRequestMethod(requestMethod !=null? requestMethod.methodName(): "REQUEST");
                return getValue(annotation, httpMappingInfo, method);
            }

        }
        return null;
    }

    /**
     * 提取Controller类文件的接口路径
     *
     * @param psiClass psi类
     * @return {@link String}
     */
    public static String extractControllerPath(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String annotationName = annotation.getQualifiedName();
            if (REQUEST_MAPPING.getQualifiedName().equals(annotationName)) {
                return getValueFromPsiAnnotation(annotation);
            }
        }
        return "";
    }
    public static String getValueFromPsiAnnotation(PsiAnnotation annotation){
        PsiAnnotationParameterList parameterList = annotation.getParameterList();
        PsiNameValuePair[] attributes = parameterList.getAttributes();
        for (PsiNameValuePair attribute : attributes) {
            String attributeName = attribute.getAttributeName();
            if ("value".equals(attributeName) || "path".equals(attributeName)) {
                PsiAnnotationMemberValue attributeValue = attribute.getValue();
                if (attributeValue instanceof PsiLiteralExpression) {
                    Object value = ((PsiLiteralExpression) attributeValue).getValue();
                    if (value instanceof String) {
                        return ((String) value).startsWith("/") ? (String) value : "/" + value;
                    }
                }
            }
        }
        return "";

    }

    /**
     * 获得价值
     * 路径：类文件接口路径+方法接口路径
     *
     * @param httpMappingInfo 控制器信息
     * @param method         方法
     * @param annotation     注释
     * @return {@link HttpMappingInfo}
     */
    public static HttpMappingInfo getValue(PsiAnnotation annotation, HttpMappingInfo httpMappingInfo, PsiMethod method) {
        String path = getValueFromPsiAnnotation(annotation);
        httpMappingInfo.setPath(httpMappingInfo.getPath() + path);
        extractSwaggerInfo(method, httpMappingInfo);
        return httpMappingInfo;
    }
    private static void extractSwaggerInfo(PsiMethod method, HttpMappingInfo httpMappingInfo) {
        PsiModifierList methodModifierList = method.getModifierList();
        PsiAnnotation swaggerAnnotation = methodModifierList.findAnnotation("io.swagger.annotations.ApiOperation");
        if (swaggerAnnotation != null) {
            extractSwaggerValue(swaggerAnnotation, "value", httpMappingInfo::setSwaggerInfo);
            extractSwaggerValue(swaggerAnnotation, "notes", httpMappingInfo::setSwaggerNotes);
        }
    }

    private static void extractSwaggerValue(PsiAnnotation swaggerAnnotation, String attributeName, Consumer<String> setter) {
        PsiAnnotationMemberValue attributeValue = swaggerAnnotation.findAttributeValue(attributeName);
        if (attributeValue instanceof PsiLiteralExpression) {
            Object value = ((PsiLiteralExpression) attributeValue).getValue();
            if (value instanceof String) {
                setter.accept((String) value);
            }
        }
    }

    private static String getRequestMethodFromMethodName(String methodName) {
        // 使用字典映射替代多个条件分支
        Map<String, String> methodMappings = new HashMap<>();
        methodMappings.put("GET", "GET");
        methodMappings.put("POST", "POST");
        methodMappings.put("PUT", "PUT");
        methodMappings.put("DELETE", "DELETE");
        return methodMappings.getOrDefault(methodName, "REQUEST");
    }

    /**
     * 目前只能跳转到当前项目下的文件否则会报Element from alien project错误
     *
     * @param psiMethod psi方法
     * @return {@link List}<{@link PsiElement}>
     */
    public static List<PsiElement> process(PsiMethod psiMethod) {
        List<PsiElement> elementList = new ArrayList<>();

        // 获取当前项目
        Project project = psiMethod.getProject();

        List<HttpMappingInfo> httpMappingInfos = JavaResourceUtil.scanControllerPaths(project);

        if (httpMappingInfos != null) {
            // 遍历 Controller 类的所有方法
            for (HttpMappingInfo httpMappingInfo : httpMappingInfos) {
                if (match2C(httpMappingInfo, psiMethod)) {
                    elementList.add(httpMappingInfo.getMethod());
                }
            }
        }

        return elementList;
    }

//    private static boolean isMethodMatch(HttpMappingInfo httpMappingInfo, PsiMethod feignMethod) {
//        PsiClass psiClass = feignMethod.getContainingClass();
//        HttpMappingInfo feignInfo = JavaSourceFileUtil.extractControllerInfo(extractFeignParentPathFromClassAnnotation(psiClass), feignMethod);
//        if(feignInfo != null){
//            String path = feignInfo.getPath();
//            if(StringUtils.isNotBlank(path)){
//                return path.equals(httpMappingInfo.getPath());
//            }
//        }
//        return false;
//    }


    private static boolean match2C(HttpMappingInfo controllerInfo, PsiMethod feignMethod) {
        String feignPath = BilateralCacheManager.getFeignPath(feignMethod);
        if (StringUtils.isNotBlank(feignPath)) {
            return feignPath.equals(controllerInfo.getPath());
        }
        return false;
    }

    /**
     * 元素是否为FeignClient下的方法
     * 当传入的是PsiMethod，则该方法失效
     * @param element 元素
     * @return boolean
     */
//    public static boolean isElementWithinFeign(PsiElement element) {
//        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
//            PsiClass psiClass = (PsiClass) element;
//
//            // 检查类上是否存在 FeignClient 注解
//            PsiAnnotation feignAnnotation = psiClass.getAnnotation("org.springframework.cloud.openfeign.FeignClient");
//            if (feignAnnotation != null) {
//                return true;
//            }
//        }
//        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
//        return type != null && isElementWithinFeign(type);
//    }

    /**
     * 元素是否为FeignClient下的方法
     *
     * 更保险的方式是
     * 当传进去的是 PsiMethod，需要手动判断所有的getParentOfType是否含有注解org.springframework.cloud.openfeign.FeignClient
     * @param element
     * @return
     */
    public static boolean isElementWithinFeign(PsiElement element) {
        PsiClass psiClass = null;

        if (element instanceof PsiClass) {
            psiClass = (PsiClass) element;
        } else {
            psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        }

        if (psiClass != null && psiClass.isInterface()) {
            PsiAnnotation[] annotations = psiClass.getModifierList().getAnnotations();
            for (PsiAnnotation annotation : annotations) {
                if ("org.springframework.cloud.openfeign.FeignClient".equals(annotation.getQualifiedName())) {
                    return true;
                }
            }
        }

        return false;
    }
    /**
     * 元素是否为Controller下的方法
     *
     * @param element 元素
     * @return boolean
     */
    public static boolean isElementWithinController(PsiElement element) {
        if (element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;

            // 检查类上是否存在 CONTROLLER/RESTCONTROLLER 注解
            return JavaResourceUtil.isControllerClass(psiClass);
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return type != null && isElementWithinController(type);
    }


    /**
     * 扫描Feign接口信息添加到缓存里面
     *
     * @param project 项目
     * @return {@link List}<{@link HttpMappingInfo}>
     */
    public static List<HttpMappingInfo> scanFeignInterfaces(Project project) {
        PsiManager psiManager = PsiManager.getInstance(project);
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
        PsiPackage rootPackage = JavaPsiFacade.getInstance(psiManager.getProject()).findPackage("");

        // 检查是否在 Dumb 模式下，以避免在项目构建期间执行代码
        if (DumbService.isDumb(project)) {
            return Collections.emptyList();
        }

//        List<Pair<String, ControllerInfo>> feignCacheData = MyCacheManager.getFeignCacheData(project);
//        if (CollectionUtils.isNotEmpty(feignCacheData)) {
//            return feignCacheData.stream()
//                    .map(Pair::getRight)
//                    .collect(Collectors.toList());
//        }
//        feignCacheData = new ArrayList<>();
        List<Pair<String, HttpMappingInfo>> feignCacheData = feignCacheData = new ArrayList<>();
        List<HttpMappingInfo> feignInfos = new ArrayList<>();
        // 获取项目中的所有Java源文件
        List<PsiClass> javaFiles = getAllClasses(rootPackage, searchScope);
        for (PsiClass psiClass : javaFiles) {
            // 判断类是否带有@FeignClient注解
            if (isFeignInterface(psiClass)) {
                // 解析类中的方法，提取接口路径
                PsiMethod[] methods = psiClass.getMethods();
                String parentPath = extractFeignParentPathFromClassAnnotation(psiClass);
                for (PsiMethod method : methods) {
                    HttpMappingInfo feignInfo = extractControllerInfo(parentPath, method);
                    if (feignInfo != null) {
                        // 设置方法信息
                        feignInfo.setMethod(method);
                        feignInfos.add(feignInfo);
                    }
                }
            }
        }

        // 将结果添加到缓存中
        feignCacheData.addAll(feignInfos.stream()
                .map(info -> Pair.of(info.getPath(), info))
                .collect(Collectors.toList()));
        BilateralCacheManager.setFeignCacheData(project, feignCacheData);

        return feignInfos;
    }

    /**
     * 提取@FeignClient path属性值
     */
    public static String extractFeignParentPathFromClassAnnotation(PsiClass psiClass) {
        PsiAnnotation annotation = psiClass.getAnnotation("org.springframework.cloud.openfeign.FeignClient");
        PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
        for (PsiNameValuePair attribute : attributes) {
            if ("path".equals(attribute.getName())) {
                PsiAnnotationMemberValue value = attribute.getValue();
                if (value instanceof PsiLiteralExpression) {
                    String path = ((PsiLiteralExpression) value).getValue().toString();
                    // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
                    if(StringUtils.isBlank(path)){
                        return "";
                    }
                    // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
                    // 如果path不以/开头，添加/
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                    // @geasscai https://github.com/Halfmoonly/feignx-plugin/pull/9
                    // 如果path以/结尾，去除/
                    if (path.endsWith("/")) {
                        path = path.substring(0, path.length() - 1);
                    }
                    return path;
                }
            }
        }
        return "";
    }

    // 判断类是否带有@FeignClient注解
    private static boolean isFeignInterface(PsiClass psiClass) {
        PsiAnnotation annotation = psiClass.getAnnotation("org.springframework.cloud.openfeign.FeignClient");
        return annotation != null;
    }


    public static void exportToCSV(List<HttpMappingInfo> httpMappingInfos) {
        // 获取文件选择器
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("导出列表");

        // 显示文件选择器
        int result = fileChooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // 获取文件
        File file = fileChooser.getSelectedFile();

        // 创建 CSV 文件写入器
        try (FileWriter fileWriter = new FileWriter(file.getAbsolutePath() + ".csv")) {
            // 写入列头
            String[] columnNames = {"序号", "请求方法", "路径", "Swagger Info", "Swagger Notes"};
            fileWriter.write(String.join(",", columnNames) + "\n");

            // 写入列表数据
            Integer i = 0;
            for (HttpMappingInfo httpMappingInfo : httpMappingInfos) {
                i++;
                String[] data = {
                        i.toString(),
                        httpMappingInfo.getRequestMethod(),
                        httpMappingInfo.getPath(),
                        httpMappingInfo.getSwaggerInfo(),
                        httpMappingInfo.getSwaggerNotes()
                };
                fileWriter.write(String.join(",", data) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}