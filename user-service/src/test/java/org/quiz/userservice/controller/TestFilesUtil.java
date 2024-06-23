package org.quiz.userservice.controller;

import org.aspectj.util.FileUtil;
import org.springframework.util.ResourceUtils;

import java.io.File;

public abstract class TestFilesUtil {

    public static String readResourceFile(String path) {
        try {
            File file = ResourceUtils.getFile("classpath:%s".formatted(path));
            return FileUtil.readAsString(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
