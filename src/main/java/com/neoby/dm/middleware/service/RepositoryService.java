package com.neoby.dm.middleware.service;

public interface RepositoryService {

    void repository(String document);

    void executeDDL(String ddlSql);
}
