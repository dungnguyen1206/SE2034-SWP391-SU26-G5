package vn.edu.fpt.SE2034_SWP391_G5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Se2034Swp391G5Application {

	public static void main(String[] args) {
		SpringApplication.run(Se2034Swp391G5Application.class, args);
	}
}
