package Rater.Services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
/*    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUser(UUID userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> createUser(UserCreateRequest userCreateRequest, Org org, PasswordEncoder passwordEncoder) {
        userCreateRequest.encode(passwordEncoder);
        User user = User.from(userCreateRequest, org);
        return Optional.ofNullable(userRepository.save(user));
    }

    public Optional<User> createUser(OrgUserCreateRequest orgUserCreateRequest, Org org, PasswordEncoder passwordEncoder) {
        orgUserCreateRequest.encode(passwordEncoder);
        User user = User.from(orgUserCreateRequest, org);
        return Optional.ofNullable(userRepository.save(user));
    }*/
}
