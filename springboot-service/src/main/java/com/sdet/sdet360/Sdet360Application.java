package com.sdet.sdet360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.sdet.sdet360",
    "com.sdet.sdet360.config",
    "com.sdet.sdet360.tenant.controller",
    "com.sdet.sdet360.grpc"
})
public class Sdet360Application {

	public static void main(String[] args) {
		SpringApplication.run(Sdet360Application.class, args);
		System.out.print("Hello Neha..");
	}
}
