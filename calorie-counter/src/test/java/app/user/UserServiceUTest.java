package app.user;

import app.exception.DomainException;
import app.exception.EditUserEmailAlreadyRegisteredException;
import app.exception.EmailAlreadyRegisteredException;
import app.exception.UsernameAlreadyExistsException;
import app.security.AuthenticationDetails;
import app.user.model.Country;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import app.user.model.User;
import app.user.model.UserRole;
import org.springframework.security.core.userdetails.UserDetails;
import app.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void changeRole_givenUserWithRoleAdmin_whenChangeRole_thenUserReceivesUserRole() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .userRole(UserRole.ADMIN)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.changeRole(userId);
        assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void changeRole_givenUserWithRoleUser_whenChangeRole_thenUserReceivesAdminRole() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .userRole(UserRole.USER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.changeRole(userId);
        assertThat(user.getUserRole()).isEqualTo(UserRole.ADMIN);
    }


    @Test
    void editUser_givenMissingUserFromDatabase_whenEditUser_thenExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        UserEditRequest dto = UserEditRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> userService.editUser(userId, dto));
    }

    @Test
    void editUser_givenExistingUser_whenEditTheirProfileWithAlreadyExistingEmail_thenExceptionIsThrown() {
        UUID optionalUserId = UUID.randomUUID();
        User optionalUser = User.builder()
                .id(optionalUserId)
                .username("simona")
                .email("simona@abv.bg")
                .build();
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("nikola")
                .build();
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .email("simona@abv.bg")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(userEditRequest.getEmail())).thenReturn(Optional.of(optionalUser));
        assertThrows(EditUserEmailAlreadyRegisteredException.class, () -> userService.editUser(userId, userEditRequest));
        verify(userRepository, never()).save(user);
    }

    @Test
    public void editUser_givenExistingUser_whenEditTheirProfileWithNotAlreadyExistingEmail_thenSaveToRepository() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("nikola")
                .build();
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .email("simona@abv.bg")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(userEditRequest.getEmail())).thenReturn(Optional.empty());
        userService.editUser(userId, userEditRequest);
        assertEquals(userEditRequest.getEmail(), user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenMissingUserFromDatabase_whenLoadUserByUsername_thenExceptionIsThrown() {
        String username = "simona";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));
    }


    @Test
    void givenExistingUser_whenLoadUserByUsername_thenReturnCorrectAuthenticationDetails() {
        String username = "simona";
        User user = User.builder()
                .id(UUID.randomUUID())
                .isActive(true)
                .password("123456")
                .userRole(UserRole.ADMIN)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UserDetails authenticationDetails = userService.loadUserByUsername(username);
        assertInstanceOf(AuthenticationDetails.class, authenticationDetails);
        AuthenticationDetails result = (AuthenticationDetails) authenticationDetails;
        assertEquals(user.getId(), result.getUserId());
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getUserRole(), result.getRole());
        assertEquals(user.isActive(), result.isActive());
        assertThat(result.getAuthorities()).hasSize(1);
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void register_givenExistingUsername_whenRegister_thenExceptionIsThrown() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("simona")
                .password("123456")
                .country(Country.BULGARIA)
                .build();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_givenAlreadyExistingEmail_whenRegister_thenExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("nikola")
                .build();
        RegisterRequest dto = RegisterRequest.builder()
                .email("simona@abv.bg")
                .build();
        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        assertThrows(EmailAlreadyRegisteredException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }


    @Test
    void register_whenUsernameAndEmailAreUnique_thenSaveUserToRepository() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("simona")
                .password("123123")
                .country(Country.BULGARIA)
                .email("simona@abv.com")
                .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .build();
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        User registeredUser = userService.register(registerRequest);
        assertEquals(user.getUsername(), registeredUser.getUsername());
        assertEquals(user.getEmail(), registeredUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void initializeUser_whenGivenRegisterRequest_thenReturnUser() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("simona")
                .password("123123")
                .country(Country.BULGARIA)
                .email("simona@abv.bg")
                .build();
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        User user = userService.initializeUser(registerRequest);
        assertEquals(registerRequest.getUsername(), user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(UserRole.USER, user.getUserRole());
        assertTrue(user.isActive());
        assertEquals(registerRequest.getCountry(), user.getCountry());
        assertEquals(registerRequest.getEmail(), user.getEmail());
        assertNotNull(user.getCreatedOn());
        assertNotNull(user.getUpdatedOn());
    }


    @Test
    public void updateWeight_whenExistingUser_ThenSaveToRepository() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .weight(65.00)
                .build();
        double updatedWeight = 60.00;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.updateWeight(userId, updatedWeight);
        assertEquals(updatedWeight, user.getWeight());
        assertNotNull(user.getUpdatedOn());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changeStatus_whenChangeStatusActive_thenUserReceivesInactiveStatus() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .isActive(true)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.changeStatus(userId);
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void changeStatus_whenChangeStatusInactive_thenUserReceivesActiveStatus() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .isActive(false)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.changeStatus(userId);
        assertTrue(user.isActive());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    public void getAllUsers_whenListNotEmpty_thenReturnListOfAllUsers() {
        User userOne = User.builder()
                .id(UUID.randomUUID())
                .build();
        User userTwo = User.builder()
                .id(UUID.randomUUID())
                .build();
        List<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertThat(users).hasSize(2);
        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getAllUsers_whenNoUsersFound_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());
        List<User> result = userService.getAllUsers();
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getByUsername_whenUserExists_thenReturnUser() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("simona")
                .build();
        when(userRepository.findByUsername("simona")).thenReturn(Optional.of(user));
        User result = userService.getByUsername("simona");
        assertEquals(user, result);
        verify(userRepository, times(1)).findByUsername("simona");
    }

    @Test
    public void getByUsername_whenUserDoesNotExist_thenException() {
      String username = "simona";
       when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
       assertThrows(DomainException.class, () -> userService.getByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    public void getAllActiveUsers_whenActiveUsersFound_thenReturnListOfActiveUsers() {
        User userOne = User.builder()
                .id(UUID.randomUUID())
                .isActive(true)
                .build();
        User userTwo = User.builder()
                .id(UUID.randomUUID())
                .isActive(true)
                .build();
        List<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        when(userRepository.findAll()).thenReturn(users);
        List<User> activeUsers = userService.getAllActiveUsers();
        assertEquals(2, activeUsers.size());
        assertTrue(activeUsers.stream().allMatch(User::isActive));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getAllActiveUsers_whenActiveUsersNotFound_thenReturnEmptyList() {
        User userOne = User.builder()
                .id(UUID.randomUUID())
                .isActive(false)
                .build();
        User userTwo = User.builder()
                .id(UUID.randomUUID())
                .isActive(false)
                .build();
        List<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);
        when(userRepository.findAll()).thenReturn(users);
        List<User> activeUsers = userService.getAllActiveUsers();
        assertTrue(activeUsers.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

}