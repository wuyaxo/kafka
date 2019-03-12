package com.neoby.dm.middleware.domain;

import com.jayway.jsonpath.JsonPath;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.util.ObjectUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DataContent<K, V> {

    private final K key;
    private final V value;


    public DataContent(ConsumerRecord consumerRecord) {

        String orgin_value =  consumerRecord.value().toString();
        StringBuilder new_valus;
        LinkedHashMap data = JsonPath.read(consumerRecord.key().toString(), "$");
        if (data.keySet().contains("database"))
                data.remove("database");
        if (data.keySet().contains("table"))
            data.remove("table");

        Set<Map.Entry<String, Object>> entrySet = data.entrySet();
        if (!entrySet.isEmpty()){
            new_valus = new StringBuilder(orgin_value.substring(0,orgin_value.length() - 1));
            new_valus.append(",\"primaryKey\":{");
        } else {
            new_valus = new StringBuilder(orgin_value);
        }

        Iterator iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = (Map.Entry<String, Object>) iterator.next();
            if (iterator.hasNext()){
                new_valus.append("\"").append(next.getKey().replace("pk.", "")).append("\":").append(convertSql(next.getValue())).append(",");
            } else {
                new_valus.append("\"").append(next.getKey().replace("pk.", "")).append("\":").append(convertSql(next.getValue())).append("}}");
            }
        }

        this.key = (K) Integer.toString(consumerRecord.value().hashCode());
        this.value = (V) new_valus;

    }

    public K key() {
        return this.key;
    }

    public V value() {
        return this.value;
    }

    public String toString() {
        return this.key.toString();
    }

    private String convertSql(Object object) {
        if (ObjectUtils.isEmpty(object)){
            return "null";
        }
        return (object instanceof String) ? "\"" + object + "\"" : object.toString();
    }
}
