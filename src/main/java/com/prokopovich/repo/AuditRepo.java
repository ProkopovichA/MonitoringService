

package com.prokopovich.repo;

import com.prokopovich.model.Audit;

import java.util.ArrayList;

public interface AuditRepo {
    boolean addAudit(Audit audit);
    ArrayList<Audit> getAuditList();

}

