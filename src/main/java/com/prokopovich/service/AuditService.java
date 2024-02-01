/**
 * Класс "список"
 * хранит в себе коллекцию обектов "аудит"
 * реализован как синглтон
 */

package com.prokopovich.service;

import com.prokopovich.model.Audit;

import java.util.ArrayList;


public class AuditService {
    private static AuditService instance;
    private ArrayList<Audit> auditList;

    private AuditService() {
        auditList = new ArrayList<>();
    }

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    /**
     * вывести все записи по аудиту
     * @return лист объектов аудит
     */
    public ArrayList<Audit> getAuditList() {
        return auditList;
    }

    /**
     * добавляет запись в лист аудита
     * @param audit - объект типа аудит
     */
    public void addAudit(Audit audit) {
        auditList.add(audit);
    }
}

