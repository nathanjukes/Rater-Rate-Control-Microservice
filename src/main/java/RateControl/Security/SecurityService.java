package RateControl.Security;

import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.Org.Org;
import RateControl.Models.User.User;
import RateControl.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SecurityService {
    private final UserService userService;

    @Autowired
    public SecurityService(UserService userService) {
        this.userService = userService;
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

    public Optional<String> getAuthToken(final HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        if (header != null) {
            var parts = header.split(" ");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
                return Optional.ofNullable(parts[1]);
            }
        }
        return null;
    }
}
