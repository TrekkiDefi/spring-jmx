package com.github.fnpac.jmx.spring;

import com.github.fnpac.jmx.SchedulerListener;
import com.github.fnpac.jmx.SchedulerManagedOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by 刘春龙 on 2018/1/24.
 */
@Configuration
public class Config {

    ////////////////////////////////////////////////////////////////////
    //                  TODO 访问远程MBean
    ////////////////////////////////////////////////////////////////////

    /**
     * 访问远程MBean
     * <p>
     * spring boot JmxAutoConfiguration自动配置，当这里配置后，自动配置就不会生效
     * <p>
     * <p>
     * 由{@link MBeanServerConnectionFactoryBean}所生成的{@link MBeanServerConnection}实际上是作为远程MBean服务器的本地代理。
     * 它能够以MBeanServerConnection的形式注入到其他bean的属性中
     *
     * @return
     * @throws MalformedURLException
     */
    @Bean
    public MBeanServerConnectionFactoryBean mBeanServerConnectionFactoryBean() throws MalformedURLException {
        MBeanServerConnectionFactoryBean connectionFactoryBean = new MBeanServerConnectionFactoryBean();
        connectionFactoryBean.setServiceUrl("service:jmx:rmi://localhost/jndi/rmi://localhost:1099/scheduler");
//        设置是否在启动时连接到服务器。默认值是"true"。
//        可以关闭以允许JMX服务器启动晚一些。在这种情况下，首次访问时将获取JMX连接器。
        connectionFactoryBean.setConnectOnStartup(true);
        return connectionFactoryBean;
    }

    @Bean
    public MBeanProxyFactoryBean schedulerProxy(MBeanServerConnection server) throws MalformedObjectNameException {
        MBeanProxyFactoryBean schedulerProxy = new MBeanProxyFactoryBean();
        schedulerProxy.setObjectName("scheduler:name=Scheduler");
        schedulerProxy.setServer(server);
        schedulerProxy.setProxyInterface(SchedulerManagedOptions.class);
        return schedulerProxy;
    }

    ////////////////////////////////////////////////////////////////////
    //                  TODO 监听远程MBean
    ////////////////////////////////////////////////////////////////////
    @Bean
    public SchedulerListener mBeanNotificationExporter(MBeanServerConnection connection) throws MalformedObjectNameException, IOException, InstanceNotFoundException {

        SchedulerListener schedulerListener = new SchedulerListener();
        connection.addNotificationListener(new ObjectName("scheduler:name=Scheduler"), schedulerListener, null, null);
        return schedulerListener;
    }
}
