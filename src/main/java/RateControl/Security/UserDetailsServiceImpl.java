package RateControl.Security;

import RateControl.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<RateControl.Models.User.User> user = userService.getUserByEmail(email);
        return User
                .builder()
                .username(user.map(u -> u.getEmail()).orElseThrow())
                .password(user.map(u -> u.getPassword()).orElseThrow())
                .roles("")
                .build();
    }
}
