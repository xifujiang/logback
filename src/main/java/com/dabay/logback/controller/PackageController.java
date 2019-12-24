package com.dabay.logback.controller;

import com.dabay.logback.service.PackageService;
import com.dabay.logback.service.RemoteShellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @program: logback
 * @description: maven命令打包项目
 * @author: shenyini
 * @create: 2019-12-16 16:38
 **/
@RestController
public class PackageController {

    @Autowired
    PackageService packageService;

    @Autowired
    RemoteShellService remoteShellService;

    /** 
    * @Description: 打包命令 
    * @Param: [gitlabPath] 
    * @return: java.lang.String 
    * @Author: shenyini
    * @Date: 2019/12/18 
    */ 
    @RequestMapping("/project/package")
    public String packageProject(@RequestParam(value="loginName") String loginName,
        @RequestParam(value="gitlabPath") String gitlabPath, @RequestParam(value = "branchName") String branchName) throws Exception {
        String returnMessage = packageService.packageProject(loginName, gitlabPath, branchName);
//        remoteShellService.executeRemoteShell(gitlabPath);
        return returnMessage;
    }


    /**
    * @Description: 获取下载地址
    * @Param: []
    * @return: java.lang.String
    * @Author: shenyini
    * @Date: 2019/12/20
    */
    @RequestMapping("project/downloadUrl")
    public String downloadUrl(String projectName) throws IOException {
        String downloadURL = packageService.getDownloadUrl(projectName);
        return downloadURL;
    }

}
