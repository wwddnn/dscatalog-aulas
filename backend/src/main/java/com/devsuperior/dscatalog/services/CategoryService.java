package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> list = repository.findAll();
        return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category(); //crio uma entidade no banco, e vou atribuir o valor do dto para ela
        entity.setName(dto.getName());
        entity = repository.save(entity); //para salvar essa entidade ja com os novos dados, no banco
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = repository.getReferenceById(id); //metodo getReferenceById() nao toca no banco de dados
            entity.setName(dto.getName());
            entity = repository.save(entity); // agora sim estou salvando a entidade com os dados novos no banco de dados
            return new CategoryDTO(entity);
        }
        catch (EntityNotFoundException e){ //se estourar essa exceção, vou lançar a minha exceção ResourceNotFoundException
            throw new ResourceNotFoundException("Id not found" + id);
            }
        }
}
