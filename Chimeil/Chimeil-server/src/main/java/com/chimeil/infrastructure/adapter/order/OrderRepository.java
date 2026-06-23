package com.chimeil.infrastructure.adapter.order;

import com.github.pagehelper.Page;
import com.chimeil.dto.OrdersPageQueryDTO;
import com.chimeil.entity.OrderDetail;
import com.chimeil.entity.Orders;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository {

    Orders getById(Long id);

    Orders getByNumber(String number);

    Orders getByNumberAndUserId(String number, Long userId);

    void insertOrder(Orders orders);

    void insertOrderDetails(List<OrderDetail> details);

    int updateByIdAndStatus(Orders orders, Integer expectedStatus);

    int updatePaySuccess(Long id, Integer expectedStatus, Integer expectedPayStatus, LocalDateTime checkoutTime);

    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    Page<Orders> pageQuery(OrdersPageQueryDTO dto);

    List<OrderDetail> getDetailsByOrderId(Long orderId);

    Integer countStatus(Integer status);
}
