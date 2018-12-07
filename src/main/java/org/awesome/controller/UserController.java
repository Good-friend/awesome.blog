package org.awesome.controller;

import org.awesome.mapper.UserMapper;
import org.awesome.models.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class UserController {

    @Resource
    private UserMapper userMapper;

    @GetMapping("user")
    public List<User> getUsers() {
        return userMapper.selectList(null);
    }

    @GetMapping("user/{id}")
    public User getUser(@PathVariable("id") int id) {
        return userMapper.selectById(id);
    }

    @PostMapping("user")
    public void createUser(@RequestBody User user) {
        userMapper.insert(user);
    }

    @DeleteMapping("user/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        userMapper.deleteById(id);
    }

    @PutMapping("user")
    public void updateUser(@RequestBody User user) {
        userMapper.updateById(user);
    }
}
