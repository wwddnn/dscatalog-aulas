package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true) //usamos PageRequest que ja tinhamos criado na classe Category Resource
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);
        return list.map(x -> new ProductDTO(x));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product(); //crio uma entidade no banco, e vou atribuir o valor do dto para ela
        //entity.setName(dto.getName());
        entity = repository.save(entity); //para salvar essa entidade ja com os novos dados, no banco
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id); //metodo getReferenceById() nao toca no banco de dados
            //entity.setName(dto.getName());
            entity = repository.save(entity); // agora sim estou salvando a entidade com os dados novos no banco de dados
            return new ProductDTO(entity);
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
