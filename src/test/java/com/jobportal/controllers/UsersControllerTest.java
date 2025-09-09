package com.jobportal.controllers;

import com.jobportal.entity.Users;
import com.jobportal.entity.UsersType;
import com.jobportal.services.UsersService;
import com.jobportal.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersTypeService usersTypeService;

    @Mock
    private UsersService usersService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UsersController usersController;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password");
    }

    @Test
    void testRegister() {
        List<UsersType> mockTypes = List.of(
                new UsersType(1, "Recruiter", null),
                new UsersType(2, "Job Seeker", null)
        );

        when(usersTypeService.getAll()).thenReturn(mockTypes);

        String viewName = usersController.register(model);

        verify(model).addAttribute("getAllTypes", mockTypes);
        verify(model).addAttribute(eq("user"), any(Users.class));
        assertEquals("register", viewName);
    }

    @Test
    void testUserRegistration() {
        String viewName = usersController.userRegistration(mockUser, bindingResult, model);

        verify(usersService).addNew(mockUser);
        assertEquals("redirect:/dashboard/", viewName);
    }

    @Test
    void testLogin() {
        String viewName = usersController.login();

        assertEquals("login", viewName);
    }

    @Test
    void testLogout_WithAuthentication() {
        // Simulate an authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser.getEmail(), mockUser.getPassword())
        );

        String viewName = usersController.logout(request, response);

        assertEquals("redirect:/", viewName);
        // After logout, authentication should be cleared
        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testLogout_NoAuthentication() {
        SecurityContextHolder.clearContext(); // No authentication

        String viewName = usersController.logout(request, response);

        assertEquals("redirect:/", viewName);
    }
}
