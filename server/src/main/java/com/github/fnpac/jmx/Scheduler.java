package com.github.fnpac.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Created by 刘春龙 on 2018/1/24.
 */
@Component
@ManagedResource(objectName = "scheduler:name=Scheduler")
public class Scheduler {

    private static final Logger logger = Logger.getLogger(Scheduler.class.getName());

    public String content;

    /**
     * 在暴露MBean功能时， 使用@ManagedOperation注解标注方法是严格限制
     * 方法的， 并不会把它作为JavaBean的存取器方法。 因此，
     * 使用@ManagedOperation可以用来把bean的方法暴露为MBean托管操作，
     * 而使用@ManagedAttribute可以把bean的属性暴露为MBean托管属性
     *
     * @return
     */
    @ManagedAttribute
    @ManagedOperation
    public String getContent() {
        return content;
    }

    @ManagedOperation
    public void setContent(String content) {
        this.content = content;
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void execute() {
        logger.info("【Job】 - " + content);
    }
}
