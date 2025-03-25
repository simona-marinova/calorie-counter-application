package app.web;

import app.dailyStatistics.service.DailyStatisticsService;
import app.exception.UsernameAlreadyExistsException;
import app.meal.service.MealService;
import app.myRecipe.service.MyRecipeService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import java.time.LocalDate;
import java.util.UUID;

import static app.TestBuilder.aRandomDailyStatistics;
import static app.TestBuilder.aRandomUser;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

   //MockitoBean - doesn't allow me to use it
    @MockBean
    private UserService userService;
   @MockBean
    private  MealService mealService;
    @MockBean
    private  MyRecipeService myRecipeService;
    @MockBean
    private  DailyStatisticsService dailyStatisticsService;


    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {
        MockHttpServletRequestBuilder request = get("/");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {
        MockHttpServletRequestBuilder request = get("/register");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }


    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginView() throws Exception {
        MockHttpServletRequestBuilder request = get("/login");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"));
    }

    @Test
    void getRequestToLoginEndpointWithErrorParam_shouldReturnLoginViewAndErrorMessageAttribute() throws Exception {
        MockHttpServletRequestBuilder request = get("/login").param("error","");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest", "errorMessage"));
    }

    @Test
    void postRequestToRegisterEndpoint_happyPath() throws Exception {
        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "Simona")
                .formField("password", "123456")
                .formField("country", "BULGARIA")
                .formField("email", "simona@abv.bg")
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(userService, times(1)).register(any());

    }

        @Test
        void postRequestToRegisterEndpointWithInvalidData_returnRegisterView() throws Exception {
            MockHttpServletRequestBuilder request = post("/register")
                    .formField("username", "")
                    .formField("password", "")
                    .formField("country", "BULGARIA")
                    .formField("email", "simona@abv.bg")
                    .with(csrf());
            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(view().name("register"));
            verify(userService, never()).register(any());
        }

    @Test
    void getUnauthenticatedRequestToHome_redirectToLogin() throws Exception {
        MockHttpServletRequestBuilder request = get("/home");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
        verify(userService, never()).getById(any());
    }

    @Test
    void getAuthenticatedRequestToHome_returnsHomeView() throws Exception {
        User user = aRandomUser();
        when(userService.getById(any())).thenReturn(user);
        when(dailyStatisticsService.createDailyStatistics(any())).thenReturn(aRandomDailyStatistics());

        UUID userId = user.getId();
        AuthenticationDetails principal = new AuthenticationDetails(userId, "user123", "123123", UserRole.USER, true);
        MockHttpServletRequestBuilder request = get("/home")
                .with(user(principal));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("dailyStatistics"));
        verify(userService, times(1)).getById(userId);
        verify(mealService, times(1)).createMealsForTheDay(userId);
        verify(mealService, times(1)).getMealsByUserIdAndDate(userId, LocalDate.now());
        verify(dailyStatisticsService, times(1)).createDailyStatistics(userId);

    }

    @Test
    void getRequestToHowToUseTheAppEndpoint_shouldReturnHowToUseTheAppView() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "user123", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/how-to-use").with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("how-to-use-the-app"));
    }

    @Test
    void getRequestToGetTipsPageEndpoint_shouldReturnGetTipsPageView() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "user123", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/tips").with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("tips"));
    }

    @Test
    void getRequestToGetCaloriesInFoodsPageEndpoint_shouldReturnCaloriesInFoodsPageView() throws Exception {
        AuthenticationDetails principal = new AuthenticationDetails(UUID.randomUUID(), "user123", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/calories-in-foods").with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("calories-in-foods"));
    }


    @Test
    void postRequestToRegisterEndpointWhenUsernameAlreadyExist_thenRedirectToRegisterWithFlashParameter() throws Exception {
        when(userService.register(any())).thenThrow(new UsernameAlreadyExistsException("Username already exist!"));
        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "Simona")
                .formField("password", "123456")
                .formField("country", "BULGARIA")
                .formField("email", "simona@abv.bg")
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("usernameAlreadyExistsMessage"));
        verify(userService, times(1)).register(any());
    }

    }

