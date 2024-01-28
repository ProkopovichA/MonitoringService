/**
 * Запись аудита фиксирует действия пользователя
 */

package com.prokopovich.model;

import lombok.Getter;
import lombok.AllArgsConstructor;

import com.prokopovich.model.User;
import java.time.LocalDate;

@AllArgsConstructor
public class Audit {
    @Getter
    private User user;
    @Getter
    private LocalDate date;
    @Getter
    private String action;



}
