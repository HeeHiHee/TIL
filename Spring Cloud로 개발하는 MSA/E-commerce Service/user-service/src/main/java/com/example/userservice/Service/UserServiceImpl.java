package com.example.userservice.Service;

import com.example.userservice.Dto.UserDto;
import com.example.userservice.Entity.UserEntity;
import com.example.userservice.Entity.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import com.example.userservice.vo.ResponseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;
    Environment env;
    RestTemplate restTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           Environment env,
                           RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseUser createuser(UserDto userDto) {

        UserEntity userEntity = UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .encryptedPwd(passwordEncoder.encode(userDto.getPwd()))
                .build();
        userRepository.save(userEntity);

        ResponseUser responseUser = ResponseUser.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .userId(userEntity.getUserId())
                .build();

        return responseUser;
     }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity==null)
            throw new UsernameNotFoundException("User not found");

//        List<ResponseOrder> ordersList = new ArrayList<>();
        // restTemplate을 사용한 첫 번째 방법
        String orderUrl = String.format("http://localhost:8080/api/order-service/%s/orders", userId);
        ResponseEntity<List<ResponseOrder>> orderListResponse =
                restTemplate.exchange(orderUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<ResponseOrder>>() {
                });
        List<ResponseOrder> ordersList = orderListResponse.getBody();

        UserDto userDto = UserDto.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .userId(userEntity.getUserId())
                .encryptedPwd(userEntity.getEncryptedPwd())
                .orders(ordersList)
                .build();

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if(userEntity==null)
            throw new UsernameNotFoundException(username);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>()); // ArrayList에는 로그인 후 권한을 추가할 예정
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        UserDto userDto = UserDto.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .pwd(userEntity.getEncryptedPwd())
                .userId(userEntity.getUserId())
                .build();

        return userDto;
    }
}
