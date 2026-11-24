package com.example.seckill.controller;

import com.example.seckill.common.Result;
import com.example.seckill.entity.User;
import com.example.seckill.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户管理：注册、登录、查看个人信息。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> body) {
        String username = trim(body.get("username"));
        String password = trim(body.get("password"));
        if (username.isEmpty() || password.isEmpty()) {
            return Result.fail("用户名和密码不能为空");
        }
        if (userService.exists(username)) {
            return Result.fail("用户名已存在");
        }
        User user = userService.register(username, password);
        return Result.ok(userService.toSafeUser(user));
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> body) {
        String username = trim(body.get("username"));
        String password = trim(body.get("password"));
        if (username.isEmpty() || password.isEmpty()) {
            return Result.fail("用户名和密码不能为空");
        }
        User user = userService.login(username, password);
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }
        return Result.ok(userService.toSafeUser(user));
    }

    /**
     * 指导书 3.3.1 示例接口：获取用户个人信息。
     */
    @GetMapping("/profile")
    public Result<User> getUserProfile(@RequestParam String username) {
        User user = userService.getUserByUsername(trim(username));
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(userService.toSafeUser(user));
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
