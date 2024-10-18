package org.lyflexi.cloudfeignapi;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 13:53
 */
@Data
public class User implements Serializable {

    private Long id;
    private String name;

    public User() {}

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}