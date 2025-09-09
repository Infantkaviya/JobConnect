package com.jobportal.controllers;

import com.jobportal.entity.JobPostActivity;
import com.jobportal.services.JobPostActivityService;
import com.jobportal.services.UsersService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobSeekerApplyController.class)
class JobSeekerApplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobPostActivityService jobPostActivityService;

    @MockBean
    private UsersService usersService;

    @Test
    void testDisplayJobDetails() throws Exception {
        JobPostActivity mockJob = new JobPostActivity();
        mockJob.setJobPostId(101);
        mockJob.setJobTitle("Java Developer");

        when(jobPostActivityService.getOne(101)).thenReturn(mockJob);
        when(usersService.getCurrentUserProfile()).thenReturn("MockUserProfile");

        mockMvc.perform(get("/job-details-apply/101"))
                .andExpect(status().isOk())
                .andExpect(view().name("job-details"))
                .andExpect(model().attributeExists("jobDetails"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("jobDetails", mockJob))
                .andExpect(model().attribute("user", "MockUserProfile"));

        Mockito.verify(jobPostActivityService).getOne(101);
        Mockito.verify(usersService).getCurrentUserProfile();
    }
}
