package com.lyflexi.feignx.utils;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.lyflexi.feignx.enums.SpringRequestMethodAnnotation;

import java.util.List;

/**
 * @Author: hmly
 * @Date: 2025/3/12 19:44
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description: 注解解析类
 */
public class MappingAnnotationUtil {

    public static PsiAnnotation findTargetMappingAnnotation(PsiMethod method) {
        List<String> targetAnnotations = SpringRequestMethodAnnotation.allQualifiedNames();

        for (PsiAnnotation annotation : method.getModifierList().getAnnotations()) {
            String qualifiedName = annotation.getQualifiedName();
            if (qualifiedName != null && targetAnnotations.contains(qualifiedName)) {
                return annotation;
            }
        }
        return null;
    }
}
