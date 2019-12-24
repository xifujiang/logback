package com.dabay.logback.service.impl;

import com.dabay.logback.constants.Constants;
import com.dabay.logback.server.LogWebSocket;
import com.dabay.logback.service.PackageService;
import com.dabay.logback.utils.PackageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @program: logback
 * @description:
 * @author: shenyini
 * @create: 2019-12-16 17:03
 **/
@Slf4j
@Service
public class PackageServiceImpl implements PackageService {

    @Resource
    LogWebSocket logWebSocket;

    @Override
    public String packageProject(String gitlabPath) {
        String shellPath = Constants.SHELL_PATH;
        String pullPath = Constants.PULL_PATH;
        String[] fields = gitlabPath.split("/");
        String projectName = fields[fields.length-1].split("\\.")[0];
        String projectPath = Constants.PROJECT_PATH + "/" + projectName;
//        System.out.println(projectName);
        String redirectPath =Constants.BIND+ " " + Constants.REDIRECT_PATH + "/" +projectName+ ".log" ;
        String[] cmd = new String[]{"sh", "-c" , shellPath +" " + pullPath + " " + gitlabPath + " "+ projectPath + " "+ redirectPath};
        System.out.println(cmd.toString());
        try {
            PackageUtils.executeLinuxCmd(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cmd[2];
    }

    @Override
    public String packageProject(String loginName, String gitlabPath, String branchName) throws Exception {
        String shellPath = Constants.SHELL_PATH;
        String pullPath = Constants.PULL_PATH;
        String[] fields = gitlabPath.split("/");
        String projectName = fields[fields.length-1].split("\\.")[0];
        String redirectPath =Constants.BIND+ " " + Constants.REDIRECT_PATH + "/" +projectName+ ".log" ;


        String shell = shellPath + " " + pullPath + " " + gitlabPath + " " + projectName + " " + branchName + " " + redirectPath;
        String[] cmd = new String[]{"sh", "-c" , shell};
        System.out.println(cmd.toString());
        PackageUtils.executeLinuxCmd(cmd);

        /*获取日志命令*/
        String logCmd  = "tail -f +0 " + redirectPath;
        Process process = Runtime.getRuntime().exec(logCmd);;
        Thread logThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "UTF-8"));
                    StringBuffer line = new StringBuffer();
                    String lineOne = null;
                    int count = 0;
                    int lineNum = 1;
                    /*如果没有到最后*/
                    while (!"end".equals(lineOne = br.readLine())) {
                        if (count == 1000) {
                            logWebSocket.sendMessageTo(loginName, line.toString());
                            line = new StringBuffer();
                            count = 0;
                            //控制线程执行速度 防止推送过快 导致浏览器卡屏
                            Thread.sleep(1000);
                        } else {
                            line.append(lineOne + "</br>");
                            count++;
                            lineNum++;
                        }
                    }
                    logWebSocket.sendMessageTo(loginName, line.toString());
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        logThread.start();

//        getLog(redirectPath, loginName);
        return projectName + "执行完成" + "    114.215.135.160:7788/package/"+projectName+".jar";
    }

    @Override
    public String getDownloadUrl(String projectName) throws IOException {
        String shell  = "/target/syntest/judgeType.sh " + projectName;
        String[] cmd = new String[]{"sh", "-c" , shell};
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader br = new BufferedReader(
            new InputStreamReader(process.getInputStream(), "UTF-8"));
        String lineOne = br.readLine();
        System.out.println(lineOne);
        if(lineOne == null) {
            System.out.println("没有该包");
            return "抱歉，没有该包，不能下载";
        }
        return "114.215.135.160:7788/package/" + lineOne;
    }

    public void getLog(String redirectPath, String loginName) throws IOException {
        String logCmd  = "tail -f " + redirectPath;

        Process process = Runtime.getRuntime().exec(logCmd);;
        Thread logThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "UTF-8"));
                    StringBuffer line = new StringBuffer();
                    String lineOne = null;
                    int count = 0;
                    int lineNum = 1;
                    while (!"end".equals(lineOne = br.readLine())) {
                        logWebSocket.sendMessageTo(loginName, lineOne);
                        if (count == 1000) {
                            logWebSocket.sendMessageTo(loginName, line.toString());
                            line = new StringBuffer();
                            count = 0;
                            //控制线程执行速度 防止推送过快 导致浏览器卡屏
                            Thread.sleep(1000);
                        } else {
                            line.append(lineOne + "</br>");
                            count++;
                            lineNum++;
                        }
                    }
                    logWebSocket.sendMessageTo(loginName, line.toString());
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        logThread.start();

//        //主线程读取错误输出流数据
//        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
//        StringBuffer line = new StringBuffer();
//        String lineOne = null;
//        int count = 0;
//        int lineNum = 1;
//        while ((lineOne = br.readLine()) != null) {
//            if (count == 100) {
//                logWebSocket.sendMessageTo(loginName,lineOne);
//                line = new StringBuffer();
//                count = 0;
//            } else {
//                line.append(lineOne + "</br>");
//                count++;
//                lineNum++;
//            }
//        }
//        try{
//            logWebSocket.sendMessageTo(loginName,lineOne);
//            //等待正常输出流线程读取完成后，统一销毁进程
//            logThread.join();
//            // 返回码 0 表示正常退出 1表示异常退出
//            int extValue = process.waitFor();
//            if (0 == extValue) {
//                log.info("Exit Success!");
//                br.close();
//                process.destroy();
//            } else {
//                log.info("Exit failure！");
//                br.close();
//                process.destroy();
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            System.out.println("execute shell fail！" + e);
//            System.out.println("Exit failure！");
//            log.error("execute shell fail！", e);
//            log.info("Exit failure！");
//        }

    }
}
