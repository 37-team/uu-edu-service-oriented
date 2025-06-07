package uu.app.order_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import uu.app.order_service.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> { }