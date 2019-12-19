package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 启动入口
 */
//启动类启用定时
@EnableScheduling
@SpringBootApplication()
@ComponentScan(basePackages = {"com.*"})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


}
