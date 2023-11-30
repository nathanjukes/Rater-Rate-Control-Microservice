package RateControlTests.Services;

import RateControl.Repositories.UserRepository;
import RateControl.Services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testGetByEmail() {
        String email = "testEmail";
        userService.getUserByEmail(email);

        verify(userRepository, times(1)).findByEmail(eq(email));
    }
}
