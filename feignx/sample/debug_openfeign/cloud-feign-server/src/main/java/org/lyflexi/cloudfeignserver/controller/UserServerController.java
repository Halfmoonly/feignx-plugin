package org.lyflexi.cloudfeignserver.controller;

import org.lyflexi.cloudfeignapi.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lyflexi
 * @project: debuginfo_jdkToFramework
 * @Date: 2024/7/27 14:09
 */
@RestController
public class UserServerController {

    @GetMapping(value = "/user/get/{id}")
    public User getUserById(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }
}