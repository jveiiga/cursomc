package com.example.cursomc.resources;

import com.example.cursomc.domain.Pedido;
import com.example.cursomc.services.PedidoService;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value="/pedidos")
public class PedidoResource {

    @Autowired
    private PedidoService pedidoService;

    // GET (ID)
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ResponseEntity<Pedido> find(@PathVariable Integer id) {

        Pedido obj = pedidoService.find(id);

        return ResponseEntity.ok().body(obj);
    }

    //POST
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Pedido> insert(@Valid @RequestBody Pedido obj) {

        obj = pedidoService.insert(obj);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(obj.getId())
                    .toUri();

        return ResponseEntity.created(uri).build();
    }

    // GET (PAGE)
    @RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Page<Pedido>> findPage(
			@RequestParam(value="page", defaultValue="0") Integer page, 
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage, 
			@RequestParam(value="orderBy", defaultValue="instante") String orderBy, 
			@RequestParam(value="direction", defaultValue="DESC") String direction) {
		Page<Pedido> list = pedidoService.findPage(page, linesPerPage, orderBy, direction);
		return ResponseEntity.ok().body(list);
	}
}
