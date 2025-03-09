package com.lyflexi.feignx.utils;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;

/**
 * @Author: hmly
 * @Date: 2025/3/9 18:57
 * @Project: feignx-plugin
 * @Version: 1.0.0
 * @Description:
 */
public class NotificationCompatUtil {

    private static final String NOTIFICATION_GROUP_ID = "Copy URL Notifications";

    private static NotificationGroup legacyGroup;

    public static void notify(Project project, String content) {
        //新IDEA版本
        if (isNewNotificationApiAvailable()) {
            NotificationGroup group = NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID);
            if (group != null) {
                group.createNotification(content, NotificationType.INFORMATION).notify(project);
            }
        }
        //旧IDEA版本兼容
        else {
            if (legacyGroup == null) {
                legacyGroup = new NotificationGroup(NOTIFICATION_GROUP_ID, NotificationDisplayType.BALLOON, true);
            }
            legacyGroup.createNotification(content, NotificationType.INFORMATION).notify(project);
        }
    }

    // 判断是否为 2020.1 及以上版本（build baseline >= 201）
    //NotificationGroupManager 类未找到	这个类是 2020.1 (IDEA 201) 才引入的新 API，2019.3 不支持
    private static boolean isNewNotificationApiAvailable() {
        return ApplicationInfo.getInstance().getBuild().getBaselineVersion() >= 201;
    }
}
