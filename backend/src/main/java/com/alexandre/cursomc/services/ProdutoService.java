package com.alexandre.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alexandre.cursomc.domain.Produto;
import com.alexandre.cursomc.repositories.ProdutoRepository;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	public Optional<Produto> find(Integer id) {
		Optional<Produto> prod = produtoRepository.findById(id);
		return prod;
	}
}
