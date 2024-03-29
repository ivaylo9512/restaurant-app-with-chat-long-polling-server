package com.vision.project.services;

import com.vision.project.models.*;
import com.vision.project.repositories.base.*;
import com.vision.project.services.base.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(Order order){
        return orderRepository.save(order);
    }

    @Override
    public Dish update(long orderId, long dishId, Restaurant restaurant, UserModel loggedUser) {
        Order order = orderRepository.findByIdAndRestaurant(orderId, restaurant)
                .orElseThrow(() -> new EntityNotFoundException("Order not found."));

        order.setReady(true);
        boolean updated = false;

        Dish updatedDish = null;
        for (Dish orderDish: order.getDishes()) {
            if(orderDish.getId() == dishId ){
                if(!orderDish.isReady()) {
                    orderDish.setUpdatedBy(loggedUser);
                    orderDish.setReady(true);

                    updated = true;
                }

                updatedDish = orderDish;
                continue;
            }
            if(!orderDish.isReady()){
                order.setReady(false);
            }
        }

        if(updatedDish == null){
            throw new EntityNotFoundException("Dish not found.");
        }

        if(updated || order.isReady()) {
            orderRepository.save(order);
        }

        return updatedDish;
    }


    @Override
    public Order findById(long id, Restaurant restaurant) {
        return orderRepository.findByIdAndRestaurant(id, restaurant).orElseThrow(() -> new EntityNotFoundException("Order not found."));
    }

    @Override
    public Map<Long, Order> findNotReady(Restaurant restaurant) {
        return orderRepository.findNotReady(restaurant).stream()
                .collect(Collectors.toMap(Order::getId, order -> order, (existing, replacement) -> existing, LinkedHashMap::new));
    }

    @Override
    public List<Order> findMoreRecent(LocalDateTime lastCheck, Restaurant restaurant) {
        return orderRepository.findMoreRecent(lastCheck, restaurant);
    }
}
