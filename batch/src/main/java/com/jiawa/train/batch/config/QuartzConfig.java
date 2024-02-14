package com.jiawa.train.batch.config;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    // 配置文件路径
    private static final String QUARTZ_CONFIG = "/quartz.properties";


    /**
     * @param myJobFactory 为SchedulerFactory配置JobFactory
     * @return
     * @throws IOException
     */
    @Bean
    SchedulerFactoryBean schedulerFactoryBean(JobFactory myJobFactory) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setJobFactory(myJobFactory);
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true); // 设置自行启动
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    /**
     * 从quartz.properties文件中读取Quartz配置属性
     *
     * @return
     * @throws IOException
     */
    @Bean
    Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();

        propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_CONFIG));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
