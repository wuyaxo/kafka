package com.neoby.dm.middleware.threads;

import com.neoby.dm.middleware.service.RepositoryService;

public class BusinessThread implements Runnable {

    private String document;

    private final RepositoryService repositoryService;

    public BusinessThread(String document, RepositoryService repositoryService) {
        this.document = document;
        this.repositoryService = repositoryService;
    }

    public String getDocument() {

        return this.document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    @Override
    public void run() {
        repositoryService.repository(document);
    }
}
