package com.github.fnpac.jmx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.management.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by 刘春龙 on 2018/1/24.
 */
@Component
public class Monitor {

    private static final Logger logger = Logger.getLogger(Monitor.class.getName());

    @Autowired
    private MBeanServerConnection connection;

    @Scheduled(cron = "0/10 * * * * ?")
    public void execute() throws IOException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, InvalidAttributeValueException {

//        获取远程MBean服务器中有多少已注册的MBean
        int count = connection.getMBeanCount();
        logger.info("MBean count - " + count);

//        查询远程服务器中所有MBean
        Set<ObjectName> queryNames = connection.queryNames(null, null);
        List<String> canonicalNames = queryNames.stream().map(
                ObjectName::getCanonicalName
        ).collect(Collectors.toList());

//        为了访问MBean属性， 我们可以使用getAttribute()和setAttribute()方法
        String content = (String) connection.getAttribute(
                new ObjectName("scheduler:name=Scheduler"),
                "content");
        logger.info("Scheduler content value - " + content);

//        使用setAttribute()方法改变MBean属性的值
//        connection.setAttribute(
//                new ObjectName("scheduler", "name", "Scheduler"),
//                new Attribute("content", "hello_" + System.currentTimeMillis()));

//        调用MBean的操作，需要使用invoke()方法
        connection.invoke(
                new ObjectName("scheduler:name=Scheduler"),
                "setContent",
                new Object[] {"helloworld_" + System.currentTimeMillis()},
                new String[] {String.class.getName()});
    }
}
