package com.erigeo.orderms.service;

import com.erigeo.orderms.listener.dto.OrderCreatedEvent;
import com.erigeo.orderms.model.Order;
import com.erigeo.orderms.model.Product;
import com.erigeo.orderms.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
        entity.setProductsOrder(getAllProductsFromPayload(order)
        );
        entity.setTotalPrice(calculateTotalPrice(order));


        orderRepository.save(entity);
    }

    private static List<Product> getAllProductsFromPayload(OrderCreatedEvent order){
        List<Product> products = new ArrayList<>();
        try {
            products = order.itens().stream()
                    .map(i -> new Product(i.produto(), i.quantidade(), i.preco()))
                    .toList();
            if (products.isEmpty()) {
                System.out.println("MERDA");
            }
            order.itens().stream().forEach(p -> System.out.println(p.produto() + " ebaa"));
        }catch(Exception e){
            System.out.println("MERDAA");
            System.out.println(e);
        }
        return products;
    }



    private BigDecimal calculateTotalPrice(OrderCreatedEvent order){
        return order.itens().stream().map(
                m -> BigDecimal.valueOf(m.quantidade()).multiply(m.preco()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

    }



}
