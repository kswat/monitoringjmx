package com.example;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.support.GenericMessage;


@SpringBootApplication
public class MonitoringdslSplitAppDSL {

	
	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext ctx =
		SpringApplication.run(MonitoringdslSplitAppDSL.class, args);
	
	}
	
	@Bean
	MyExecutor exec() {
	    MyExecutor taskExecutor = new MyExecutor();
	    taskExecutor.setThreadGroupName(getClass().getSimpleName());
	    taskExecutor.setCorePoolSize(10);
	    taskExecutor.setMaxPoolSize(20);
	    taskExecutor.setKeepAliveSeconds(5);
	    taskExecutor.setQueueCapacity(1);
	    taskExecutor.setThreadGroupName("hello");
	    taskExecutor.setThreadNamePrefix("shhh");
	    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
	    return taskExecutor;
	}
	
	@Bean
	public IntegrationFlow theMonitor() {
		
	    return IntegrationFlows.from(() -> new GenericMessage<>("test"),
	                    e -> e.poller(p -> p.cron("0/1 * * * * ?")))
	    		.channel(MessageChannels.executor("executorChannel",exec()))
	    		.channel((onlyChannel()))
				.get();				
	}
	
	@Bean
	public IntegrationFlow printer() {
	return IntegrationFlows.from(onlyChannel())
    .handle( e -> System.out.println(e.getPayload()))
    .get();
	}

	@Bean
	public PublishSubscribeChannel onlyChannel() {
	    return MessageChannels.publishSubscribe().get();
	}

}
