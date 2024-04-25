package com.example.userservice.Controller;

import com.example.userservice.Dto.UserDto;
import com.example.userservice.Entity.UserEntity;
import com.example.userservice.Service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user-service")
public class UserController {
    UserService userService;
    Environment env;
    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status() {
        // 유저 서비스의 포트번호 출력으로 잘 작동하는지 확인
        return String.format("It's Working in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", token secret=" + env.getProperty("token.secret")
                + ", token expiration time=" + env.getProperty("token.expiration_time"));
//        return String.format("It's Working in User service on PORT %s",
//                env.getProperty("local.server.port"));

    }

    @PostMapping("/users") // 회원가입
    public ResponseEntity<Object> createUser(@RequestBody RequestUser user) {
        try {
            UserDto userDto = UserDto.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .pwd(user.getPwd())
                    .build();
            ResponseUser createdUser = userService.createuser(userDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            // 오류가 발생한 경우 400 Bad Request 오류와 함께 오류 메시지 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/users") // 전체유저조회
    public ResponseEntity<Object> getUsers() {
        try {
            Iterable<UserEntity> userList = userService.getUserByAll();

            List<ResponseUser> result = new ArrayList<>();
            userList.forEach(v -> {
                ResponseUser responseUser = ResponseUser.builder()
                        .email(v.getEmail())
                        .name(v.getName())
                        .userId(v.getUserId())
                        // orders 추가
                        .build();
                result.add(responseUser);
            });

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            // 오류가 발생한 경우 400 Bad Request 오류와 함께 오류 메시지 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/users/{userId}") // 유저상세보기 + 주문목록보기
    public ResponseEntity<Object> gerUser(@PathVariable("userId") String userId) {
        try {
            UserDto userDto = userService.getUserByUserId(userId);
            ResponseUser responseUser = ResponseUser.builder()
                    .email(userDto.getEmail())
                    .name(userDto.getName())
                    .userId(userDto.getUserId())
                    .orders(userDto.getOrders())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(responseUser);

        } catch (Exception e) {
            // 오류가 발생한 경우 400 Bad Request 오류와 함께 오류 메시지 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginuser(@RequestBody RequestLogin requestLogin,
                                       HttpServletResponse httpServletResponse){

        return ResponseEntity.status(HttpStatus.OK).body(requestLogin);
    }

}
