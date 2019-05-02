package com.spronq.mbt.VdekMock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.spronq.mbt.VdekMock"})
public class VdekMockApplication {

	public static void main(String[] args) {
	    // SpringApplication.run(VdekMockApplication.class, args);
		// This is needed to make SpringFox work in SpringBoot 2
		final SpringApplication application = new SpringApplication(VdekMockApplication.class);
		application.setWebApplicationType(WebApplicationType.SERVLET);
		application.run(args);
	}
}


