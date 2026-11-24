package com.example.seckill.service;

import com.example.seckill.entity.User;
import com.example.seckill.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    /** 仅用于口令加密，避免数据库里出现明文密码 */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password) {
        User user = new User(username, encoder.encode(password));
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            return null;
        }
        User user = optional.get();
        if (!encoder.matches(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    public boolean exists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User toSafeUser(User user) {
        User safe = new User();
        safe.setId(user.getId());
        safe.setUsername(user.getUsername());
        safe.setPassword(null);
        return safe;
    }
}
