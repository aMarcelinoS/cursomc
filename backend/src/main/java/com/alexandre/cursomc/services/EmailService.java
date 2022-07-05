package com.alexandre.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.alexandre.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
}
