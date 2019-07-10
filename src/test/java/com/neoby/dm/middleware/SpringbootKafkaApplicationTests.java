package com.neoby.dm.middleware;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootKafkaApplicationTests {


    @Test
    public void contextLoads() {
    }


    @Test
    public void redis_put() {
        String key = "2072305";
        String data = "{'database':'zf_config','table':'t_qrtz_scheduler_state','type':'update˙','ts':1551922917,'xid':2072305,'commit':true,'data':{'SCHED_NAME':'quartzScheduler','INSTANCE_NAME':'dev-app1550741785516','LAST_CHECKIN_TIME':1551922916336,'CHECKIN_INTERVAL':20000},'old':{'LAST_CHECKIN_TIME':1551922896334}}";

    }

}
