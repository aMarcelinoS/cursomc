package com.alexandre.cursomc;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alexandre.cursomc.domain.Categoria;
import com.alexandre.cursomc.domain.Produto;
import com.alexandre.cursomc.repositories.CategoriaRepository;
import com.alexandre.cursomc.repositories.ProdutoRepository;

@SpringBootApplication
public class CursomcApplication implements CommandLineRunner{
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;

	public static void main(String[] args) {
		SpringApplication.run(CursomcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		Categoria cat1 = new Categoria(null, "Informática");
		Categoria cat2 = new Categoria(null, "Escritório");
		
		categoriaRepository.saveAll(Arrays.asList(cat1, cat2));
		
		Produto prod1 = new Produto(null, "Computador", 1000.00);
		Produto prod2 = new Produto(null, "Tablet", 600.00);
		
		produtoRepository.saveAll(Arrays.asList(prod1, prod2));
	}
}
