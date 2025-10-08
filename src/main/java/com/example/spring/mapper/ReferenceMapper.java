package com.example.spring.mapper;

import com.example.spring.model.BaseEntity;

import jakarta.persistence.EntityManager;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;

import org.springframework.beans.factory.annotation.Autowired;

//конвертация из DTO в свойство, чтобы выполнить эту операцию, нужно сделать запрос в базу данных и извлечь объект

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ReferenceMapper {
    @Autowired
    private EntityManager entityManager;

    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }
}
