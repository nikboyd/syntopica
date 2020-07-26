package com.educery.utils;

import org.slf4j.*;
import org.apache.commons.lang3.StringUtils;
import static com.educery.utils.Utils.*;

/**
 * Grafts logging behavior onto an implementing class.
 * @author Nik Boyd <nik.boyd@educery.dev>
 */
public interface Logging {

    public static Logger logger(Class<?> aClass) { return LoggerFactory.getLogger(aClass); }
    default Logger logger() { return logger(getClass()); }
    default String format(String report, Object ... values) { return String.format(report, values); }

    default void whisper(String message) { logger().debug(message); }
    default void whisper(Throwable ex) {
        if (hasNo(ex)) return;
        if (hasNo(ex.getMessage())) ex.printStackTrace();
        else logger().debug(ex.getMessage(), ex); }

    default void report(String message) { logger().info(message); }
    default void report(Throwable ex) {
        if (hasNo(ex)) return;
        if (hasNo(ex.getMessage())) ex.printStackTrace();
        else logger().info(ex.getMessage(), ex); }

    default void warn(String message) { logger().warn(message); }
    default void warn(Throwable ex) {
        if (hasNo(ex)) return;
        if (hasNo(ex.getMessage())) ex.printStackTrace();
        else logger().warn(ex.getMessage(), ex); }

    default void error(String message) { logger().error(message); }
    default void error(String message, Throwable ex) { error(message); error(ex); }
    default void error(Throwable ex) {
        if (hasNo(ex)) return;
        if (hasNo(ex.getMessage())) ex.printStackTrace();
        else logger().error(ex.getMessage(), ex); }

    static int countMatches(CharSequence seq, CharSequence chars) { return StringUtils.countMatches(seq, chars); }
    static String strip(String value, String chars) { return StringUtils.strip(value, chars); }

    static boolean notEmpty(CharSequence seq) { return !isEmpty(seq); }
    static boolean isEmpty(CharSequence seq) { return StringUtils.isEmpty(seq); }

    static final String Empty = "";
    static final String Blank = " ";
    static final String Colon = ":";
    static final String Comma = ",";
    static final String Period = ".";

} // Logging
