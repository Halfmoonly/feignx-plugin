package com.lyflexi.feignx.model;

import com.intellij.psi.PsiMethod;

import java.io.Serializable;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:51
 */
public class HttpMappingInfo implements Serializable {
    private String path="";
    private String swaggerInfo="";
    private String swaggerNotes="";
    private PsiMethod method;

    private String requestMethod;

    public HttpMappingInfo() {    }

    public HttpMappingInfo(String path, String swaggerInfo, String swaggerNotes, PsiMethod method) {
        this.path = path;
        this.swaggerInfo = swaggerInfo;
        this.swaggerNotes = swaggerNotes;
        this.method = method;
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

    public PsiMethod getMethod() {
        return this.method;
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

    public void setMethod(PsiMethod method) {
        this.method = method;
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
        final Object this$method = this.getMethod();
        final Object other$method = other.getMethod();
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
        final Object $method = this.getMethod();
        result = result * PRIME + ($method == null ? 43 : $method.hashCode());
        final Object $requestMethod = this.getRequestMethod();
        result = result * PRIME + ($requestMethod == null ? 43 : $requestMethod.hashCode());
        return result;
    }

    public String toString() {
        return "ControllerInfo(path=" + this.getPath() + ", swaggerInfo=" + this.getSwaggerInfo() + ", swaggerNotes=" + this.getSwaggerNotes() + ", method=" + this.getMethod() + ", requestMethod=" + this.getRequestMethod() + ")";
    }
}