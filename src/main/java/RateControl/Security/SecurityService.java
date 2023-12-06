package RateControl.Security;

import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Auth.TokenResponse;
import RateControl.Models.Org.Org;
import RateControl.Models.User.User;
import RateControl.Services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class SecurityService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public SecurityService(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public Auth getServiceAccountAuth(String serviceId) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(serviceId, serviceId)
        );
        TokenResponse jwt = jwtUtil.generateTokenResponse(auth);

        return new Auth(jwt.getAccessToken());
    }

    public boolean hasOrg(String orgName) throws InternalServerException, UnauthorizedException {
        Optional<User> user = getAuthedUser();

        return user.map(u -> u.getOrg().getName())
                .filter(n -> n.equals(orgName))
                .isPresent();
    }

    public boolean hasOrg(UUID orgId) throws InternalServerException, UnauthorizedException {
        Optional<Org> org = getAuthedOrg();

        return org.map(o -> o.getId())
                .filter(o -> o.equals(orgId))
                .isPresent();
    }

    public Optional<User> getAuthedUser() throws InternalServerException, UnauthorizedException {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            return userService.getUserByEmail(userDetails.getUsername());
        } catch (ClassCastException ex) {
            // When token not provided or token expired
            throw new UnauthorizedException();
        } catch (Exception ex) {
            // Only expecting true 500s here
            throw new InternalServerException();
        }
    }

    public Optional<String> getToken() {
        String userDetails = SecurityContextHolder.getContext().getAuthentication().toString();
        return Optional.ofNullable(userDetails);
    }

    public Optional<Org> getAuthedOrg() throws InternalServerException, UnauthorizedException {
        return Optional.of(getAuthedUser().map(u -> u.getOrg()).orElseThrow());
    }

    public static void throwIfNoAuth(Optional<Org> org) throws UnauthorizedException {
        if (org.isEmpty()) {
            throw new UnauthorizedException();
        }
    }

    public Optional<Auth> getAuthToken(final HttpServletRequest request) {
        // Read auth header and parse into Auth object
        // Authorization = "Bearer {token}"
        var header = request.getHeader("Authorization");
        if (header != null) {
            var parts = header.split(" ");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
                String jwt = parts[1];
                return Optional.of(new Auth(jwt));
            }
        }
        return Optional.empty();
    }
}
