package com.jobportal.services;

import com.jobportal.entity.*;
import com.jobportal.repository.JobPostActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobPostActivityServiceTest {

    @Mock
    private JobPostActivityRepository jobPostActivityRepository;

    @InjectMocks
    private JobPostActivityService jobPostActivityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNew() {
        JobPostActivity jobPostActivity = new JobPostActivity();
        jobPostActivity.setJobPostId(1);

        when(jobPostActivityRepository.save(jobPostActivity)).thenReturn(jobPostActivity);

        JobPostActivity savedJob = jobPostActivityService.addNew(jobPostActivity);

        assertNotNull(savedJob);
        assertEquals(1, savedJob.getJobPostId());
        verify(jobPostActivityRepository, times(1)).save(jobPostActivity);
    }

    @Test
    void testGetRecruiterJobs() {
        int recruiterId = 1;

        IRecruiterJobs recruiterJobsMock = mock(IRecruiterJobs.class);
        when(recruiterJobsMock.getTotalCandidates()).thenReturn(5L);
        when(recruiterJobsMock.getJob_post_id()).thenReturn(10);
        when(recruiterJobsMock.getJob_title()).thenReturn("Java Developer");
        when(recruiterJobsMock.getLocationId()).thenReturn(100);
        when(recruiterJobsMock.getCity()).thenReturn("Bangalore");
        when(recruiterJobsMock.getState()).thenReturn("Karnataka");
        when(recruiterJobsMock.getCountry()).thenReturn("India");
        when(recruiterJobsMock.getCompanyId()).thenReturn(200);
        when(recruiterJobsMock.getName()).thenReturn("Tech Corp");

        List<IRecruiterJobs> recruiterJobsList = Collections.singletonList(recruiterJobsMock);
        when(jobPostActivityRepository.getRecruiterJobs(recruiterId)).thenReturn(recruiterJobsList);

        List<RecruiterJobsDto> result = jobPostActivityService.getRecruiterJobs(recruiterId);

        assertNotNull(result);
        assertEquals(1, result.size());
        RecruiterJobsDto dto = result.get(0);
        assertEquals(10, dto.getJobPostId());
        assertEquals("Java Developer", dto.getJobTitle());
        assertEquals("Bangalore", dto.getJobLocation().getCity());
        assertEquals("Tech Corp", dto.getJobCompany().getName());
    }

    @Test
    void testGetOne_Found() {
        JobPostActivity jobPost = new JobPostActivity();
        jobPost.setJobPostId(1);

        when(jobPostActivityRepository.findById(1)).thenReturn(Optional.of(jobPost));

        JobPostActivity result = jobPostActivityService.getOne(1);

        assertNotNull(result);
        assertEquals(1, result.getJobPostId());
    }

    @Test
    void testGetOne_NotFound() {
        when(jobPostActivityRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobPostActivityService.getOne(1);
        });

        assertEquals("Job not found", exception.getMessage());
    }
}
