package com.dabay.logback.service;

import ch.ethz.ssh2.StreamGobbler;
import com.dabay.logback.constants.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: logback
 * @description:
 * @author: shenyini
 * @create: 2019-12-17 10:06
 **/
public interface RemoteShellService {
    void executeRemoteShell(String gitlabPath) throws IOException;
}
