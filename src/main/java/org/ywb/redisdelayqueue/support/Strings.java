package org.ywb.redisdelayqueue.support;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author yuwenbo1
 * @date 2021/1/19 20:20
 * @since 1.0.0
 */
public class Strings {

    public static boolean isNullOrEmpty(String target) {
        return target == null || target.isEmpty();
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
