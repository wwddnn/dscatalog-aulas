package com.devsuperior.dscatalog.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

//link github do projeto do professor https://github.com/devsuperior/dscatalog-resources/tree/master/backend

@Entity
@Table(name = "tb_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE") //é UTC ou seja sem time zone //utc é universal time coordinated
    private Instant createdAt; //Instant registra a data e a hora. // usado para dados de auditoria // registra momento da alteração no banco

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

    public Category(){
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() { //vou deixar somente o metodo get para createdAt
        return createdAt;
    }

    public Instant getUpdatedAt() { //vou deixar somente o metodo get para updatedAt
        return updatedAt;
    }

    @PrePersist //anotacao da JPA para antes de chamar o save da JPA, vai fazer o @PrePersist
    public void prePersist() { //antes de persistir no banco, vai no atributo createdAt e registra o momento atual
        createdAt = Instant.now();
    }

    @PreUpdate //anotacao da JPA para antes de chamar o update da JPA, vai fazer o @PreUpdate
    public void preUpdate() { //antes de atualizar no banco, vai no atributo updatedAt e registra o momento atual
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
