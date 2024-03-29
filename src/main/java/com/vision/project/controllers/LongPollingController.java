package com.vision.project.controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.DTOs.UserDto;
import com.vision.project.models.DTOs.UserRequestDto;
import com.vision.project.services.base.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users/polling")
public class LongPollingController {
    private final UserService userService;
    private final OrderService orderService;
    private final ChatService chatService;
    private final LongPollingService longPollingService;
    private final RestaurantService restaurantService;

    public LongPollingController(UserService userService, OrderService orderService, ChatService chatService, LongPollingService longPollingService, RestaurantService restaurantService) {
        this.userService = userService;
        this.orderService = orderService;
        this.chatService = chatService;
        this.longPollingService = longPollingService;
        this.restaurantService = restaurantService;
    }

    @PostMapping("/login")
    public UserDto login(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return initializeUser(userDetails.getUserModel());
    }

    @GetMapping(value = "/auth/getLoggedUser")
    public UserDto getLoggedUser(){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return initializeUser(userService.findById(loggedUser.getId()));
    }

    @Transactional
    public UserDto initializeUser(UserModel user){
        Restaurant restaurant = user.getRestaurant();

        Map<Long, Chat> chats = chatService.findAllUserChats(user.getId());
        Map<Long, Order> orders = orderService.findNotReady(restaurant);
        LocalDateTime lastCheck = LocalDateTime.now();
        RestaurantDto restaurantDto = new RestaurantDto(restaurant, orders);

        UserRequest userRequest = new UserRequest(user.getId(), restaurant, null);
        longPollingService.addRequest(userRequest);

        return new UserDto(user, restaurantDto, lastCheck, chats);
    }

    @PostMapping(value = "/auth/waitData")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public DeferredResult<UserRequestDto> waitData(@RequestBody LocalDateTime lastCheck){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        DeferredResult<UserRequestDto> request = new DeferredResult<>(100_000L,"Time out.");

        UserRequest userRequest = new UserRequest(loggedUser.getId(), restaurantService.getById(loggedUser.getRestaurantId()), request, lastCheck);

        Runnable onTimeoutOrCompletion = ()-> userRequest.setRequest(null);
        request.onTimeout(onTimeoutOrCompletion);
        request.onCompletion(onTimeoutOrCompletion);

        longPollingService.setAndAddRequest(userRequest);

        return request;
    }
}
