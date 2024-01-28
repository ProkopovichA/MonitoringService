/**
 * enum содержит список возможных прав доступа
 */
package com.prokopovich.model;

public enum UserRole {
    ADMIN,
    USER;

    /**
     * метод проверки является ли пользователь админом
     * @return true - в случае успешной проверки
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
}
