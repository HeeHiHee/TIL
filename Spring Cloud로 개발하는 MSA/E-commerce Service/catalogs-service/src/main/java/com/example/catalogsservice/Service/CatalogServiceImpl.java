package com.example.catalogsservice.Service;

import com.example.catalogsservice.JPA.CatalogEntity;
import com.example.catalogsservice.JPA.CatalogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j // 로그출력
public class CatalogServiceImpl implements CatalogService{
    CatalogRepository catalogRepository;
    Environment env;
    @Autowired
    public CatalogServiceImpl(CatalogRepository catalogRepository, Environment env) {
        this.catalogRepository = catalogRepository;
        this.env = env;
    }

    @Override
    public Iterable<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }
}
