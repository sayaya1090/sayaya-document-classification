package net.sayaya.document.modeler;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableDiscoveryClient
public class Application implements AsyncConfigurer {
	public static void main(String[] args) {
		try {
			String hostaddress =  InetAddress.getLocalHost().getHostAddress();
			System.out.println("HostAddr:" + hostaddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		SpringApplication.run(Application.class, args);
	}
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(5);
		executor.initialize();
		return executor;
	}
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}
}
