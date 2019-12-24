package com.dabay.logback.controller;

import com.dabay.logback.constants.Constants;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: logback
 * @description:
 * @author: shenyini
 * @create: 2019-12-23 10:31
 **/
@RestController
public class TestController {
    public void myTest(String gitlabPath) {
        String shellPath = Constants.SHELL_PATH;
        String pullPath = Constants.PULL_PATH;
        String[] fields = gitlabPath.split("/");
        String projectName = fields[fields.length-1].split("\\.")[0];
        String redirectPath =Constants.BIND+ " " + Constants.REDIRECT_PATH + "/" +projectName+ ".log" ;



    }
}
