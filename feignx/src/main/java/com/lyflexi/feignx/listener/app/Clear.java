package com.lyflexi.feignx.listener.app;
import com.intellij.openapi.project.Project;
import com.lyflexi.feignx.cache.BilateralCacheManager;
import com.lyflexi.feignx.cache.InitialPsiClassCacheManager;

/**
 * 当前项目关闭，缓存清理接口
 */
public interface Clear {
    static void clear(Project project){
        //清理双边接口方法缓存
        BilateralCacheManager.clear(project);
        //清理psiclass缓存
        InitialPsiClassCacheManager.getInstance().clear(project);
    }
}
