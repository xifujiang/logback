package com.dabay.logback.service.impl;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.dabay.logback.constants.Constants;
import com.dabay.logback.server.LogWebSocket;
import com.dabay.logback.service.WebSocketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @program: logback
 * @description:
 * @author: shenyini
 * @create: 2019-12-16 10:14
 **/
@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Resource
    LogWebSocket logWebSocket;

    @Override
    public void sendLog(String loginName) throws IOException {
        // 通过Process和Runtime执行linux命令

//        String cmd = "tail -f /var/test.log";
        String cmd  = "tail -f /myPackageTest/test.log";
        System.out.println(cmd);

        Connection connection = new Connection(Constants.VM_IP);
        connection.connect();//连接
        connection.authenticateWithPassword(Constants.USERNAME,Constants.PASSWORD);//认证
        Session session=connection.openSession();
        try {
        //    Process process = Runtime.getRuntime().exec(cmd);
            session.execCommand(cmd);

            //需要另外启动线程进行读取，防止输入流阻塞当前线程
            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout(), "UTF-8"));
                        StringBuffer line = new StringBuffer();
                        String lineOne = null;
                        int count = 0;
                        int lineNum = 1;
                        while (!"end".equals(lineOne = br.readLine())) {
//                            logWebSocket.sendMessageTo(loginName,lineOne);
                            if (count == 1000) {
                                logWebSocket.sendMessageTo(loginName,line.toString());
                                line = new StringBuffer();
                                count = 0;
                                //控制线程执行速度 防止推送过快 导致浏览器卡屏
                                Thread.sleep(1000);
                            } else {
                                line.append(lineOne + "</br>");
                                count++;
                                lineNum++;
                            }
//                            System.err.println(lineOne);
                        }
                        br.close();
                        logWebSocket.sendMessageTo(loginName,line.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            inputThread.start();

            //主线程读取错误输出流数据
            BufferedReader br = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStderr()), "UTF-8"));
            StringBuffer line = new StringBuffer();
            String lineOne = null;
            int count = 0;
            int lineNum = 1;
            while ((lineOne = br.readLine()) != null) {
                if (count == 100) {
                    logWebSocket.sendMessageTo(loginName,line.toString());
                    System.out.println(line.toString());
                    line = new StringBuffer();
                    count = 0;
                } else {
                    line.append(lineOne + "</br>");
                    count++;
                    lineNum++;
                }
            }
            logWebSocket.sendMessageTo(loginName,line.toString());
            System.out.println(line.toString());
            //等待正常输出流线程读取完成后，统一销毁进程
            inputThread.join();
            // 返回码 0 表示正常退出 1表示异常退出

//            int extValue = process.waitFor();
            int extValue = session.getExitStatus();
            if (0 == extValue) {
                System.out.println("Exit Success!");
//                logger.info("Exit Success!");
                br.close();
//                process.destroy();
                session.close();
            } else {
                System.out.println("Exit failure！");
//                logger.info("Exit failure！");
                br.close();
                session.close();
//                process.destroy();
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