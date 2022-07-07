package com.alexandre.cursomc.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.alexandre.cursomc.services.DBService;
import com.alexandre.cursomc.services.EmailService;
import com.alexandre.cursomc.services.SmtpEmailService;

@Configuration
@Profile("dev")
public class DevConfig {
	
	@Autowired
	DBService dbService;
	
	@Bean
	public boolean instatiateDatabase() throws ParseException {
		dbService.instantiateTestDatabase();
		return true;
	}
	
	//Retorna uma instância de SmtpEmailService
		@Bean
		public EmailService mailService() {
			return new SmtpEmailService();
		}
}
