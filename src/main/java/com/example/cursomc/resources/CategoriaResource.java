package com.example.cursomc.resources;

import com.example.cursomc.domain.Categoria;
import com.example.cursomc.dto.CategoriaDTO;
import com.example.cursomc.services.CategoriaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

// EndPoint
@RestController
@RequestMapping(value="/categorias")
public class CategoriaResource {

    @Autowired
    private CategoriaService categoriaService;

    //GET
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ResponseEntity<Categoria> find(@PathVariable Integer id) {

        Categoria obj = categoriaService.find(id);

        return ResponseEntity.ok().body(obj);
    }

    //POST
    @PreAuthorize("hasAnRole('ADMIN')")
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Categoria> insert(@Valid @RequestBody CategoriaDTO objDto) {

        Categoria obj = categoriaService.fromDTO(objDto);
        obj = categoriaService.insert(obj);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(obj.getId())
                    .toUri();

        return ResponseEntity.created(uri).build();
    }

    //PUT
    @PreAuthorize("hasAnRole('ADMIN')")
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ResponseEntity<Categoria> update(@Valid @RequestBody CategoriaDTO objDto, @PathVariable Integer id) {
        Categoria obj = categoriaService.fromDTO(objDto);
        obj.setId(id);
        obj = categoriaService.update(obj);

        return ResponseEntity.noContent().build();
    }

    //DELETE
    @PreAuthorize("hasAnRole('ADMIN')")
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<Categoria> delete(@PathVariable Integer id) {

        categoriaService.delete(id);
        
        return ResponseEntity.noContent().build();
    } 

    // GET
    @RequestMapping(method=RequestMethod.GET)
    public ResponseEntity<List<CategoriaDTO>> findAll() {

        List< Categoria> list = categoriaService.findall();
        List<CategoriaDTO> listDto = list.stream().map(obj -> new CategoriaDTO(obj)).collect(Collectors.toList());

        return ResponseEntity.ok().body(listDto);
    }

    // GET (LISTPAGE)
    @RequestMapping(value="/page", method=RequestMethod.GET)
    public ResponseEntity<Page<CategoriaDTO>> findPage(
        @RequestParam(value="page", defaultValue="0") Integer page,
        @RequestParam(value="linesPerPage", defaultValue="24")Integer linesPerPage,
        @RequestParam(value="orderBy", defaultValue="id")String orderBy,
        @RequestParam(value="direction", defaultValue="ASC")String direction) {
        
        Page<Categoria> list = categoriaService.findPage(page, linesPerPage, orderBy, direction);

        Page<CategoriaDTO> listDto = list.map(obj -> new CategoriaDTO(obj));

        return ResponseEntity.ok().body(listDto);
    }
}
