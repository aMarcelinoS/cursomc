package com.alexandre.cursomc.services;

import java.util.Date;
import java.util.Calendar;

import org.springframework.stereotype.Service;

import com.alexandre.cursomc.domain.PagamentoComBoleto;

@Service
public class BoletoService {

	//Simula geração de data para um boleto
	public void preencherPagamentoComBoleto(PagamentoComBoleto pgto, Date instanteDoPedido) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(instanteDoPedido);
	cal.add(Calendar.DAY_OF_MONTH, 7);
	pgto.setDataVencimento(cal.getTime());	
	}	
}
