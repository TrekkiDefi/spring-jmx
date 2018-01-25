package com.github.fnpac.jmx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.management.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by 刘春龙 on 2018/1/24.
 */
@Component
public class SchedulerConsumer {

    private static final Logger logger = Logger.getLogger(SchedulerConsumer.class.getName());

    @Autowired
    private MBeanServerConnection connection;

    /**
     * 基于代理的方式访问远程MBean
     */
    @Autowired
    private SchedulerManagedOptions schedulerManagedOptions;

    /**
     * 基于MBeanServerConnection直接连接访问远程MBean
     *
     * @throws IOException
     * @throws MalformedObjectNameException
     * @throws AttributeNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     * @throws InstanceNotFoundException
     * @throws InvalidAttributeValueException
     */
//    @Scheduled(cron = "0/10 * * * * ?")
    public void direct() throws IOException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, InvalidAttributeValueException {

//        获取远程MBean服务器中有多少已注册的MBean
        int count = connection.getMBeanCount();
        logger.info("MBean count - " + count);

//        查询远程服务器中所有MBean
        Set<ObjectName> queryNames = connection.queryNames(null, null);
        List<String> domains = queryNames.stream().map(
                ObjectName::getDomain
        ).collect(Collectors.toList());
        logger.info(domains.toString());

//        为了访问MBean属性， 我们可以使用getAttribute()和setAttribute()方法
//        注意：这里的属性会被设置首字母大写。
        queryNames = connection.queryNames(new ObjectName("scheduler:name=Scheduler,*"), null);
        Iterator<ObjectName> iterator = queryNames.iterator();
        if (iterator.hasNext()) {
            String content = (String) connection.getAttribute(
                    iterator.next(),
                    "Content");
            logger.info("Scheduler content value - " + content);
        }

//        使用setAttribute()方法改变MBean属性的值
//        connection.setAttribute(
//                new ObjectName("scheduler:name=Scheduler"),
//                new Attribute("content", "hello_" + System.currentTimeMillis()));

//        调用MBean的操作，需要使用invoke()方法
        connection.invoke(
                new ObjectName("scheduler:name=Scheduler"),
                "setContent",
                new Object[] {"helloworld_" + System.currentTimeMillis()},
                new String[] {String.class.getName()});
    }

    /**
     * 基于代理的方式访问远程MBean
     */
//    @Scheduled(cron = "0/10 * * * * ?")
    public void proxy() {
        String content = schedulerManagedOptions.getContent();
        logger.info("content value - " + content);

        schedulerManagedOptions.setContent("Hello Java");
    }
}
