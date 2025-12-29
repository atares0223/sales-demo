package com.waaw.order.repository;

import com.waaw.order.domain.ProductOrder;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<ProductOrder,Long> {

}
