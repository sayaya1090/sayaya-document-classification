package net.sayaya.document.modeler

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@SpringBootApplication
@EnableAsync
@EnableDiscoveryClient
class Application : AsyncConfigurer {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }
    }
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 10
        executor.setQueueCapacity(5)
        executor.initialize()
        return executor
    }
    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return SimpleAsyncUncaughtExceptionHandler()
    }
}