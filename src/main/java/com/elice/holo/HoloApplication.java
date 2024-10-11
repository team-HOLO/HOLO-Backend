package com.elice.holo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO) //page 직렬화 Warning 해결
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class HoloApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoloApplication.class, args);
	}

}
