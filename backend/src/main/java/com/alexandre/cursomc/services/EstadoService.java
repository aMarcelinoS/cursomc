package com.alexandre.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexandre.cursomc.domain.Estado;
import com.alexandre.cursomc.repositories.EstadoRepository;

@Service
public class EstadoService {

	@Autowired
	private EstadoRepository estadoRepository;
	
	//Retorna uma lista de estados ordenados por nome
	public List<Estado> findByEstado() {		
		 return estadoRepository.findAllByOrderByNome();		
	}
}
