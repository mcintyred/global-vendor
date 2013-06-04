package gv.products.service.repository;

import gv.products.service.entity.ProductEntity;

import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductEntity, Long>{
}
