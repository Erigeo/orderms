package com.erigeo.orderms.service;

import com.erigeo.orderms.listener.dto.OrderCreatedEvent;
import com.erigeo.orderms.model.Order;
import com.erigeo.orderms.model.Product;
import com.erigeo.orderms.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderService {

        private final OrderRepository orderRepository;


    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void saveOrder(OrderCreatedEvent order){
        var entity = new Order();
        entity.setOrderId(order.codigoPedido());
        entity.setCustomerId(order.codigoCliente());
        entity.setProductsOrder(order.itens().stream()
                .map(i -> new Product(i.produto(), i.preco(), i.quantidade()))
                .toList()
        );

        orderRepository.save(entity);
    }



}
