package com.jobportal.services;

import com.jobportal.entity.Users;
import com.jobportal.entity.UsersType;
import com.jobportal.repository.UsersRepository;
import com.jobportal.util.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UsersType usersType = new UsersType();
        usersType.setUserTypeId(1);
        usersType.setUserTypeName("Recruiter");

        mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");
        mockUser.setActive(true);
        mockUser.setRegistrationDate(new Date());
        mockUser.setUserTypeId(usersType);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails instanceof CustomUserDetails);

        verify(usersRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(usersRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("notfound@example.com"));

        verify(usersRepository, times(1)).findByEmail("notfound@example.com");
    }
}
