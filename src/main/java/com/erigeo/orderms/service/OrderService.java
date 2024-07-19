package com.erigeo.orderms.service;

import com.erigeo.orderms.controller.dto.OrderResponse;
import com.erigeo.orderms.listener.dto.OrderCreatedEvent;
import com.erigeo.orderms.model.Order;
import com.erigeo.orderms.model.Product;
import com.erigeo.orderms.repository.OrderRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {

        private final OrderRepository orderRepository;
        private final MongoTemplate mongoTemplate;


    public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
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
                System.out.println("vazio");
            }
            order.itens().stream().forEach(p -> System.out.println(p.produto() + " ebaa"));
        }catch(Exception e){
            System.out.println("algum erro");
            System.out.println(e);
        }
        return products;
    }

    //stream only in payloads, im this case im converting entity to response.
    public Page<OrderResponse> findAllOrdersByCustomerId(Long customerId, PageRequest pageRequest){
      var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);
      //return orders.map(t -> new OrderResponse(t.orderId(), t.customerId(), t.totalOrder()));
        //return orders.map(t -> OrderResponse.fromEntity(t));
        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal calculateTotalValueFromAllOrdersById(Long customerId) {
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );

        var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);
        var result = response.getUniqueMappedResult();

        if (result == null || result.get("total") == null) {
            return BigDecimal.ZERO;  // or handle it according to your business logic
        }

        return new BigDecimal(result.get("total").toString());
    }


    private BigDecimal calculateTotalPrice(OrderCreatedEvent order){
        return order.itens().stream().map(
                m -> BigDecimal.valueOf(m.quantidade()).multiply(m.preco()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

    }



}
