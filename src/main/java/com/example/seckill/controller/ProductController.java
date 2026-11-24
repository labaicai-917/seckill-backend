package com.example.seckill.controller;

import com.example.seckill.common.Result;
import com.example.seckill.entity.Product;
import com.example.seckill.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品管理：列表、详情，以及管理员创建秒杀活动。
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/list")
    public Result<List<Product>> list() {
        return Result.ok(productRepository.findAllByOrderByIdAsc());
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(Result::ok)
                .orElseGet(() -> Result.fail("商品不存在"));
    }

    /**
     * 管理员创建秒杀活动（一个活动对应一个秒杀商品）。
     */
    @PostMapping("/create")
    public Result<Product> create(@RequestBody Product form) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            return Result.fail("商品名称不能为空");
        }
        if (form.getPrice() == null || form.getSeckillPrice() == null) {
            return Result.fail("原价和秒杀价不能为空");
        }
        if (form.getStock() == null || form.getStock() <= 0) {
            return Result.fail("库存必须大于 0");
        }
        Product product = new Product();
        product.setName(form.getName().trim());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setSeckillPrice(form.getSeckillPrice());
        product.setStock(form.getStock());
        product.setCreateTime(LocalDateTime.now());
        return Result.ok(productRepository.save(product));
    }
}
