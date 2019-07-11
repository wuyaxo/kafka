package com.neoby.dm.middleware.kafkaConsumer;


import com.neoby.dm.middleware.domain.DDLContent;
import com.neoby.dm.middleware.domain.DataContent;
import com.neoby.dm.middleware.service.RepositoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class Consumer {

    private final RepositoryService repositoryService;

    @Autowired
    public Consumer(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * KafkaListener 监听器
     *
     * @param cr -kafka的消息
     */
    @KafkaListener(id = "DML", topics = "${kafka.topics}", groupId = "DML")
    public void listenDML(ConsumerRecord<?, ?> cr) {
        if (cr.value() != null) {
            DataContent dataContent = new DataContent(cr);
            repositoryService.repository(dataContent.value().toString());
        }
    }


    @KafkaListener(id = "DDL", topics = "${ddl.kafka.topics}", groupId = "DDL")
    public void listenDDL(ConsumerRecord<?, ?> cr) {
        if (cr.value() != null) {
            DDLContent ddlContent = new DDLContent(cr);
            if (!StringUtils.isEmpty(ddlContent.value())) {
                repositoryService.executeDDL(ddlContent.value().toString());
            }
        }
    }

}
