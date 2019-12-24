package com.dabay.logback.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @program: package_program
 * @description:
 * @author: shenyini
 * @create: 2019-12-13 10:12
 **/
public class PackageUtils {
    public static String executeLinuxCmd(String[] cmd) throws Exception {
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            InputStream in = process.getInputStream();
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            in.close();
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "脚本执行失败";
    }

//    public static void main(String[] args) throws Exception {
//        String shell = "/myPackageTest/package.sh";
//        String dir = "~";
//        String git = "git@gitlab.dabay.cn:Automatic_System/System.git";
//        String project = "~/System";
//        String redirect = ">>test.log 2>&1";
//        PackageUtils packageUtils = new PackageUtils();
//        String[] cmd = new String[]{"sh", "-c" , shell +" " + dir + " " + git + " "+ project + " "+ redirect};
//        System.out.println(packageUtils.executeLinuxCmd(cmd));
//        //输出到web上。
//    }
}
