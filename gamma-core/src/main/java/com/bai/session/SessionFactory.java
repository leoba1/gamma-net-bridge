package com.bai.session;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/23 21:02
 */
public abstract class SessionFactory {
    final static Session session = new SessionMemory();

    public static Session getSession() {
        return session;
    }
}
