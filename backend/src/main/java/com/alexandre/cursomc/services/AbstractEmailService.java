package com.alexandre.cursomc.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import com.alexandre.cursomc.domain.Pedido;

public abstract class AbstractEmailService implements EmailService {

	//busca o endereço de e-mail no application.properties
	@Value("${default.sender}")
	private String sender;
	
	// Recebe o email setado no objeto sm e envia o mesmo confirmando o pedido 
	@Override
	public void sendOrderConfirmationEmail(Pedido obj) {
		SimpleMailMessage sm = prepareSimpleMailMessageFromPedido(obj);
		sendEmail(sm);
		
	}
	
	//Seta os campos do email a ser enviado
	protected SimpleMailMessage prepareSimpleMailMessageFromPedido(Pedido obj) {
		SimpleMailMessage sm = new SimpleMailMessage();
		sm.setTo(obj.getCliente().getEmail());
		sm.setFrom(sender);
		sm.setSubject("Pedido Confirmado! Código: " + obj.getId());
		sm.setSentDate(new Date(System.currentTimeMillis()));
		sm.setText(obj.toString());
		return sm; 
	}	
}
