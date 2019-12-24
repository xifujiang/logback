package com.dabay.logback.service;

import java.io.IOException;

public interface PackageService {
    /** 
    * @Description: 打包项目 
    * @Param: [gitlabPath] 
    * @return: void 
    * @Author: shenyini
    * @Date: 2019/12/16 
    */
    String packageProject(String gitlabPath);

    String packageProject(String loginName, String gitlabPath, String branchName) throws Exception;

    String getDownloadUrl(String projectName) throws IOException;
}
