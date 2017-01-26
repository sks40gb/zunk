package com.fossa.servlet.server;

import com.fossa.servlet.session.UserTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities used by Handler_sql_query and Handler_sql_update.
 */
public class SqlUtil {

    private SqlUtil() {
    }

    /**
     * Replace substrings in sql statement.
     */
    public static String substitute(UserTask task, String sql) {

        Pattern p = Pattern.compile(
                "\\[(user|task|volume|batch)\\]");
        Matcher m = p.matcher(sql);
        if (!m.find()) {
            return sql;
        }
        StringBuffer buffer = new StringBuffer(sql.length());
        do {
            String repl = m.group(0);
            if (repl.equals("[user]")) {
                repl = Integer.toString(task.getUsersId());
            } else if (repl.equals("[task]")) {
                repl = Integer.toString(task.getSessionId());
            } else if (repl.equals("[volume]")) {
                repl = Integer.toString(task.getVolumeId());
            } else if (repl.equals("[batch]")) {
                repl = Integer.toString(task.getBatchId());
            }
            m.appendReplacement(buffer, repl);
        } while (m.find());
        m.appendTail(buffer);
        return buffer.toString();
    }
}
