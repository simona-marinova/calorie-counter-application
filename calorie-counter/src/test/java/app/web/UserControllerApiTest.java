package app.web;

import app.security.AuthenticationDetails;
import app.user.model.*;
import app.user.service.UserService;
import app.web.dto.UserEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static app.TestBuilder.aRandomUser;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

@MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void putUnauthorizedRequestToChangeRole_shouldReturn404AndNotFoundView() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "simona", "123123", UserRole.USER, true);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }


    @Test
    void putAuthorizedRequestToChangeRole_shouldRedirectToUsers() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "simona", "123123", UserRole.ADMIN, true);
        MockHttpServletRequestBuilder request = put("/users/{id}/role", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        verify(userService, times(1)).changeRole(any());
    }


    @Test
    void putUnauthorizedRequestToChangeStatus_shouldReturn404AndNotFoundView() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "simona", "123123", UserRole.USER, true);
        MockHttpServletRequestBuilder request = put("/users/{id}/status", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }


    @Test
    void putAuthorizedRequestToChangeStatus_shouldRedirectToUsers() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "simona", "123123", UserRole.ADMIN, true);
        MockHttpServletRequestBuilder request = put("/users/{id}/status", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        verify(userService, times(1)).changeStatus(any());
    }

    @Test
    void putUnauthorizedRequestToViewAllUsers_shouldReturn404AndNotFoundView() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "simona", "123123", UserRole.USER, true);
        MockHttpServletRequestBuilder request = get("/users", UUID.randomUUID())
                .with(user(principal))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void putAuthorizedRequestToViewAllUsers_shouldRedirectToUsers() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "simona", "123123", UserRole.ADMIN, true);
        MockHttpServletRequestBuilder request = get("/users", UUID.randomUUID())
                .with(user(principal));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("users"));
    }

    @Test
    void getAuthenticatedRequestToEditProfile_returnsEditProfileView() throws Exception {
        User user = aRandomUser();
        when(userService.getById(any())).thenReturn(user);
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .email("simona.com")
                .firstName("Simona")
                .lastName("Marinova")
                .email("simona@abv.bg")
                .height(165.0)
                .weight(55.0)
                .age(36)
                .activityLevel(ActivityLevel.MODERATELY_ACTIVE)
                .country(Country.BULGARIA)
                .weightGoal(WeightGoal.WEIGHT_LOSS)
                .build();
        when(userService.getById(user.getId())).thenReturn(user);
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "simona", "123123", UserRole.ADMIN, true);
        MockHttpServletRequestBuilder request = get("/users/{id}/profile", user.getId())
                .with(user(principal));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("user"))
            .andExpect(model().attributeExists("userEditRequest"));
        verify(userService, times(1)).getById(user.getId());
    }




}


