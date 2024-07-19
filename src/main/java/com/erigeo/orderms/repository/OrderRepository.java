package com.erigeo.orderms.repository;

import com.erigeo.orderms.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, Long> {
}
