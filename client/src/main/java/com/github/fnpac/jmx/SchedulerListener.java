package com.github.fnpac.jmx;

import org.springframework.stereotype.Component;

import javax.management.Notification;
import javax.management.NotificationListener;
import java.util.logging.Logger;

/**
 * Created by 刘春龙 on 2018/1/25.
 *
 * 监听MBean通知
 */
@Component
public class SchedulerListener implements NotificationListener {

    private static final Logger logger = Logger.getLogger(SchedulerListener.class.getName());

    @Override
    public void handleNotification(Notification notification, Object handback) {
        logger.info("Message - " + notification.getMessage());
        logger.info("Type - " + notification.getType());
        logger.info("Source - " + notification.getSource());
        logger.info("UserData - " + notification.getUserData());
        logger.info("SequenceNumber - " + notification.getSequenceNumber());
        logger.info("TimeStamp - " + notification.getTimeStamp());
    }
}

