package com.jobportal.controllers;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.entity.RecruiterJobsDto;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.entity.Users;
import com.jobportal.services.JobPostActivityService;
import com.jobportal.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobPostActivityController.class)
class JobPostActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private JobPostActivityService jobPostActivityService;

    @BeforeEach
    void setupSecurity() {
        var auth = new UsernamePasswordAuthenticationToken(
                "testUser",
                "password",
                Collections.emptyList() // default no roles
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testSearchJobsForNormalUser() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(1);


        when(usersService.getCurrentUserProfile()).thenReturn(mockUser);

        mockMvc.perform(get("/dashboard/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("username", "testUser"));
    }

    @Test
    void testSearchJobsForRecruiter() throws Exception {
        RecruiterProfile recruiterProfile = new RecruiterProfile();
        recruiterProfile.setUserAccountId(100);

        // Security with "Recruiter" role
        var auth = new UsernamePasswordAuthenticationToken(
                "recruiterUser",
                "password",
                List.of(new SimpleGrantedAuthority("Recruiter"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(usersService.getCurrentUserProfile()).thenReturn(recruiterProfile);
        when(jobPostActivityService.getRecruiterJobs(100))
                .thenReturn(List.of(new RecruiterJobsDto(5L, 1, "Java Developer", null, null)));

        mockMvc.perform(get("/dashboard/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("jobPost"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("username", "recruiterUser"));
    }

    @Test
    void testAddJobsPage() throws Exception {
        when(usersService.getCurrentUserProfile()).thenReturn(new Users());

        mockMvc.perform(get("/dashboard/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-jobs"))
                .andExpect(model().attributeExists("jobPostActivity"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testAddNewJobPost() throws Exception {
        Users user = new Users();
        user.setUserId(1);


        when(usersService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(post("/dashboard/addNew")
                        .flashAttr("jobPostActivity", new JobPostActivity()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/"));

        Mockito.verify(jobPostActivityService).addNew(any(JobPostActivity.class));
    }

    @Test
    void testEditJob() throws Exception {
        JobPostActivity job = new JobPostActivity();
        job.setJobPostId(10);
        job.setPostedDate(new Date());

        when(jobPostActivityService.getOne(10)).thenReturn(job);
        when(usersService.getCurrentUserProfile()).thenReturn(new Users());

        mockMvc.perform(get("/dashboard/edit/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-jobs"))
                .andExpect(model().attributeExists("jobPostActivity"))
                .andExpect(model().attributeExists("user"));
    }
}
