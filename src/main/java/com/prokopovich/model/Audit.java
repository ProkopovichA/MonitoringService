/**
 * Запись аудита фиксирует действия пользователя
 */

package com.prokopovich.model;

import lombok.Getter;
import lombok.AllArgsConstructor;

import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@ToString
public class Audit {
    @Getter
    private User user;
    @Getter
    private LocalDate date;
    @Getter
    private AuditAction action;



}
