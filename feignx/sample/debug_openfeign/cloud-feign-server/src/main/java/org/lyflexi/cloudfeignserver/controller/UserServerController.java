package org.lyflexi.cloudfeignserver.controller;

import org.lyflexi.cloudfeignapi.User;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    /**
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/user/get/{id}")
    public User getUserById(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    /**
     * hhh
     * @param id
     * @return
     */
    @GetMapping(value = "/user/update2/{id}")
    /**
     * 哈哈哈
     */
    public User update(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    @GetMapping(value = "/user/del/{id}")
    public User del(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/getfather/{id}")
    /**
     *
     */
    public User getfather(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/getmather/{id}")
    /**
     *
     */
    public User getmather(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/clipboard/{id}")
    public User clipboard(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/clipboard2/{id}")
    public User clipboard2(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }
    @GetMapping(value = "/user/clipboard3/{id}")
    /**
     *
     */
    public User clipboard3(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/clipboard4/{id}")
    /**
     *
     */
    @Deprecated
    /**
     *
     */
    public User clipboard4(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/optimizedCache/{id}")
    @Deprecated
    /**
     *
     */
    public User optimizedCache(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }
    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/optimizedCache2/{id}")
    @Deprecated
    /**
     *
     */
    public User optimizedCache2(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/optimizedCache3/{id}")
    public User optimizedCache3(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

    @GetMapping(value = "/user/parallelScan/{id}")
    public User parallelScan(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }
    @GetMapping(value = "/user/parallelScan2/{id}")
    public User parallelScan2(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

}