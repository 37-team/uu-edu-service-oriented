package uu.app.order_service.service;

import uu.app.order_service.dto.UserDTO;
import uu.app.order_service.entity.OrderEntity;
import uu.app.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    // Оголошення залежностей:
    // -- для збереження замовлень у БД.
    private final OrderRepository orderRepository;
    private final UserClient userClient;

    public OrderService(OrderRepository orderRepository, UserClient userClient) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
    }

    public OrderEntity createOrder(OrderEntity order, String token) {
        UserDTO user = userClient.getUser(order.getUserId(), token);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return orderRepository.save(order);
    }
}