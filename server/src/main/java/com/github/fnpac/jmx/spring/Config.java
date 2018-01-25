package com.github.fnpac.jmx.spring;

import com.github.fnpac.jmx.Scheduler;
import com.github.fnpac.jmx.SchedulerManagedOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MethodExclusionMBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MethodNameBasedMBeanInfoAssembler;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import javax.management.MBeanServer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 刘春龙 on 2018/1/24.
 */
@Configuration
@ImportResource("classpath:spring/spring-ctx.xml")
public class Config {


    ////////////////////////////////////////////////////////////////////
    //                  TODO MBean导出器
    ////////////////////////////////////////////////////////////////////

    /**
     * {@link MBeanExporter}可以把一个或多个Spring bean导出为MBean服务器（MBean server） 内的模型 MBean
     * <p>
     * 使用注解驱动的MBean不支持Java config配置，需要结合spring xml context命名空间提供的支持
     * <p>
     * 默认情况下， MBeanExporter将抛出InstanceAlreadyExistsException异常，
     * 该异常表明MBean服务器中已经存在相同名字的MBean。
     * 不过， 我们可以通过MBeanExporter的{@code registrationPolicy}属性
     * 或者{@code <context:mbean-export>}的registration属性指定冲突处理机制来改变默认行为
     *
     * @param scheduler 要导出为模型 MBean的Spring bean
     * @return
     */
//    采用spring xml配置，以支持注解配置
//    @Bean
    public MBeanExporter mBeanExporter(Scheduler scheduler, @Qualifier("mbeanServer") MBeanServer mBeanServer) {
        MBeanExporter mBeanExporter = new MBeanExporter();

//        每个Map条目的key就是MBean的名称（由管理域的名字
//        和一个key-value对组成， 在SpittleController MBean示例中
//        是spitter:name=HomeController） ， 而Map条目的值则是需要
//        暴露的Spring bean引用。
        Map<String, Object> beans = new HashMap<>();
        beans.put("scheduler:name=Scheduler", scheduler);

        mBeanExporter.setBeans(beans);

//        设置MBean信息装配器
//        mBeanExporter.setAssembler(methodNameBasedMBeanInfoAssembler());
//        mBeanExporter.setAssembler(methodExclusionMBeanInfoAssemble());
        mBeanExporter.setAssembler(interfaceBasedMBeanInfoAssembler());

        mBeanExporter.setRegistrationPolicy(RegistrationPolicy.FAIL_ON_EXISTING);
        mBeanExporter.setServer(mBeanServer);
        return mBeanExporter;
    }

    ////////////////////////////////////////////////////////////////////
    //                  TODO MBean服务器
    ////////////////////////////////////////////////////////////////////

    /**
     * MBeanServerFactoryBean会创建一个MBean服务器， 并将其作为Spring应用上下文中的bean。
     * 默认情况下， 这个bean的ID是mbeanServer
     * <p>
     * spring boot JmxAutoConfiguration自动配置，当这里配置后，自动配置就不会生效
     *
     * @return
     */
//    采用spring xml配置，以支持注解配置
//    @Bean
    public MBeanServerFactoryBean mBeanServerFactoryBean() {
        MBeanServerFactoryBean factoryBean = new MBeanServerFactoryBean();
//        先查找正在运行的MBeanServer，否则总会创建一个新的MBeanServer
        factoryBean.setLocateExistingServerIfPossible(true);
        return factoryBean;
    }

    ////////////////////////////////////////////////////////////////////
    //                  TODO MBean属性装配器
    ////////////////////////////////////////////////////////////////////

    /**
     * MBean信息装配器
     * 一、通过名称暴露方法
     * <p>
     * 指定哪些方法需要暴露为MBean的托管操作
     *
     * @return
     */
    @Bean
    public MethodNameBasedMBeanInfoAssembler methodNameBasedMBeanInfoAssembler() {
        MethodNameBasedMBeanInfoAssembler assembler = new MethodNameBasedMBeanInfoAssembler();
        assembler.setManagedMethods("getContent",
                "setContent");
        return assembler;
    }

    /**
     * MBean信息装配器
     * 一、通过名称暴露方法
     * <p>
     * 排除哪些方法需要暴露为MBean的托管操作
     *
     * @return
     */
    @Bean
    public MethodExclusionMBeanInfoAssembler methodExclusionMBeanInfoAssemble() {
        MethodExclusionMBeanInfoAssembler assembler = new MethodExclusionMBeanInfoAssembler();
        assembler.setIgnoredMethods("execute");
        return assembler;
    }

    /**
     * MBean信息装配器
     * 二、使用接口定义MBean的操作和属性
     *
     * @return
     */
    @Bean
    public InterfaceBasedMBeanInfoAssembler interfaceBasedMBeanInfoAssembler() {
        InterfaceBasedMBeanInfoAssembler assembler = new InterfaceBasedMBeanInfoAssembler();
        assembler.setManagedInterfaces(SchedulerManagedOptions.class);
        return assembler;
    }

    ////////////////////////////////////////////////////////////////////
    //              TODO 导出为远程MBean，支持远程访问
    ////////////////////////////////////////////////////////////////////

    /**
     * 声明RmiRegistryFactoryBean来启动一个RMI注册表
     * <p>
     * 这里需要注意的是{@link RmiRegistryFactoryBean} 和 {@link ConnectorServerFactoryBean} 的声明顺序，
     * 要保证rmi服务先启动，再启动MBean远程调用服务
     *
     * @return
     */
    @Bean
    public RmiRegistryFactoryBean rmiRegistryFactoryBean() {
        RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
        rmiRegistryFactoryBean.setPort(1099);
        return rmiRegistryFactoryBean;
    }

    /**
     * JSR-160 Connectors
     * <p>
     * 导出为远程MBean，支持远程访问
     * <p>
     * ConnectorServerFactoryBean会创建和启动 JSR-160 JMXConnectorServer。
     * <p>
     * 默认情况下， 服务器使用JMXMP协议并监听端口9875——因此， 它将绑定 service:jmx:jmxmp://localhost:9875
     * <p>
     * 根据不同JMX的实现， 我们有多种远程访问协议可供选择， 包括远程方法调用（Remote Method Invocation， RMI） 、 SOAP、 Hessian/Burlap和IIOP（Internet InterORB Protocol） 。
     * 为MBean绑定不同的远程访问协议， 我们仅需要设置ConnectorServerFactoryBean的serviceUrl属性。
     * <p>
     * 在这里， 我们将ConnectorServerFactoryBean绑定到了一个RMI注册表， 该注册表监听本机的1099端口。
     * 这意味着我们需要一个RMI注册表运行时，并监听该端口。
     * Spring中声明RmiRegistryFactoryBean来启动一个RMI注册表
     *
     * @return
     */
    @Bean
    public ConnectorServerFactoryBean connectorServerFactoryBean() {
        ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setServiceUrl("service:jmx:rmi://localhost/jndi/rmi://localhost:1099/scheduler");
        return connectorServerFactoryBean;
    }
}
