package com.neoby.dm.middleware.kafkaConsumer;


import com.neoby.dm.middleware.domain.DataContent;
import com.neoby.dm.middleware.service.RepositoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


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
    @KafkaListener(topics = "${kafka.topics}")
    public void listen(ConsumerRecord<?, ?> cr) {
        if (cr.value() != null) {
            DataContent dataContent = new DataContent(cr);
            repositoryService.repository(dataContent.value().toString());
        }
    }
}
