package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

//enxugar sempre a lógica do controlador, ele tem que ser enxuto. colocar as excecoes na classe controllerAdvice

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

    @Autowired
    private CategoryService service;

    @GetMapping //para paginação pode fazer tanto com PageRequest como nessa aula, quanto com Pageable
    public ResponseEntity<Page<CategoryDTO>> findAll(Pageable pageable) {//Pageable : page, size, sort
        Page<CategoryDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        CategoryDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto); //resposta 200
    }

    @PostMapping //para inserir um novo recurso, por padrao do Rest, usamos @PostMapping
    public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO dto) { //retorna no corpo a nova categoria inserida e retorna seu id tbm.
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto); //resposta 201
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> delete(@PathVariable Long id) {
         service.delete(id);
         return ResponseEntity.noContent().build(); //resposta 204
    }
}
