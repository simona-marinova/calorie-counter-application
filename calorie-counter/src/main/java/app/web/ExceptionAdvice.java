package app.web;


import app.exception.EditEmailAlreadyRegisteredException;
import app.exception.EmailAlreadyRegisteredException;
import app.exception.UsernameAlreadyExistsException;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;


@ControllerAdvice
public class ExceptionAdvice {

    private final UserService userService;

    public ExceptionAdvice(UserService userService) {
        this.userService = userService;
    }


    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public String handleUsernameAlreadyExists(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("usernameAlreadyExistsMessage", "This username is already in use");

        return "redirect:/register";
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public String handleEmailAlreadyRegistered(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("emailAlreadyRegistered", "This email is already registered");

        return "redirect:/register";
    }

    @ExceptionHandler(EditEmailAlreadyRegisteredException.class)
    public String handleEditEmailAlreadyRegistered(RedirectAttributes redirectAttributes, @PathVariable UUID id) {


        redirectAttributes.addFlashAttribute("editEmailAlreadyRegistered", "This email is already registered");

        return "redirect:/users/{id}/profile";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class

    })
    public ModelAndView handleNotFoundExceptions() {
        return new ModelAndView("not-found");
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handeAnyException(Exception exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());
        return modelAndView;
    }
}
