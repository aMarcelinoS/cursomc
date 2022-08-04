package com.alexandre.cursomc.services;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;

import com.alexandre.cursomc.domain.Cliente;
import com.alexandre.cursomc.domain.Pedido;

public interface EmailService {
	
	//Métodp para versão de texto plano
	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
	
	//Método para versão de html	
	void sendOrderConfirmationHtmlEmail(Pedido obj);
	
	void sendHtmlEmail(MimeMessage msg);

	void sendNewPasswordEmail(Cliente cliente, String newPass);
}
