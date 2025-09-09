package com.jobportal.services;

import com.jobportal.entity.*;
import com.jobportal.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersService usersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------- addNew tests ----------------
    @Test
    void testAddNewRecruiter() {
        Users user = new Users();
        UsersType type = new UsersType();
        type.setUserTypeId(1); // 1 = Recruiter
        user.setUserTypeId(type);
        user.setPassword("plainPassword");

        Users savedUser = new Users();
        savedUser.setUserId(1);
        savedUser.setUserTypeId(type);

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(usersRepository.save(any())).thenReturn(savedUser);
        when(recruiterProfileRepository.save(any())).thenReturn(new RecruiterProfile());

        Users result = usersService.addNew(user);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        verify(recruiterProfileRepository).save(any());
        verify(jobSeekerProfileRepository, never()).save(any());
    }

    @Test
    void testAddNewJobSeeker() {
        Users user = new Users();
        UsersType type = new UsersType();
        type.setUserTypeId(2); // 2 = JobSeeker
        user.setUserTypeId(type);
        user.setPassword("plainPassword");

        Users savedUser = new Users();
        savedUser.setUserId(2);
        savedUser.setUserTypeId(type);

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(usersRepository.save(any())).thenReturn(savedUser);
        when(jobSeekerProfileRepository.save(any())).thenReturn(new JobSeekerProfile());

        Users result = usersService.addNew(user);

        assertNotNull(result);
        assertEquals(2, result.getUserId());
        verify(jobSeekerProfileRepository).save(any());
        verify(recruiterProfileRepository, never()).save(any());
    }

    // ---------------- getCurrentUser tests ----------------
    @Test
    void testGetCurrentUser() {
        Users user = new Users();
        user.setUserId(1);
        user.setEmail("test@example.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@example.com");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            Users result = usersService.getCurrentUser();
            assertEquals("test@example.com", result.getEmail());
        }
    }

    // ---------------- getCurrentUserProfile tests ----------------
    @Test
    void testGetCurrentUserProfileRecruiter() {
        Users user = new Users();
        user.setUserId(1);
        user.setEmail("recruiter@example.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("Recruiter"));
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("recruiter@example.com");
        

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        RecruiterProfile profile = new RecruiterProfile();
        profile.setUserAccountId(1);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(usersRepository.findByEmail("recruiter@example.com")).thenReturn(Optional.of(user));
            when(recruiterProfileRepository.findById(1)).thenReturn(Optional.of(profile));

            Object result = usersService.getCurrentUserProfile();
            assertTrue(result instanceof RecruiterProfile);
            assertEquals(1, ((RecruiterProfile) result).getUserAccountId());
        }
    }

    @Test
    void testGetCurrentUserProfileJobSeeker() {
        Users user = new Users();
        user.setUserId(2);
        user.setEmail("jobseeker@example.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("JobSeeker"));
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("jobseeker@example.com");
        

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setUserAccountId(2);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(usersRepository.findByEmail("jobseeker@example.com")).thenReturn(Optional.of(user));
            when(jobSeekerProfileRepository.findById(2)).thenReturn(Optional.of(profile));

            Object result = usersService.getCurrentUserProfile();
            assertTrue(result instanceof JobSeekerProfile);
            assertEquals(2, ((JobSeekerProfile) result).getUserAccountId());
        }
    }

    @Test
    void testGetCurrentUserProfileAnonymous() {
        Authentication auth = mock(AnonymousAuthenticationToken.class);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            Object result = usersService.getCurrentUserProfile();
            assertNull(result);
        }
    }
}
