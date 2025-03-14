package com.lyflexi.feignx.entity;

import com.intellij.psi.*;
import com.lyflexi.feignx.enums.SpringBootMethodAnnotation;
import com.lyflexi.feignx.utils.AnnotationParserUtils;

import java.io.Serializable;

import static com.lyflexi.feignx.enums.SpringBootMethodAnnotation.REQUEST_MAPPING;


/**
 * @Description: 方法级别的HttpMappingInfo
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:51
 */
public class HttpMappingInfo implements Serializable {
    /**
     * full url path
     */
    private String path = "";
    /**
     * psiMethod
     */
    private PsiMethod psiMethod;
    /**
     * request method
     */
    private String requestMethod;
    /**
     * swagger info
     */
    private String swaggerInfo = "";
    /**
     * swagger notes
     */
    private String swaggerNotes = "";

    public HttpMappingInfo() {
    }

    public HttpMappingInfo(String path, String swaggerInfo, String swaggerNotes, PsiMethod psiMethod) {
        this.path = path;
        this.swaggerInfo = swaggerInfo;
        this.swaggerNotes = swaggerNotes;
        this.psiMethod = psiMethod;
    }

    public String getPath() {
        return this.path;
    }

    public String getSwaggerInfo() {
        return this.swaggerInfo;
    }

    public String getSwaggerNotes() {
        return this.swaggerNotes;
    }

    public PsiMethod getPsiMethod() {
        return this.psiMethod;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSwaggerInfo(String swaggerInfo) {
        this.swaggerInfo = swaggerInfo;
    }

    public void setSwaggerNotes(String swaggerNotes) {
        this.swaggerNotes = swaggerNotes;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof HttpMappingInfo)) return false;
        final HttpMappingInfo other = (HttpMappingInfo) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$path = this.getPath();
        final Object other$path = other.getPath();
        if (this$path == null ? other$path != null : !this$path.equals(other$path)) return false;
        final Object this$swaggerInfo = this.getSwaggerInfo();
        final Object other$swaggerInfo = other.getSwaggerInfo();
        if (this$swaggerInfo == null ? other$swaggerInfo != null : !this$swaggerInfo.equals(other$swaggerInfo))
            return false;
        final Object this$swaggerNotes = this.getSwaggerNotes();
        final Object other$swaggerNotes = other.getSwaggerNotes();
        if (this$swaggerNotes == null ? other$swaggerNotes != null : !this$swaggerNotes.equals(other$swaggerNotes))
            return false;
        final Object this$method = this.getPsiMethod();
        final Object other$method = other.getPsiMethod();
        if (this$method == null ? other$method != null : !this$method.equals(other$method)) return false;
        final Object this$requestMethod = this.getRequestMethod();
        final Object other$requestMethod = other.getRequestMethod();
        if (this$requestMethod == null ? other$requestMethod != null : !this$requestMethod.equals(other$requestMethod))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HttpMappingInfo;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $path = this.getPath();
        result = result * PRIME + ($path == null ? 43 : $path.hashCode());
        final Object $swaggerInfo = this.getSwaggerInfo();
        result = result * PRIME + ($swaggerInfo == null ? 43 : $swaggerInfo.hashCode());
        final Object $swaggerNotes = this.getSwaggerNotes();
        result = result * PRIME + ($swaggerNotes == null ? 43 : $swaggerNotes.hashCode());
        final Object $method = this.getPsiMethod();
        result = result * PRIME + ($method == null ? 43 : $method.hashCode());
        final Object $requestMethod = this.getRequestMethod();
        result = result * PRIME + ($requestMethod == null ? 43 : $requestMethod.hashCode());
        return result;
    }

    public String toString() {
        return "ControllerInfo(path=" + this.getPath() + ", swaggerInfo=" + this.getSwaggerInfo() + ", swaggerNotes=" + this.getSwaggerNotes() + ", method=" + this.getPsiMethod() + ", requestMethod=" + this.getRequestMethod() + ")";
    }
    /**
     * 根据方法提取完整的接口信息
     *
     * @param parentPath 父路径
     * @param method     方法
     * @return {@link HttpMappingInfo}
     */
    public static HttpMappingInfo of(String parentPath, PsiMethod method) {
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
                        httpMappingInfo.setRequestMethod(AnnotationParserUtils.getRequestMethodFromMethodName(methodName));
                    }
                }
                return AnnotationParserUtils.getValue(annotation, httpMappingInfo, method);
            } else if (SpringBootMethodAnnotation.getByQualifiedName(annotationName) != null) {
                // 处理其他常用注解
                SpringBootMethodAnnotation requestMethod = SpringBootMethodAnnotation.getByQualifiedName(annotationName);
                httpMappingInfo.setRequestMethod(requestMethod != null ? requestMethod.methodName() : "REQUEST");
                return AnnotationParserUtils.getValue(annotation, httpMappingInfo, method);
            }

        }
        return null;
    }
}