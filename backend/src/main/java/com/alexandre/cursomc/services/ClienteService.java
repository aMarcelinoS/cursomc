package com.alexandre.cursomc.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alexandre.cursomc.domain.Cidade;
import com.alexandre.cursomc.domain.Cliente;
import com.alexandre.cursomc.domain.Endereco;
import com.alexandre.cursomc.domain.enums.Perfil;
import com.alexandre.cursomc.domain.enums.TipoCliente;
import com.alexandre.cursomc.dto.ClienteDTO;
import com.alexandre.cursomc.dto.ClienteNewDTO;
import com.alexandre.cursomc.repositories.ClienteRepository;
import com.alexandre.cursomc.repositories.EnderecoRepository;
import com.alexandre.cursomc.security.UserSS;
import com.alexandre.cursomc.services.exceptions.AuthorizationException;
import com.alexandre.cursomc.services.exceptions.DataIntegrityException;
import com.alexandre.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private ImageService imageService;
	
	@Value("${img.prefix.client.profile}")
	private String prefix;
	
	@Value("${img.profile.size}")
	private Integer size;

	// Busca um cliente por id no banco de dados
	public Cliente find(Integer id) {
		UserSS user = UserService.authenticated();
		
		if(user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = clienteRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	// Busca todos clientes no banco de dados
	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
	
	//Busca cliente por e-mail
	public Cliente findByEmail(String email) {
		
		UserSS user = UserService.authenticated();
		if(user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Cliente obj = clienteRepository.findByEmail(email);
		if(obj == null) {
			throw new ObjectNotFoundException("Objeto não encontrado! Id: " + user.getId() + ", Tipo: " + Cliente.class.getName());
		}
		return obj;
	}

	// Atualiza cliente cadastrado no BD
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj); //<- método auxiliar
		return clienteRepository.save(newObj);
	}
	
	//Método auxiliar para atualizar os dados do novo obj (newObj) com base no objeto que veio como argumento (obj)
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());		
	}
	
	//Insere um novo cliente no BD
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = clienteRepository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
		
	}

	// Deleta um cliente cadastrado pelo id
	public void delete(Integer id) {
		find(id);
		try {
			clienteRepository.deleteById(id);
		}catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados");
		}
	}

	// Paginação com parâmetros opcionais
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return clienteRepository.findAll(pageRequest);
	}

	//Instancia o Cliente através do seu DTO
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cliente = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()), bCryptPasswordEncoder.encode(objDto.getSenha()));
		Cidade cidade = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getComplemento(), objDto.getNumero(), objDto.getBairro(), objDto.getCep(), cliente, cidade);
		cliente.getEnderecos().add(end);
		cliente.getTelefones().add(objDto.getTelefone1());
		if(objDto.getTelefone2()!=null) {
			cliente.getTelefones().add(objDto.getTelefone2());
		}
		if(objDto.getTelefone3()!=null) {
			cliente.getTelefones().add(objDto.getTelefone3());
		}
		return cliente;
	}
	
	//Envia foto de perfil do cliente
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if(user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);
		jpgImage = imageService.cropSquare(jpgImage); /*recorta a imagem para ficar quadrada */
		jpgImage = imageService.resize(jpgImage, size); /*redimensiona a imagem*/
		
		
		String fileName = prefix + user.getId() + ".jpg";
		
		return s3Service.uploadFile(imageService.getInputStream(jpgImage, "jpg"), fileName, "image");
	}
}
