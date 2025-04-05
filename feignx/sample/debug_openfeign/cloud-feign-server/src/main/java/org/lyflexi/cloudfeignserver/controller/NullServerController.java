package org.lyflexi.cloudfeignserver.controller;

import org.lyflexi.cloudfeignapi.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NullServerController {

    @DeleteMapping(value = "/user/get/{id}")
    public User delb(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }





















}
