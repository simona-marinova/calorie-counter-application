package app.user.service;


import app.exception.DomainException;
import app.security.AuthenticationDetails;
import app.user.model.*;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());
        if (userOptional.isPresent()) {
            throw new DomainException("Username [%s] already exists".formatted(registerRequest.getUsername()));
        }

        User user = initializeUser(registerRequest);
        userRepository.save(user);

        log.info("Successfully created new user account for username [%s] with id [%s]"
                .formatted(user.getUsername(), user.getId()));

        return user;
    }


    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .userRole(UserRole.USER)
                .isActive(true)
                .country(registerRequest.getCountry())
                .email(registerRequest.getEmail())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public void editUser(UUID userId, UserEditRequest userEditRequest) {
        User user = getById(userId);
        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());
        user.setHeight(userEditRequest.getHeight());
        user.setWeight(userEditRequest.getWeight());
        user.setAge(userEditRequest.getAge());
        user.setActivityLevel(userEditRequest.getActivityLevel());
        user.setCountry(userEditRequest.getCountry());
        user.setWeightGoal(userEditRequest.getWeightGoal());
        userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("User with this username does not exist."));

        return new AuthenticationDetails(user.getId(), username, user.getPassword(), user.getUserRole(), user.isActive());
    }

    public void updateUserWeight(UUID userId, double weight) {
        User user = getById(userId);
        user.setWeight(weight);
        userRepository.save(user);

    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] does not exist.".formatted(id)));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public void changeRole(UUID userId) {
        User user = getById(userId);
        if (user.getUserRole() == UserRole.USER) {
            user.setUserRole(UserRole.ADMIN);
        } else {
            user.setUserRole(UserRole.USER);
        }
        userRepository.save(user);
    }

    public void changeStatus(UUID userId) {
        User user = getById(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findAll().stream().filter(User::isActive).toList();
    }
}





