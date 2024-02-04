package com.prokopovich;


import com.prokopovich.in.MainController;
import com.prokopovich.repo.AuditRepo;

public class Main {

    public static void main(String[] args) {
       //MainController.start();
        AuditRepo.getInstance().getAuditList().stream()
                .forEach(auditLog -> System.out.println(auditLog));;
    }

}