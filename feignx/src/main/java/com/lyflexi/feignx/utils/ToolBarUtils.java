package com.lyflexi.feignx.utils;

import com.intellij.openapi.project.Project;
import com.lyflexi.feignx.entity.HttpMappingInfo;

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
public class ToolBarUtils {
    private static List<HttpMappingInfo> httpMappingInfos = new ArrayList<HttpMappingInfo>();

    //返回所有的feign
    public static List<HttpMappingInfo> scanAllProjectFeignInfo() {
        Project[] openProjects = ProjectUtils.getOpenProjects();
        return Arrays.stream(openProjects)
                .flatMap(project -> FeignClassScanUtils.scanFeignInterfaces(project).stream())
                .collect(Collectors.toList());
    }


    /**
     * 返回的controller
     * @return
     */
    public static List<HttpMappingInfo> scanAllProjectControllerInfo() {
        Project[] openProjects = ProjectUtils.getOpenProjects();
        List<HttpMappingInfo> collect = Arrays.stream(openProjects)
                .flatMap(project -> ControllerClassScanUtils.scanControllerPaths(project).stream())
                .collect(Collectors.toList());
        httpMappingInfos = collect;
        return collect;
    }



    public static List<HttpMappingInfo>  getControllerInfos(){
        return httpMappingInfos;
    }

    public static String showResult(List<HttpMappingInfo> httpMappingInfos) {
        StringBuilder message = new StringBuilder();
        // 表头信息
        int i = 0;
        message.append(String.format("%-3s", "Num")).append("\t");
        message.append(String.format("%-7s", "Request")).append("\t");
        message.append(String.format("%-52s", "Path")).append("\t");
        message.append(String.format("%-25s", "Swagger Info")).append("\t");
        message.append(String.format("%-25s", "Swagger Notes")).append("\n");
        for (HttpMappingInfo info : httpMappingInfos) {
            message.append(String.format("%-3d", ++i)).append("\t");
            message.append(String.format("%-7s", info.getRequestMethod())).append("\t");
            // 接口路径
            message.append(String.format("%-52s", info.getPath())).append("\t");
            // Swagger Info
            message.append(String.format("%-25s", info.getSwaggerInfo())).append("\t");
            // Swagger Notes
            message.append(String.format("%-25s", info.getSwaggerNotes())).append("\n");
        }
        return message.toString();
    }
}
