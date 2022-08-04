package com.alexandre.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alexandre.cursomc.domain.Cliente;
import com.alexandre.cursomc.repositories.ClienteRepository;
import com.alexandre.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	
	@Autowired
	private EmailService emailService;
	
	private Random random = new Random();
	
	//Verifica se o email existe no Banco de Dados
	public void sendNewPassword(String email) {	
		Cliente cliente = clienteRepository.findByEmail(email);
		if(cliente == null) {
			throw new ObjectNotFoundException("Email não encontrado");
		}
		
		//Gera nova senha e faz a alteração no BD
		String newPass = newPassword();
		cliente.setSenha(bCrypt.encode(newPass));
		
		//Salva a nova senha e envia ao cliente
		clienteRepository.save(cliente);
		emailService.sendNewPasswordEmail(cliente, newPass);		
	}
	
	//Gera senha aleatória de 10 caracteres
	private String newPassword() {
		char[] vet = new char[10];
		for(int i = 0; i < 10; i++) {
			vet[i] = randomChar();
		}
		return new String (vet);
	}

	private char randomChar() {
		int opt = random.nextInt(3);
		if(opt == 0) {//Gera um digito
			return (char) (random.nextInt(10) + 48);
		}
		else if(opt == 1) {//Gera letra maiuscula
			return (char) (random.nextInt(26) + 65);
		}
		else {//Gera letra minuscula
			return (char) (random.nextInt(26) + 97);
		}
	}
}
