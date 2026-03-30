package dev.esanchez.timely.backend.controller;

import dev.esanchez.timely.backend.dto.request.CreateUserRequest;
import dev.esanchez.timely.backend.dto.response.UserResponse;
import dev.esanchez.timely.backend.entity.User;
import dev.esanchez.timely.backend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createBooking(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return new UserResponse(user.getUserId(), user.getEmail());
    }

}
