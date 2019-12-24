package com.dabay.logback.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.dabay.logback.constants.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: package_program
 * @description:
 * @author: shenyini
 * @create: 2019-12-13 14:08
 **/
public class RemoteShellUtils {
    public void executeLinuxCmd(String cmd) throws IOException {
        List<String> result=new ArrayList<String>();
        Connection connection = new Connection(Constants.VM_IP);
        connection.connect();//连接
        connection.authenticateWithPassword(Constants.USERNAME,Constants.PASSWORD);//认证
        Session session=connection.openSession();
        session.execCommand(cmd);
        InputStream in = new StreamGobbler(session.getStdout());//获得标准输出流
        StringBuilder  out = processStdout(in, StandardCharsets.UTF_8);
        byte[] b = new byte[8192];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }

        System.out.println(out.toString());
        if (session != null) {
            session.close();
        }
        session.close();
        in.close();
    }

    /**
     * 解析流获取字符串信息
     *
     * @param in      输入流对象
     * @param charset 字符集
     * @return 脚本输出结果
     */
    public StringBuilder processStdout(InputStream in, Charset charset) throws FileNotFoundException {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
//        OutputStream os = new FileOutputStream("./data.txt");
        try {
            int length;
            while ((length = in.read(buf)) != -1) {
//                os.write(buf, 0, c);
                sb.append(new String(buf, 0, length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }
    public void getFile(String cmd) throws IOException {
        List<String> result=new ArrayList<String>();
        Connection connection = new Connection(Constants.VM_IP);
        connection.connect();//连接
        connection.authenticateWithPassword(Constants.USERNAME,Constants.PASSWORD);//认证
        Session session=connection.openSession();
        session.execCommand(cmd);
        InputStream in = new StreamGobbler(session.getStdout());//获得标准输出流
        StringBuilder  out = processStdout(in, StandardCharsets.UTF_8);
        byte[] b = new byte[8192];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }

        System.out.println(out.toString());
        if (session != null) {
            session.close();
        }
        session.close();
        in.close();
    }
    public static void main(String[] args) throws Exception{
        String shell = "/myPackageTest/package.sh";
        String dir = "~";
        String git = "git@gitlab.dabay.cn:Automatic_System/System.git";
        String project = "~/System";
        String redirect = ">>test.log 2>&1";
//        String cmd = new String(shell +" " + dir + " " + git + " "+ project + " "+ redirect);
        String cmd = "/target/test.sh";
        System.out.println(cmd);
        RemoteShellUtils remoteShellUtils = new RemoteShellUtils();
        remoteShellUtils.executeLinuxCmd(cmd);
//        remoteShellUtils.getFile("cat /myPackageTest/test.log");
    }
}
