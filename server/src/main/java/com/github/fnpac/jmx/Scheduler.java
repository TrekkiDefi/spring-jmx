package com.github.fnpac.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedNotification;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.management.Notification;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Created by 刘春龙 on 2018/1/24.
 */
@Component
@ManagedResource(objectName = "scheduler:name=Scheduler")
@ManagedNotification(name = "SchedulerNotification",
        notificationTypes = {
                "scheduler-notif-set_content",
                "scheduler-notif-get_content"}
)
public class Scheduler implements NotificationPublisherAware {

    private static final Logger logger = Logger.getLogger(Scheduler.class.getName());

    private String content = "init value";

    /**
     * 基于监听的方式，设置服务端通知发送器
     */
    private NotificationPublisher notificationPublisher;

    private AtomicLong sequenceNumber = new AtomicLong(0);

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
        this.notificationPublisher.sendNotification(
                new Notification("scheduler-notif-get_content",
                        this,
                        sequenceNumber.addAndGet(1),
                        System.currentTimeMillis())
        );
        return content;
    }

    @ManagedAttribute
    @ManagedOperation
    public void setContent(String content) {
        this.content = content;
        this.notificationPublisher.sendNotification(
                new Notification("scheduler-notif-set_content",
                        this,
                        sequenceNumber.addAndGet(1),
                        System.currentTimeMillis())
        );
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void execute() {
        logger.info("【Job】 - " + content);
    }

    @Override
    public void setNotificationPublisher(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }
}
