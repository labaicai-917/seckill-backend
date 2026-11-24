package com.example.seckill.config;

import com.example.seckill.entity.Product;
import com.example.seckill.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 首次启动时写入几条演示商品，方便直接验证前端。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.saveAll(Arrays.asList(
                product("华为 Mate 60 Pro", "旗舰手机，秒杀限量 100 台", "6999.00", "5999.00", 100),
                product("小米 14", "小屏旗舰，秒杀限量 50 台", "3999.00", "3499.00", 50),
                product("机械键盘 K8", "热插拔轴体，秒杀限量 200 件", "499.00", "299.00", 200)
        ));
    }

    private Product product(String name, String description, String price, String seckillPrice, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setSeckillPrice(new BigDecimal(seckillPrice));
        product.setStock(stock);
        product.setCreateTime(LocalDateTime.now());
        return product;
    }
}
