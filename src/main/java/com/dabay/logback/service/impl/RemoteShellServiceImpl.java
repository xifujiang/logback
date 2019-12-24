package com.dabay.logback.service.impl;

import com.dabay.logback.service.RemoteShellService;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * @program: logback
 * @description:
 * @author: shenyini
 * @create: 2019-12-17 10:07
 **/
@Service
public class RemoteShellServiceImpl implements RemoteShellService {

    @Override
    public void executeRemoteShell(String gitlabPath) throws IOException {
//        String shellPath = Constants.SHELL_PATH;
//        String downloadPath = Constants.DOWNLOAD_PATH;
//        String[] fields = gitlabPath.split("/");
//        String project = fields[fields.length-1].split("\\.")[0];
//        String peoject_path = Constants.PROJECT_PATH + "/" + project;
//        String cmd = shellPath +" " + downloadPath + " " + gitlabPath + " "+ peoject_path;
//        System.out.println(cmd);
//        RemoteShellUtils remoteShellUtils = new RemoteShellUtils();
//        remoteShellUtils.executeLinuxCmd(cmd);
    }
}
