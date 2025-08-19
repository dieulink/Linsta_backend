package com.linsta.linsta_backend.service;

import com.linsta.linsta_backend.model.*;
import com.linsta.linsta_backend.repository.*;
import com.linsta.linsta_backend.request.OrderDetailRequest;
import com.linsta.linsta_backend.request.OrderRequest;
import com.linsta.linsta_backend.request.RevenueByMonth;
import com.linsta.linsta_backend.response.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPayMethodId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

        // Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(request.getTotalPrice());
        order.setReceiveAddress(request.getReceiveAddress());
        order.setReceiveName(request.getReceiveName());
        order.setReceivePhone(request.getReceivePhone());
        order.setShipCost(request.getShipCost());
        order.setStatus("Đang chờ đơn vị vận chuyển");
        order.setPaymentMethod(paymentMethod);

        orderRepository.save(order);

        // chi tiết đơn hàng + xóa cart + giảm stock
        for (OrderDetailRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ hàng trong kho.");
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            // tạo chi tiết đơn hàng
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setProductPrice(item.getProductPrice());
            orderDetail.setQuantity(item.getQuantity());

            orderDetailRepository.save(orderDetail);

            // xóa sản phẩm khỏi giỏ hàng
            Optional<CartItem> optionalCartItem = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());
            if (optionalCartItem.isPresent() && request.getIsCart()==1) {
                cartRepository.delete(optionalCartItem.get());
            }
        }

        //tạo payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("Chưa thanh toán");

        paymentRepository.save(payment);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> responseList = new ArrayList<>();

        for (Order order : orders) {
            List<OrderDetail> details = orderDetailRepository.findByOrder(order);

            List<OrderDetailResponse> detailResponses = details.stream()
                    .map(detail -> new OrderDetailResponse(
                            detail.getProduct().getId(),
                            detail.getProduct().getImageUrl(),
                            detail.getProduct().getName(),
                            detail.getProductPrice(),
                            detail.getQuantity()

                    ))
                    .toList();

            OrderResponse response = new OrderResponse();
            response.setOrderId(order.getId());
            response.setUserId(order.getUser().getId());
            response.setQuantity(order.getQuantity());
            response.setTotalPrice(order.getTotalPrice());
            response.setReceiveAddress(order.getReceiveAddress());
            response.setReceiveName(order.getReceiveName());
            response.setReceivePhone(order.getReceivePhone());
            response.setShipCost(order.getShipCost());
            response.setPayMethodId(order.getPaymentMethod().getId());
            response.setItems(detailResponses);
            response.setStatus(order.getStatus());
            response.setCreatedAt(order.getCreatedAt());
            response.setDoneAt(order.getDoneAt());

            responseList.add(response);
        }

        return responseList;
    }

    public OrderResponse getOrdersByOrderId(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);

            List<OrderDetail> details = orderDetailRepository.findByOrder(order.get());

            List<OrderDetailResponse> detailResponses = details.stream()
                    .map(detail -> new OrderDetailResponse(
                            detail.getProduct().getId(),
                            detail.getProduct().getImageUrl(),
                            detail.getProduct().getName(),
                            detail.getProductPrice(),
                            detail.getQuantity()

                    ))
                    .toList();

            OrderResponse response = new OrderResponse();
            response.setOrderId(order.get().getId());
            response.setUserId(order.get().getUser().getId());
            response.setQuantity(order.get().getQuantity());
            response.setTotalPrice(order.get().getTotalPrice());
            response.setReceiveAddress(order.get().getReceiveAddress());
            response.setReceiveName(order.get().getReceiveName());
            response.setReceivePhone(order.get().getReceivePhone());
            response.setShipCost(order.get().getShipCost());
            response.setPayMethodId(order.get().getPaymentMethod().getId());
            response.setItems(detailResponses);
            response.setStatus(order.get().getStatus());
            response.setCreatedAt(order.get().getCreatedAt());
            response.setDoneAt(order.get().getDoneAt());


        return response;
    }

    public Map<String, Object> getOrderGrowthThisMonth() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // Tháng trước
        LocalDate lastMonthDate = now.minusMonths(1);
        int lastMonth = lastMonthDate.getMonthValue();
        int lastMonthYear = lastMonthDate.getYear();

        long thisMonthCount = orderRepository.countOrdersByMonthAndYear(currentMonth, currentYear);
        long lastMonthCount = orderRepository.countOrdersByMonthAndYear(lastMonth, lastMonthYear);

        long difference = thisMonthCount - lastMonthCount;
        double growthRate = lastMonthCount == 0 ? 100.0 : ((double) difference / lastMonthCount) * 100;
        growthRate = Math.round(growthRate * 10.0) / 10.0;
        String growth = "" + growthRate;
        if (growthRate >0) {
            growth = "+"+ growthRate;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("orderThisMonth", thisMonthCount);
        result.put("orderGrowthRate", growth);

        long thisMonthQuantity = orderRepository.sumQuantityByMonthAndYear(currentMonth, currentYear);
        long lastMonthQuantity = orderRepository.sumQuantityByMonthAndYear(lastMonth, lastMonthYear);

        long thisMonthTotalPrice = orderRepository.sumTotalPriceByMonthAndYear(currentMonth, currentYear);
        long lastMonthTotalPrice = orderRepository.sumTotalPriceByMonthAndYear(lastMonth, lastMonthYear);

        double quantityGrowth = lastMonthQuantity == 0 ? 100.0 :
                ((double) (thisMonthQuantity - lastMonthQuantity) / lastMonthQuantity) * 100;
        quantityGrowth = Math.round(quantityGrowth * 10.0) / 10.0;
        String growthQuantity = "" + quantityGrowth;
        if (quantityGrowth >0) {
            growthQuantity = "+"+ quantityGrowth;
        }
        double priceGrowth = lastMonthTotalPrice == 0 ? 100.0 :
                ((double) (thisMonthTotalPrice - lastMonthTotalPrice) / lastMonthTotalPrice) * 100;
        priceGrowth = Math.round(priceGrowth * 10.0) / 10.0;
        String growthPrice = "" + priceGrowth;
        if (priceGrowth >0) {
            growthPrice = "+"+ priceGrowth;
        }
        result.put("thisMonthQuantity", thisMonthQuantity);
        result.put("quantityGrowth", growthQuantity);

        result.put("thisMonthTotalPrice", thisMonthTotalPrice);
        result.put("priceGrowth", growthPrice);
        return result;
    }

    public List<DailyRevenueDTO> get7DayRevenue() {
        LocalDate startDate = LocalDate.now().minusDays(6);
        List<Object[]> results = orderRepository.findDailyRevenueSinceNative(startDate);

        Map<LocalDate, Long> revenueMap = results.stream()
                .collect(Collectors.toMap(
                        obj -> ((java.sql.Date) obj[0]).toLocalDate(),
                        obj -> ((Number) obj[1]).longValue()
                ));

        List<DailyRevenueDTO> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            Long revenue = revenueMap.getOrDefault(date, 0L);
            list.add(new DailyRevenueDTO(date, revenue));
        }
        return list;
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        List<OrderResponse> responseList = new ArrayList<>();

        for (Order order : orders) {
            List<OrderDetail> details = orderDetailRepository.findByOrder(order);

            List<OrderDetailResponse> items = details.stream()
                    .map(d -> new OrderDetailResponse(
                            d.getProduct().getId(),
                            d.getProduct().getImageUrl(),
                            d.getProduct().getName(),
                            d.getProductPrice(),
                            d.getQuantity()
                    ))
                    .collect(Collectors.toList());

            OrderResponse orderResponse = new OrderResponse(
                    order.getId(),
                    order.getUser().getId(),
                    order.getQuantity(),
                    order.getTotalPrice(),
                    order.getReceiveAddress(),
                    order.getReceiveName(),
                    order.getReceivePhone(),
                    order.getShipCost(),
                    order.getStatus(),
                    order.getPaymentMethod() != null ? order.getPaymentMethod().getId() : null,
                    order.getCreatedAt(),

                    order.getDoneAt(),
                    items

            );

            responseList.add(orderResponse);
        }
        return responseList;
    }
    public UserOrderSummaryResponse getUserOrderSummary(Long userId){
        long totalOrders = orderRepository.countOrdersByUserId(userId);
        Integer totalQuantity = orderRepository.sumQuantityByUserId(userId);
        Long totalAmount = orderRepository.sumTotalPriceByUserId(userId);

        if (totalQuantity == null) totalQuantity = 0;
        if (totalAmount == null) totalAmount = 0L;

        return new UserOrderSummaryResponse(totalOrders, totalQuantity, totalAmount);
    }
    public List<RevenueByMonth> getUserStatsByMonthYear(int month, int year) {
        List<Object[]> results = orderRepository.getUserPurchaseStats(month, year);
        List<RevenueByMonth> rvn = new ArrayList<>();

        for (Object[] row : results) {
            rvn.add(new RevenueByMonth(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    ((Number) row[2]).longValue(),
                    ((Number) row[3]).longValue(),
                    ((Number) row[4]).doubleValue()
            ));
        }

        return rvn;
    }
    public List<RevenueByMonth> getUserRevenueLast6Months() {
        List<Object[]> results = orderRepository.getRevenueLast6Months();
        List<RevenueByMonth> list = new ArrayList<>();

        for (Object[] row : results) {
            Long userId = ((Number) row[0]).longValue();
            String userName = (String) row[1];
            Long totalOrders = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            Long totalProducts = row[3] != null ? ((Number) row[3]).longValue() : 0L;
            Double totalRevenue = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;

            list.add(new RevenueByMonth(userId, userName, totalOrders, totalProducts, totalRevenue));
        }
        return list;
    }
    public List<RevenueByMonth> getUserRevenueCurentYear() {
        List<Object[]> results = orderRepository.getRevenueCurrentYear();
        List<RevenueByMonth> list = new ArrayList<>();

        for (Object[] row : results) {
            Long userId = ((Number) row[0]).longValue();
            String userName = (String) row[1];
            Long totalOrders = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            Long totalProducts = row[3] != null ? ((Number) row[3]).longValue() : 0L;
            Double totalRevenue = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;

            list.add(new RevenueByMonth(userId, userName, totalOrders, totalProducts, totalRevenue));
        }
        return list;
    }
    public List<ProductRevenueResponse> getProductRevenueByMonth(int month, int year) {
        return orderRepository.getProductRevenueByMonth(month, year)
                .stream().map(row -> new ProductRevenueResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).doubleValue()
                )).collect(Collectors.toList());
    }

    public List<ProductRevenueResponse> getProductRevenueLast6Months() {
        return orderRepository.getProductRevenueLast6Months()
                .stream().map(row -> new ProductRevenueResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).doubleValue()
                )).collect(Collectors.toList());
    }

    public List<ProductRevenueResponse> getProductRevenueCurrentYear() {
        return orderRepository.getProductRevenueCurrentYear()
                .stream().map(row -> new ProductRevenueResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).doubleValue()
                )).collect(Collectors.toList());
    }
    public boolean markOrderDone(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus("Hoàn thành");
            order.setDoneAt(LocalDateTime.now());
            orderRepository.save(order);
            return true;
        }
        return false;
    }

}
