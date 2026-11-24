package com.example.seckill.repository;

import com.example.seckill.entity.SeckillOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<SeckillOrder, Long> {

    List<SeckillOrder> findByUsernameOrderByIdDesc(String username);
}
