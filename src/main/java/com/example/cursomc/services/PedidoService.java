package com.example.cursomc.services;

import com.example.cursomc.domain.Cliente;
import com.example.cursomc.domain.ItemPedido;
import com.example.cursomc.domain.PagamentoComBoleto;
import com.example.cursomc.domain.Pedido;
import com.example.cursomc.domain.enums.EstadoPagamento;
import com.example.cursomc.repositories.ItemPedidoRepository;
import com.example.cursomc.repositories.PagamentoRepository;
import com.example.cursomc.repositories.PedidoRepository;
import com.example.cursomc.security.UserSS;
import com.example.cursomc.services.exceptions.AuthorizationException;
import com.example.cursomc.services.exceptions.ObjectNotFoundExpection;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmailService emailService;
    


    public Pedido find(Integer id) {

        Optional<Pedido> obj = pedidoRepository.findById(id);

        if (obj.isEmpty()) {
            throw new ObjectNotFoundExpection("Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName());
        }
        return  obj.get();
    }

    @Transactional
    public Pedido insert(Pedido obj) {

        obj.setId(null);
        obj.setInstante(new Date());
        obj.setCliente(clienteService.find(obj.getCliente().getId()));
        obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
        obj.getPagamento().setPedido(obj);

        if (obj.getPagamento() instanceof PagamentoComBoleto) {
            
            PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
            boletoService.preencherPagementoComBoleto(pagto, obj.getInstante());
        }

        obj = pedidoRepository.save(obj);
        pagamentoRepository.save(obj.getPagamento());

        for (ItemPedido ip : obj.getItens()) {

            ip.setDesconto(0.0);
            ip.setProduto(produtoService.find(ip.getProduto().getId()));
            ip.setPreco(ip.getProduto().getPreco());
            ip.setPedido(obj);
        }

        itemPedidoRepository.saveAll(obj.getItens());
        emailService.sendOrderConfirmationEmail(obj);
        
        return obj;
    }

    public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		Cliente cliente =  clienteService.find(user.getId());
		return pedidoRepository.findByCliente(cliente, pageRequest);
	}
}
