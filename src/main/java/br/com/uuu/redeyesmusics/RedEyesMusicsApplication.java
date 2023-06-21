package br.com.uuu.redeyesmusics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RedEyesMusicsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedEyesMusicsApplication.class, args);
	}
}
