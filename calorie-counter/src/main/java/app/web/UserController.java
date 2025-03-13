package app.web;

import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.UserEditRequest;
import app.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}/profile")
    public ModelAndView editProfileMenu(@PathVariable UUID id) {
        User user = userService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userEditRequest", DtoMapper.mapUserToUserEditRequest(user));
        return modelAndView;
    }


    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(@PathVariable UUID id, @Valid UserEditRequest userEditRequest, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            User user = userService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("userEditRequest", userEditRequest);
            return modelAndView;
        }
        userService.editUser(id, userEditRequest);
        return new ModelAndView("redirect:/home");

    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView viewAllUsers(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        List<User> users = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @PutMapping("/{id}/role")
    public String changeUserRole(@PathVariable UUID id) {

        userService.changeRole(id);

        return "redirect:/users";
    }



    @PutMapping("/{id}/status")
    public String changeUserStatus(@PathVariable UUID id) {

        userService.changeStatus(id);

        return "redirect:/users";
    }


}












