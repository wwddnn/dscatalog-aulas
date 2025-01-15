package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) { //deleção tem alguns problemas, se eu tentar deletar um id inexistente ou que não pode ser deletado do banco
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado " + id);
        }
        try{
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) { //tentar apagar algo que comprometa a integridade do banco, lança essa exceção
            throw new DatabaseException("Falha de integridade referencial"); //criar a exceção DatabaseException dentro do service exceptions
        }
    }

}
