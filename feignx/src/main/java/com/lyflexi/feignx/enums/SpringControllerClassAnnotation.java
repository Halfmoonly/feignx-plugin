package com.lyflexi.feignx.enums;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:53
 */
public enum SpringControllerClassAnnotation {
    /**
     * RequestMapping
     */
    CONTROLLER("org.springframework.stereotype.Controller"),
    /**
     * GetMapping
     */
    RESTCONTROLLER("org.springframework.web.bind.annotation.RestController");

    private final String qualifiedName;

    SpringControllerClassAnnotation(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }


    public static SpringControllerClassAnnotation getByShortName(String requestMapping) {
        for (SpringControllerClassAnnotation springRequestAnnotation : SpringControllerClassAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().endsWith(requestMapping)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }
}