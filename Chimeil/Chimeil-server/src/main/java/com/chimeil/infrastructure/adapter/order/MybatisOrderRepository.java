package com.chimeil.infrastructure.adapter.order;

import com.github.pagehelper.Page;
import com.chimeil.dto.OrdersPageQueryDTO;
import com.chimeil.entity.OrderDetail;
import com.chimeil.entity.Orders;
import com.chimeil.mapper.OrderDetailMapper;
import com.chimeil.mapper.OrderMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MybatisOrderRepository implements OrderRepository {

        private final OrderMapper orderMapper;
        private final OrderDetailMapper orderDetailMapper;

    @Override
    public Orders getById(Long id) {
        return orderMapper.getById(id);
    }

    @Override
    public Orders getByNumber(String number) {
        return orderMapper.getByNumber(number);
    }

    @Override
    public Orders getByNumberAndUserId(String number, Long userId) {
        return orderMapper.getByNumberAndUserId(number, userId);
    }

    @Override
    public void insertOrder(Orders orders) {
        orderMapper.insert(orders);
    }

    @Override
    public void insertOrderDetails(List<OrderDetail> details) {
        orderDetailMapper.insertBatch(details);
    }

    @Override
    public int updateByIdAndStatus(Orders orders, Integer expectedStatus) {
        return orderMapper.updateByIdAndStatus(orders, expectedStatus);
    }

    @Override
    public int updatePaySuccess(Long id, Integer expectedStatus, Integer expectedPayStatus, LocalDateTime checkoutTime) {
        return orderMapper.updatePaySuccess(id, expectedStatus, expectedPayStatus, checkoutTime);
    }

    @Override
    public List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime) {
        return orderMapper.getByStatusAndOrderTimeLT(status, orderTime);
    }

    @Override
    public Page<Orders> pageQuery(OrdersPageQueryDTO dto) {
        return orderMapper.pageQuery(dto);
    }

    @Override
    public List<OrderDetail> getDetailsByOrderId(Long orderId) {
        return orderDetailMapper.getByOrderId(orderId);
    }

    @Override
    public Integer countStatus(Integer status) {
        return orderMapper.countStatus(status);
    }
}
