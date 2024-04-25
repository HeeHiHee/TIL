package com.example.catalogsservice.Controller;

import com.example.catalogsservice.JPA.CatalogEntity;
import com.example.catalogsservice.Service.CatalogService;
import com.example.catalogsservice.vo.ResponseCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/catalog-service")
public class CatalogController {
    private Environment env;
    CatalogService catalogService;
    @Autowired
    public CatalogController(Environment env, CatalogService catalogService) {
        this.env = env;
        this.catalogService = catalogService;
    }

    @GetMapping("/health_check")
    public String status() {
        // 카탈로그 서비스의 포트번호 출력으로 잘 작동하는지 확인
        return String.format("It's Working in Catalog Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @GetMapping("/catalogs") //전체카탈로그 조회
    public ResponseEntity<Object> getCatalogs() {
        try {
            Iterable<CatalogEntity> orderList = catalogService.getAllCatalogs();

            List<ResponseCatalog> result = new ArrayList<>();
            orderList.forEach(v -> {
                ResponseCatalog responseCatalog = ResponseCatalog.builder()
                        .productId(v.getProductId())
                        .productName(v.getProductName())
                        .stock(v.getStock())
                        .unitPrice(v.getUnitPrice())
                        .createdAt(v.getCreatedAt())
                        .build();

                result.add(responseCatalog);
            });

            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e) {
            // 오류가 발생한 경우 400 Bad Request 오류와 함께 오류 메시지 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred: " + e.getMessage());
        }
    }
}
