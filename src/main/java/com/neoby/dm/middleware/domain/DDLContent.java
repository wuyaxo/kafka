package com.neoby.dm.middleware.domain;

import com.jayway.jsonpath.JsonPath;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;

public class DDLContent<K, V> {

    private final K key;
    private final V value;


    public DDLContent(ConsumerRecord consumerRecord) {
        LinkedHashMap data = JsonPath.read(consumerRecord.value().toString(), "$");
        if (CollectionUtils.isEmpty(data)) {
            this.key = null;
            this.value = null;
        } else {
            String databaseName = new StringBuilder("`").append(data.get("database").toString()).append("`.").toString();
            String sql = StringUtils.replace(data.get("sql").toString(), databaseName, "");
            this.key = (K) consumerRecord.key().toString();
            this.value = (V) sql;
        }


    }

    public K key() {
        return this.key;
    }

    public V value() {
        return this.value;
    }
}
