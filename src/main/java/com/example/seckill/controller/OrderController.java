package com.example.seckill.controller;

import com.example.seckill.common.Result;
import com.example.seckill.entity.Product;
import com.example.seckill.entity.SeckillOrder;
import com.example.seckill.repository.OrderRepository;
import com.example.seckill.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单管理：参与秒杀下单、查看订单、取消订单。
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final String STATUS_CREATED = "已下单";
    private static final String STATUS_CANCELED = "已取消";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /** 单机演示用锁，保证库存不会被超卖 */
    private final Object stockLock = new Object();

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/seckill")
    public Result<SeckillOrder> seckill(@RequestBody Map<String, Object> body) {
        String username = body.get("username") == null ? "" : body.get("username").toString().trim();
        Long productId = parseId(body.get("productId"));
        if (username.isEmpty() || productId == null) {
            return Result.fail("用户名和商品不能为空");
        }

        synchronized (stockLock) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return Result.fail("商品不存在");
            }
            if (product.getStock() == null || product.getStock() <= 0) {
                return Result.fail("商品已售完");
            }
            product.setStock(product.getStock() - 1);
            productRepository.save(product);

            SeckillOrder order = new SeckillOrder();
            order.setUsername(username);
            order.setProductId(product.getId());
            order.setProductName(product.getName());
            order.setPrice(product.getSeckillPrice());
            order.setStatus(STATUS_CREATED);
            order.setCreateTime(LocalDateTime.now());
            return Result.ok(orderRepository.save(order));
        }
    }

    @GetMapping("/list")
    public Result<List<SeckillOrder>> list(@RequestParam String username) {
        return Result.ok(orderRepository.findByUsernameOrderByIdDesc(username.trim()));
    }

    @PostMapping("/cancel/{id}")
    public Result<SeckillOrder> cancel(@PathVariable Long id) {
        SeckillOrder order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if (!STATUS_CREATED.equals(order.getStatus())) {
            return Result.fail("该订单已取消");
        }
        order.setStatus(STATUS_CANCELED);
        orderRepository.save(order);

        productRepository.findById(order.getProductId()).ifPresent(product -> {
            product.setStock(product.getStock() + 1);
            productRepository.save(product);
        });
        return Result.ok(order);
    }

    private Long parseId(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
