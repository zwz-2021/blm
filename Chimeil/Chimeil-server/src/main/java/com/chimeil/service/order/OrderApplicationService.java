package com.chimeil.service.order;

import com.alibaba.fastjson.JSONObject;
import com.chimeil.constant.MessageConstant;
import com.chimeil.context.BaseContext;
import com.chimeil.dto.*;
import com.chimeil.entity.*;
import com.chimeil.exception.AddressBookBusinessException;
import com.chimeil.exception.OrderBusinessException;
import com.chimeil.exception.ShoppingCartBusinessException;
import com.chimeil.infrastructure.adapter.address.AddressBookRepository;
import com.chimeil.infrastructure.adapter.cart.ShoppingCartAdapter;
import com.chimeil.infrastructure.adapter.order.OrderRepository;
import com.chimeil.infrastructure.adapter.payment.PaymentAdapter;
import com.chimeil.infrastructure.adapter.user.UserRepository;
import com.chimeil.result.PageResult;
import com.chimeil.service.OrderService;
import com.chimeil.vo.OrderPaymentVO;
import com.chimeil.vo.OrderStatisticsVO;
import com.chimeil.vo.OrderSubmitVO;
import com.chimeil.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单应用服务 —— 负责订单创建、支付和再下单的核心编排。
 * 状态流转委托给 {@link OrderStateService}，查询委托给 {@link OrderQueryService}。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderApplicationService implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressBookRepository addressBookRepository;
    private final ShoppingCartAdapter shoppingCartAdapter;
    private final UserRepository userRepository;
    private final PaymentAdapter paymentAdapter;
    private final OrderNumberGenerator orderNumberGenerator;
    private final PaymentCallbackHandler paymentCallbackHandler;
    private final OrderStateService orderStateService;
    private final OrderQueryService orderQueryService;

    // ==================== 订单创建 ====================

    /**
     * 用户下单
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 校验地址簿
        AddressBook addressBook = addressBookRepository.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 校验购物车
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartAdapter.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 创建订单
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(orderNumberGenerator.next());
        orders.setAddress(addressBook.getDetail());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orderRepository.insertOrder(orders);

        // 创建订单明细
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderRepository.insertOrderDetails(orderDetailList);

        // 清空购物车
        shoppingCartAdapter.deleteByUserId(userId);

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }

    /**
     * 再来一单
     */
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        List<OrderDetail> orderDetailList = orderRepository.getDetailsByOrderId(id);

        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartAdapter.insertBatch(shoppingCartList);
    }

    // ==================== 支付 ====================

    /**
     * 订单支付
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        Long userId = BaseContext.getCurrentId();
        User user = userRepository.getById(userId);

        Orders ordersDB = orderRepository.getByNumberAndUserId(ordersPaymentDTO.getOrderNumber(), userId);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (Orders.PAID.equals(ordersDB.getPayStatus())) {
            throw new OrderBusinessException("该订单已支付");
        }

        JSONObject jsonObject = paymentAdapter.createPayment(
                ordersDB.getNumber(),
                ordersDB.getAmount(),
                "订单支付",
                user.getOpenid());

        if (jsonObject.getString("code") != null && "ORDERPAID".equals(jsonObject.getString("code"))) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        return vo;
    }

    /**
     * 支付成功回调
     */
    public void paySuccess(String outTradeNo) {
        paymentCallbackHandler.handlePaySuccess(outTradeNo);
    }

    // ==================== 委托：状态流转 → OrderStateService ====================

    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        orderStateService.confirm(ordersConfirmDTO);
    }

    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        orderStateService.rejection(ordersRejectionDTO);
    }

    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        orderStateService.cancel(ordersCancelDTO);
    }

    public void userCancelById(Long id) throws Exception {
        orderStateService.userCancelById(id);
    }

    public void delivery(Long id) {
        orderStateService.delivery(id);
    }

    public void complete(Long id) {
        orderStateService.complete(id);
    }

    public void reminder(Long id) {
        orderStateService.reminder(id);
    }

    // ==================== 委托：查询 → OrderQueryService ====================

    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        return orderQueryService.pageQuery4User(pageNum, pageSize, status);
    }

    public OrderVO details(Long id) {
        return orderQueryService.details(id);
    }

    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        return orderQueryService.conditionSearch(ordersPageQueryDTO);
    }

    public OrderStatisticsVO statistics() {
        return orderQueryService.statistics();
    }
}
