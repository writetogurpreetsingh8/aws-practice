package com.easy.task.db.config;

import com.easy.task.enums.DBType;

public class DbContextHolder {

	private static final ThreadLocal<DBType> contextHolder = ThreadLocal.withInitial(() -> DBType.WRITE);

    public static void set(DBType dbType) {
        contextHolder.set(dbType);
    }

    public static DBType get() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}
