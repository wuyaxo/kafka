package com.neoby.dm.middleware.service.impl;

import com.jayway.jsonpath.JsonPath;
import com.neoby.dm.middleware.dao.MysqlRepository;
import com.neoby.dm.middleware.service.RepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service("RepositoryService")
public class RepositoryServiceImpl implements RepositoryService {

    private final MysqlRepository mysqlRepository;

    @Autowired
    public RepositoryServiceImpl(MysqlRepository mysqlRepository) {
        this.mysqlRepository = mysqlRepository;
    }

    @Override
    public void repository(String document) {

        String type = JsonPath.read(document, "$.type");
        String tableName = JsonPath.read(document, "$.table");
        LinkedHashMap data = JsonPath.read(document, "$.data");

        if (type.equals("insert")) {
            log.info("insert {}", tableName);
            insertMysql(tableName, data);
        } else if (type.equals("update")) {
            log.info("update {}", tableName);
            LinkedHashMap oldData = JsonPath.read(document, "$.old");
            LinkedHashMap primaryKeys = JsonPath.read(document, "$.primaryKey");
            updateMysql(tableName, data, oldData, primaryKeys);
        }
    }


    private void insertMysql(String tableName, LinkedHashMap data) {
        StringBuilder sql_head = new StringBuilder("insert INTO " + tableName + " (");
        StringBuilder sql_data = new StringBuilder(") VALUES (");
        for (Map.Entry entry : (Iterable<Map.Entry>) data.entrySet()) {
            sql_head.append("`").append(entry.getKey()).append("`,");
            sql_data.append(convertSql(entry.getValue())).append(",");
        }

        if (!StringUtils.isEmpty(sql_head.toString())) {
            sql_head = new StringBuilder(sql_head.substring(0, sql_head.length() - 1));
        }
        if (!StringUtils.isEmpty(sql_data.toString())) {
            sql_data = new StringBuilder((sql_data.substring(0, sql_data.length() - 1)) + ");");
        }

        mysqlRepository.execute(sql_head + sql_data.toString());
    }

    private String convertSql(Object object) {
        if (ObjectUtils.isEmpty(object)) {
            return "null";
        }
        return (object instanceof String) ? "'" + object + "'" : object.toString();
    }

    private void updateMysql(String tableName, LinkedHashMap data, LinkedHashMap oldData, LinkedHashMap primaryKeys) {
        StringBuilder sql_head = new StringBuilder("update " + tableName + " set ");
        StringBuilder sql_condition = new StringBuilder();

        //sql_head
        Iterator<Map.Entry> iterator1 = oldData.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry entry = iterator1.next();
            if (iterator1.hasNext()) {
                sql_head.append(String.format("`%s`= %s,", entry.getKey(), convertSql(data.get(entry.getKey()))));
            } else {
                sql_head.append(String.format("`%s`= %s ", entry.getKey(), convertSql(data.get(entry.getKey()))));
            }
        }

        //sql_condition
        if (primaryKeys.isEmpty()) {
            sql_condition = generateSqlNormal(data, oldData);
        } else {
            sql_condition = generateSqlIndex(primaryKeys);
        }

        mysqlRepository.execute(sql_head + sql_condition.toString());
    }


    private StringBuilder generateSqlNormal(LinkedHashMap data, LinkedHashMap oldData) {
        Set<String> keySets = oldData.keySet();
        StringBuilder sql_condition = new StringBuilder("where ");
        Iterator<Map.Entry> iterator2 = data.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry entry = iterator2.next();
            if (entry.getKey() != null && keySets.contains(entry.getKey())) {
                if (!iterator2.hasNext()) {
                    sql_condition = new StringBuilder(sql_condition.substring(0, sql_condition.length() - 4) + ";");
                }
                continue;
            }
            if (iterator2.hasNext()) {
                sql_condition.append(String.format("`%s`= %s and ", entry.getKey(), convertSql(entry.getValue())));
            } else {
                sql_condition.append(String.format("`%s`= %s;", entry.getKey(), convertSql(entry.getValue())));
            }

        }
        return sql_condition;
    }

    private StringBuilder generateSqlIndex(LinkedHashMap primaryKeys) {
        StringBuilder sql_condition = new StringBuilder("where ");
        Iterator<Map.Entry> iterator = primaryKeys.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            if (iterator.hasNext()) {
                sql_condition.append(String.format("`%s`= %s and ", entry.getKey(), convertSql(entry.getValue())));
            } else {
                sql_condition.append(String.format("`%s`= %s;", entry.getKey(), convertSql(entry.getValue())));
            }
        }
        return sql_condition;
    }


    @Override
    public void executeDDL(String ddlSql) {
        log.info("123");
        if (ddlSql.toUpperCase().indexOf("DROP") >= 0) {
            log.error("企图执行删表语句:{}", ddlSql);
        } else {
            log.info("执行DDL语句:{}", ddlSql);
            mysqlRepository.execute(ddlSql);
        }

    }
}
