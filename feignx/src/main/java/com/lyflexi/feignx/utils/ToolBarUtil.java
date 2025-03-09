package com.lyflexi.feignx.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.lyflexi.feignx.model.HttpMappingInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: hmly
 * @Date: 2025/3/8 9:46
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description:
 */
public class ToolBarUtil {
    private static List<HttpMappingInfo> httpMappingInfos = new ArrayList<HttpMappingInfo>();

    //返回所有的feign
    public static List<HttpMappingInfo> scanAllProjectFeignInfo() {
        Project[] openProjects = getOpenProjects();
        return Arrays.stream(openProjects)
                .flatMap(project -> JavaResourceUtil.scanFeignInterfaces(project).stream())
                .collect(Collectors.toList());
    }


    /**
     * 返回的controller
     * @return
     */
    public static List<HttpMappingInfo> scanAllProjectControllerInfo() {
        Project[] openProjects = getOpenProjects();
        List<HttpMappingInfo> collect = Arrays.stream(openProjects)
                .flatMap(project -> JavaResourceUtil.scanControllerPaths(project).stream())
                .collect(Collectors.toList());
        httpMappingInfos = collect;
        return collect;
    }


    /**
     * 获取所有打开的项目列表
     *
     * @return {@link Project[]}
     */
    private static Project[] getOpenProjects() {
        // 获取ProjectManager实例
        ProjectManager projectManager = ProjectManager.getInstance();
        // 获取所有打开的项目列表
        return projectManager.getOpenProjects();
    }

    public static List<HttpMappingInfo>  getControllerInfos(){
        return httpMappingInfos;
    }
}
