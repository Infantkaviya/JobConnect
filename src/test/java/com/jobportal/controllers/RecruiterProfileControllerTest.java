package com.jobportal.controllers;

import com.jobportal.entity.RecruiterProfile;
import com.jobportal.entity.Users;
import com.jobportal.repository.UsersRepository;
import com.jobportal.services.RecruiterProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruiterProfileControllerTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RecruiterProfileService recruiterProfileService;

    @Mock
    private Model model;

    @InjectMocks
    private RecruiterProfileController recruiterProfileController;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        // mock authenticated user
        mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setEmail("test@example.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser.getEmail(), "password")
        );
    }

    @Test
    void testRecruiterProfile_WhenProfileExists() {
        RecruiterProfile profile = new RecruiterProfile();
        profile.setUserAccountId(mockUser.getUserId());

        when(usersRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(recruiterProfileService.getOne(mockUser.getUserId())).thenReturn(Optional.of(profile));

        String viewName = recruiterProfileController.recruiterProfile(model);

        verify(model).addAttribute("profile", profile);
        assertEquals("recruiter_profile", viewName);
    }

    @Test
    void testRecruiterProfile_WhenNoProfileExists() {
        when(usersRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(recruiterProfileService.getOne(mockUser.getUserId())).thenReturn(Optional.empty());

        String viewName = recruiterProfileController.recruiterProfile(model);

        verify(model, never()).addAttribute(eq("profile"), any());
        assertEquals("recruiter_profile", viewName);
    }

    @Test
    void testAddNewRecruiterProfile() throws Exception {
        RecruiterProfile profile = new RecruiterProfile();
        MockMultipartFile multipartFile =
                new MockMultipartFile("image", "profile.jpg", "image/jpeg", "dummy".getBytes());

        when(usersRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(recruiterProfileService.addNew(any(RecruiterProfile.class))).thenAnswer(invocation -> {
            RecruiterProfile saved = invocation.getArgument(0);
            saved.setUserAccountId(mockUser.getUserId());
            return saved;
        });

        String viewName = recruiterProfileController.addNew(profile, multipartFile, model);

        verify(model).addAttribute("profile", profile);
        verify(recruiterProfileService).addNew(any(RecruiterProfile.class));
        assertEquals("redirect:/dashboard/", viewName);
    }
}
