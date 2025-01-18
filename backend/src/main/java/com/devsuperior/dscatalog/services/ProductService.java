package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {


    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true) //usamos PageRequest que ja tinhamos criado na classe Category Resource
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<Product> list = repository.findAll(pageable);
        return list.map(x -> new ProductDTO(x));//uso o construtor so do produto pra nao ficar pesado, sem category
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity, entity.getCategories());//busco produto e category, pois traz nesse metodo so 1 id por vez
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product(); //crio uma entidade no banco, e vou atribuir o valor do dto para ela
        copyDtoToEntity(dto, entity); //metodo novo criado para passar o dto pra entidade.
        entity = repository.save(entity); //para salvar essa entidade ja com os novos dados, no banco
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id); //metodo getReferenceById() nao toca no banco de dados
            copyDtoToEntity(dto, entity);//metodo novo criado para passar o dto pra entidade.
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

    private void copyDtoToEntity(ProductDTO dto, Product entity) { //metodo privado, so essa classe usa
        //o id nao troca na hora de inicializar e nao informa na hora de inserir
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        entity.getCategories().clear();
        for(CategoryDTO catDto : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        }
    }

}
