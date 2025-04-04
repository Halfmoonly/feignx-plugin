package org.lyflexi.cloudfeignserver.controller;

import org.lyflexi.cloudfeignapi.User;
import org.lyflexi.cloudfeignapi.UserApiConst;
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




    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/parallelScan3/{id}")
    public User parallelScan3(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }




    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/parallelScan4/{id}")
    public User parallelScan4(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }


    /**
     * parallelScan5. v5.2.1
     * @param id
     * @return
     */
    @GetMapping(value = "/user/parallelScan5/{id}")
    public User parallelScan5(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }



    @GetMapping(value = "/user/parallelScan7/{id}")
    public User parallelScan7(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }



    @GetMapping(value = "/user/parallelScan811/{id}")
    public User parallelScan8(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }


    /**
     *
     * @param id
     * @return
     */
    @GetMapping(UserApiConst.USER_CLIENT_PARALLEL_SCAN9_ID)
    public User parallelScan9(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }




    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/parallelScan110/{id}")
    public User parallelScan10(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }





    /**
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/parallelScan11/{id}")
    public User parallelScan11(@PathVariable("id") Long id)
    {
        return new User(id, "user");
    }

}