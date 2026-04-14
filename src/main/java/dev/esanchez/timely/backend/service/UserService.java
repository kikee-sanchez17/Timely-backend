package dev.esanchez.timely.backend.service;

import dev.esanchez.timely.backend.dto.request.CreateUserRequest;
import dev.esanchez.timely.backend.entity.User;
import dev.esanchez.timely.backend.exception.EmailAlreadyExistsException;
import dev.esanchez.timely.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    //List all the users in the db
    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

}
