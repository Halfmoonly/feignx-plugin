package com.lyflexi.feignx.enums;

/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/10/18 14:53
 */
public enum SpringBootClassAnnotation {
    /**
     * RequestMapping
     */
    CONTROLLER("org.springframework.stereotype.Controller",""),
    /**
     * GetMapping
     */
    RESTCONTROLLER("org.springframework.web.bind.annotation.RestController","");

    private final String qualifiedName;
    private final String desc;

    SpringBootClassAnnotation(String qualifiedName, String desc) {
        this.qualifiedName = qualifiedName;
        this.desc = desc;
    }


    public String getQualifiedName() {
        return qualifiedName;
    }


    public String getDesc() {
        return desc;
    }
}