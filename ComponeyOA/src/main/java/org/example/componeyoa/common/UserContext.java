package org.example.componeyoa.common;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_NAME_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void setUserName(String userName) {
        USER_NAME_HOLDER.set(userName);
    }

    public static String getUserName() {
        return USER_NAME_HOLDER.get();
    }

    // 关键防线：请求结束时必须清空，防止 Tomcat 线程池线程复用导致内存泄漏与身份串号
    public static void clear() {
        USER_ID_HOLDER.remove();
        USER_NAME_HOLDER.remove();
    }
}
