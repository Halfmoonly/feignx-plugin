package com.lyflexi.feignx.properties;


import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;


import java.util.Map;
import java.util.Properties;


/**
 * @Description:
 * @Author: lyflexi
 * @project: feignx-plugin
 * @Date: 2024/11/3 15:01 
 */

public class ConfigReader {

    private static final String PROPERTIES_FILE_NAME = "application.properties";
    private static final String YML_FILE_NAME = "application.yml";
    private static final String YAML_FILE_NAME = "application.yaml";

    public static Properties readProperties(PsiDirectory moduleDirectory) {
        return readPropertiesFromFile(moduleDirectory, PROPERTIES_FILE_NAME);
    }

    public static Map<String, Object> readYmlOrYaml(PsiDirectory moduleDirectory) {
        Map<String, Object> yamlData = readYmlFromFile(moduleDirectory, YML_FILE_NAME);
        if (yamlData == null) {
            yamlData = readYmlFromFile(moduleDirectory, YAML_FILE_NAME);
        }
        return yamlData;
    }

    private static Properties readPropertiesFromFile(PsiDirectory moduleDirectory, String fileName) {
        Properties properties = new Properties();
        VirtualFile[] files = findFilesByName(moduleDirectory, fileName);
        for (VirtualFile file : files) {
            try (InputStream inputStream = file.getInputStream()) {
                properties.load(inputStream);
                break; // 只加载第一个找到的文件
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    private static Map<String, Object> readYmlFromFile(PsiDirectory moduleDirectory, String fileName) {
        Yaml yaml = new Yaml();
        VirtualFile[] files = findFilesByName(moduleDirectory, fileName);
        for (VirtualFile file : files) {
            try (InputStream inputStream = file.getInputStream()) {
                return yaml.load(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static VirtualFile[] findFilesByName(PsiDirectory directory, String fileName) {
        if (directory == null || directory.getVirtualFile() == null) {
            return new VirtualFile[0];
        }

        return findFilesByNameRecursively(directory.getVirtualFile(), fileName);
    }

    private static VirtualFile[] findFilesByNameRecursively(VirtualFile directory, String fileName) {
        if (!directory.isDirectory()) {
            return new VirtualFile[0];
        }

        VirtualFile[] foundFiles = new VirtualFile[0];

        for (VirtualFile child : directory.getChildren()) {
            if (child.isDirectory()) {
                foundFiles = concatenate(foundFiles, findFilesByNameRecursively(child, fileName));
            } else if (fileName.equals(child.getName())) {
                foundFiles = concatenate(foundFiles, new VirtualFile[]{child});
            }
        }

        return foundFiles;
    }

    private static VirtualFile[] concatenate(VirtualFile[] array1, VirtualFile[] array2) {
        VirtualFile[] result = new VirtualFile[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

}
