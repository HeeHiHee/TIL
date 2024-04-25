package com.example.userservice.Service;

import com.example.userservice.Dto.UserDto;
import com.example.userservice.Entity.UserEntity;
import com.example.userservice.vo.ResponseUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    ResponseUser createuser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
    UserDto getUserDetailsByEmail(String userName);
}
