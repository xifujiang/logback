package com.dabay.logback.service;

import com.dabay.logback.server.LogWebSocket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class WebSocketService2 {
    @Resource
    LogWebSocket logWebSocket;
    public void sendMessage(String sessionid) {
        // 通过Process和Runtime执行linux命令

//        String cmd = "tail -f /var/test.log";
        String cmd  = "tail -f /myPackageTest/test.log";
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            //需要另外启动线程进行读取，防止输入流阻塞当前线程
            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(process.getInputStream(), "UTF-8"));
                        StringBuffer line = new StringBuffer();
                        String lineOne = null;
                        int count = 0;
                        int lineNum = 1;
                        while ((lineOne = br.readLine()) != null) {
                            if (count == 1000) {
                                logWebSocket.sendMessageTo(sessionid,lineOne);
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
                        logWebSocket.sendMessageTo(sessionid,lineOne);
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            inputThread.start();

            //主线程读取错误输出流数据
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
            StringBuffer line = new StringBuffer();
            String lineOne = null;
            int count = 0;
            int lineNum = 1;
            while ((lineOne = br.readLine()) != null) {
                if (count == 100) {
                    logWebSocket.sendMessageTo(sessionid,lineOne);
                    line = new StringBuffer();
                    count = 0;
                } else {
                    line.append(lineOne + "</br>");
                    count++;
                    lineNum++;
                }
            }

            logWebSocket.sendMessageTo(sessionid,lineOne);
            //等待正常输出流线程读取完成后，统一销毁进程
            inputThread.join();
            // 返回码 0 表示正常退出 1表示异常退出
            int extValue = process.waitFor();
            if (0 == extValue) {
                System.out.println("Exit Success!");
//                logger.info("Exit Success!");
                br.close();
                process.destroy();
            } else {
                System.out.println("Exit failure！");
//                logger.info("Exit failure！");
                br.close();
                process.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("execute shell fail！"+ e);
            System.out.println("Exit failure！");
//            logger.error("execute shell fail！", e);
//            logger.info("Exit failure！");
        }
    }
}