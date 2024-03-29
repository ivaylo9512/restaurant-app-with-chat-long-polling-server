package com.vision.project.models;

import com.vision.project.models.DTOs.UserRequestDto;
import org.springframework.web.context.request.async.DeferredResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class UserRequest {
    private List<Order> orders = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private List<Dish> dishes = new ArrayList<>();
    private DeferredResult<UserRequestDto> request;
    private ReentrantLock lock = new ReentrantLock();
    private Restaurant restaurant;
    private Long userId;
    private LocalDateTime lastCheck;

    public UserRequest(Long userId, Restaurant restaurant, DeferredResult<UserRequestDto> request){
        this.userId = userId;
        this.restaurant = restaurant;
        this.request = request;
    }

    public UserRequest(Long userId, Restaurant restaurant, List<Order> orders, List<Message> messages, List<Dish> dishes){
        this.userId = userId;
        this.restaurant = restaurant;
        this.orders = orders;
        this.messages = messages;
        this.dishes = dishes;
    }

    public UserRequest(Long userId, Restaurant restaurant, DeferredResult<UserRequestDto> request, LocalDateTime lastCheck){
        this.userId = userId;
        this.restaurant = restaurant;
        this.request = request;
        this.lastCheck = lastCheck;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public DeferredResult<UserRequestDto> getRequest() {
        return request;
    }

    public void setRequest(DeferredResult<UserRequestDto> request) {
        this.request = request;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(LocalDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }
}
