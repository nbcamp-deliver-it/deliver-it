package com.sparta.deliverit.order.application;

import com.sparta.deliverit.global.response.code.OrderResponseCode;
import com.sparta.deliverit.menu.domain.entity.Menu;
import com.sparta.deliverit.menu.domain.entity.MenuStatus;
import com.sparta.deliverit.menu.domain.repository.MenuRepository;
import com.sparta.deliverit.order.application.dto.CreateOrderCommand;
import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.order.domain.entity.OrderItem;
import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.exception.*;
import com.sparta.deliverit.order.infrastructure.OrderItemRepository;
import com.sparta.deliverit.order.infrastructure.OrderRepository;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForUser;
import com.sparta.deliverit.order.presentation.dto.request.OrderMenuRequest;
import com.sparta.deliverit.order.presentation.dto.response.*;
import com.sparta.deliverit.payment.application.service.PaymentService;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import com.sparta.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import com.sparta.deliverit.user.domain.entity.User;
import com.sparta.deliverit.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final int TIMEOUT_MINUTES = 5;
    private static final Set<OrderStatus> CANCELABLE_STATUS_SET =
            EnumSet.of(OrderStatus.ORDER_COMPLETED, OrderStatus.ORDER_CONFIRMED);

    private static final List<OrderStatus> CANCELABLE_STATUS_LIST =
            List.copyOf(CANCELABLE_STATUS_SET);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PaymentService paymentService;
    private final Clock clock;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, RestaurantRepository restaurantRepository, UserRepository userRepository, MenuRepository menuRepository, PaymentService paymentService, Clock clock) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.clock = clock;
    }

    @Override
    public OrderInfo getOrderDetailForUser(String orderId, String userId) {

        OrderDetailForUser orderDetail = orderRepository.getByOrderIdForUser(orderId)
                .orElseThrow(
                        () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
                );

        if (!userId.equals(orderDetail.getUserId())) {
            throw new AccessDeniedException("해당 주문에 접근할 권한이 없습니다.");
        }

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(orderId);
        if (orderItemList.isEmpty()) {
            throw new NotFoundOrderItemException(OrderResponseCode.NOT_FOUND_ORDER_ITEM);
        }

        List<MenuInfo> menuInfoList = orderItemList.stream()
                .map(MenuInfo::of)
                .toList();

        return OrderInfo.of(orderDetail, menuInfoList);
    }

    @Override
    public OrderInfo getOrderDetailForOwner(String orderId, String userId) {
        OrderDetailForOwner orderDetail = orderRepository.getByOrderIdForOwner(orderId)
                .orElseThrow(
                        () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
                );

        if (!userId.equals(orderDetail.getRestaurantUserId())) {
            throw new AccessDeniedException("현재 사장님이 접근할 수 없는 주문입니다.");
        }

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(orderDetail.getOrderId());

        if (orderItemList.isEmpty()) {
            throw new NotFoundOrderItemException(OrderResponseCode.NOT_FOUND_ORDER_ITEM);
        }

        List<MenuInfo> menuInfoList = orderItemList.stream()
                .map(MenuInfo::of)
                .toList();

        return OrderInfo.of(orderDetail, menuInfoList);
    }

    @Override
    public Page<OrderInfo> getOrderListForUser(String userId, LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize) {
        Page<OrderDetailForUser> orderdetailPage = orderRepository.findOrdersByUserIdForUser(userId, from, to, PageRequest.of(pageNumber, pageSize));
        List<OrderDetailForUser> orderDetailList = orderdetailPage.getContent();

        boolean checkAccess = orderDetailList.stream()
                .map(OrderDetailForUser::getUserId)
                .allMatch(userId::equals);

        if (!checkAccess) {
            throw new AccessDeniedException("주문 목록에 접근할 권한이 없습니다.");
        }

        Set<String> orderIds = orderDetailList.stream()
                .map(OrderDetailForUser::getOrderId)
                .collect(Collectors.toSet());

        Map<String, List<OrderItem>> itemsByOrderId = orderItemRepository.findAllByOrderIn(orderIds).stream()
                .collect(Collectors.groupingBy(orderItem -> orderItem.getOrder().getOrderId()));


        return orderdetailPage.map(o -> {
            List<OrderItem> orderItemList = itemsByOrderId.getOrDefault(o.getOrderId(), List.of());
            if (orderItemList.isEmpty()) {
                throw new NotFoundOrderItemException(OrderResponseCode.NOT_FOUND_ORDER_ITEM);
            }

            List<MenuInfo> menuInfoList = orderItemList.stream()
                    .map(MenuInfo::of)
                    .toList();

            return OrderInfo.of(o, menuInfoList);
        });
    }

    @Override
    public Page<OrderInfo> getOrderListForOwner(String userId, String restaurantId, LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize) {
        Page<OrderDetailForOwner> orderDetailPage = orderRepository.findOrdersByRestaurantIdForOwner(restaurantId, from, to, PageRequest.of(pageNumber, pageSize));
        List<OrderDetailForOwner> orderDetailList = orderDetailPage.getContent();

        boolean checkAccess = orderDetailList.stream()
                .map(OrderDetailForOwner::getRestaurantUserId)
                .allMatch(userId::equals);

        if (!checkAccess) {
            throw new AccessDeniedException("해당 음식점은 접근할 수 없는 주문 목록입니다.");
        }

        Set<String> orderIds = orderDetailList.stream()
                .map(OrderDetailForOwner::getOrderId)
                .collect(Collectors.toSet());

        Map<String, List<OrderItem>> itemsByOrderId = orderItemRepository.findAllByOrderIn(orderIds).stream()
                .collect(Collectors.groupingBy(orderItem -> orderItem.getOrder().getOrderId()));

        return orderDetailPage.map(o -> {
            List<OrderItem> orderItemList = itemsByOrderId.getOrDefault(o.getOrderId(), List.of());
            if (orderItemList.isEmpty()) {
                throw new NotFoundOrderItemException(OrderResponseCode.NOT_FOUND_ORDER_ITEM);
            }

            List<MenuInfo> menuInfoList = orderItemList.stream()
                    .map(MenuInfo::of)
                    .toList();

            return OrderInfo.of(o, menuInfoList);
        });
    }

    @Override
    public ConfirmOrderInfo confirmOrder(String restaurantId, String orderId, String accessUserId) {

        OrderDetailForOwner currOrder = orderRepository.getByOrderIdForOwner(orderId).orElseThrow(
                () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
        );

        if (!accessUserId.equals(currOrder.getRestaurantUserId()) || !restaurantId.equals(currOrder.getRestaurantId())) {
            throw new AccessDeniedException("주문에 접근할 수 없는 사용자입니다.");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime cutOffTime = currOrder.getOrderedAt().plusMinutes(TIMEOUT_MINUTES);
        if (!now.isBefore(cutOffTime)) {
            throw new OrderConfirmTimeOutException(OrderResponseCode.ORDER_CONFIRM_FAIL);
        }

        if (currOrder.getOrderStatus() != OrderStatus.ORDER_COMPLETED) {
            throw new InvalidOrderStatusException(OrderResponseCode.INVALID_ORDER_STATUS);
        }

        LocalDateTime nowMinusMinute = now.minusMinutes(TIMEOUT_MINUTES);
        int queryResult = orderRepository.updateOrderStatusToConfirm(
                orderId,
                restaurantId,
                Long.valueOf(accessUserId),
                OrderStatus.ORDER_COMPLETED,
                OrderStatus.ORDER_CONFIRMED,
                currOrder.getVersion(),
                nowMinusMinute
        );

        if (queryResult == 0) {
            throw new OrderConfirmFailException(OrderResponseCode.ORDER_CONFIRM_FAIL);
        }

        Order nextOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
        );
        return ConfirmOrderInfo.create(nextOrder);
    }

    @Override
    public CancelOrderInfo cancelOrderForUser(String orderId, String accessUserId) {

        OrderDetailForUser currOrder = orderRepository.getByOrderIdForUser(orderId).orElseThrow(
                () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
        );

        if (!accessUserId.equals(currOrder.getUserId())) {
            throw new AccessDeniedException("주문에 접근할 수 없는 사용자입니다.");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime cutOffTime = currOrder.getOrderedAt().plusMinutes(TIMEOUT_MINUTES);
        if (!now.isBefore(cutOffTime)) {
            throw new OrderCancelTimeOutException(OrderResponseCode.ORDER_CANCEL_FAIL);
        }

        if (currOrder.getOrderStatus() != OrderStatus.ORDER_COMPLETED) {
            throw new InvalidOrderStatusException(OrderResponseCode.INVALID_ORDER_STATUS);
        }

        LocalDateTime nowMinusMinute = now.minusMinutes(TIMEOUT_MINUTES);
        int queryResult = orderRepository.updateOrderStatusToCancelForUser(
                orderId,
                Long.valueOf(accessUserId),
                OrderStatus.ORDER_COMPLETED,
                OrderStatus.ORDER_CANCELED,
                currOrder.getVersion(),
                nowMinusMinute
        );

        if (queryResult == 0) {
            throw new OrderCancelFailException(OrderResponseCode.ORDER_CANCEL_SUCCESS);
        }

        Order nextOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
        );

        return CancelOrderInfo.create(nextOrder);
    }

    @Override
    public CancelOrderInfo cancelOrderForOwner(String restaurantId, String orderId, String accessUserId) {

        OrderDetailForOwner currOrder = orderRepository.getByOrderIdForOwner(orderId).orElseThrow(
                () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
        );

        if (!accessUserId.equals(currOrder.getRestaurantUserId()) || !restaurantId.equals(currOrder.getRestaurantId())) {
            throw new AccessDeniedException("주문에 접근할 수 없는 사용자입니다.");
        }

        OrderStatus currentStatus = currOrder.getOrderStatus();
        if (!CANCELABLE_STATUS_SET.contains(currentStatus)) {
            throw new InvalidOrderStatusException(OrderResponseCode.INVALID_ORDER_STATUS);
        }

        int queryResult = orderRepository.updateOrderStatusToCancelForOwner(
                orderId,
                restaurantId,
                Long.valueOf(accessUserId),
                CANCELABLE_STATUS_LIST,
                OrderStatus.ORDER_CANCELED,
                currOrder.getVersion()
        );

        if (queryResult == 0) {
            throw new OrderCancelFailException(OrderResponseCode.ORDER_CANCEL_FAIL);
        }

        Order nextOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
        );

        return CancelOrderInfo.create(nextOrder, currentStatus);
    }

    @Override
    public CreateOrderInfo createOrder(CreateOrderCommand orderCommand, Long userId) {

        Restaurant restaurant = restaurantRepository.findById(orderCommand.getRestaurantId()).orElseThrow(
                () -> new IllegalArgumentException("음식점을 찾을 수 없습니다.")
        );
        if (restaurant.getStatus() != RestaurantStatus.OPEN) {
            throw new OrderCreateFailException(OrderResponseCode.ORDER_CREATE_FAIL);
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        Map<String, Integer> quantityByMenuId = orderCommand.getMenus().stream()
                .collect(Collectors.toMap(
                        OrderMenuRequest::getMenuId,
                        OrderMenuRequest::getQuantity,
                        Integer::sum
                ));
        if (quantityByMenuId.isEmpty()) {
            throw new IllegalArgumentException("주문 메뉴가 비어있습니다.");
        }

        Map<String, Menu> menuByMenuId = menuRepository.findByIdIn(quantityByMenuId.keySet()).stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));

        List<String> missing = quantityByMenuId.keySet().stream()
                .filter(id -> !menuByMenuId.containsKey(id))
                .toList();
        if (!missing.isEmpty()) throw new IllegalArgumentException("존재하지 않는 메뉴입니다. :" + missing);

        BigDecimal totalPrice = quantityByMenuId.keySet().stream()
                .map(key -> {
                    Menu currMenu = menuByMenuId.get(key);
                    if (currMenu.getStatus() != MenuStatus.SELLING) throw new OrderCreateFailException(OrderResponseCode.ORDER_CREATE_FAIL);

                    var quantity = quantityByMenuId.get(key);
                    if (quantity < 1) throw new IllegalArgumentException("주문 메뉴의 수량은 0이 될 수 없습니다.");

                    var price = currMenu.getPrice();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.create(
                user,
                restaurant,
                LocalDateTime.now(clock),
                OrderStatus.PAYMENT_PENDING,
                orderCommand.getDeliveryAddress(),
                totalPrice
        );

        orderRepository.save(order);

        List<OrderItem> orderItemList = quantityByMenuId.keySet().stream()
                .map(key -> {
                    Menu currentMenu = menuByMenuId.get(key);
                    return OrderItem.create(
                            order,
                            currentMenu,
                            currentMenu.getName(),
                            currentMenu.getPrice(),
                            quantityByMenuId.get(key)
                    );
                })
                .toList();

        orderItemRepository.saveAll(orderItemList);

        return CreateOrderInfo.create(
                order.getOrderId(),
                userId,
                user.getPhone(),
                user.getName(),
                totalPrice
        );
    }
}