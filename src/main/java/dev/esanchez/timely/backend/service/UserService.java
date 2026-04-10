package dev.esanchez.timely.backend.service;

import dev.esanchez.timely.backend.dto.request.CreateUserRequest;
import dev.esanchez.timely.backend.entity.User;
import dev.esanchez.timely.backend.exception.EmailAlreadyExistsException;
import dev.esanchez.timely.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserRequest request) {

        //Verify if the email already exists
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        
        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getName(),request.getSurname(),request.getPhoneNumber());

        return userRepository.save(user);
    }

}
