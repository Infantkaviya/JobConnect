package com.jobportal.repository;

import com.jobportal.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Rollback(false) // disable rollback if you want to inspect data in H2
class JobPostActivityRepositoryTest {

    @Autowired
    private JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JobCompanyRepository jobCompanyRepository;

    @Autowired
    private JobLocationRepository jobLocationRepository;

    private Users recruiter;

    @BeforeEach
    void setup() {
        // Create recruiter user
        UsersType recruiterType = new UsersType();
        recruiterType.setUserTypeName("Recruiter");

        recruiter = new Users();
        recruiter.setEmail("recruiter@test.com");
        recruiter.setPassword("password");
        recruiter.setActive(true);
        recruiter.setRegistrationDate(new Date());
        recruiter.setUserTypeId(recruiterType);

        recruiter = usersRepository.save(recruiter);

        // Create company
        JobCompany company = new JobCompany();
        company.setName("Test Company");
        company.setLogo("logo.png");
        company = jobCompanyRepository.save(company);

        // Create location
        JobLocation location = new JobLocation();
        location.setCity("Chennai");
        location.setState("TN");
        location.setCountry("India");
        location = jobLocationRepository.save(location);

        // Create job post activity
        JobPostActivity jobPost = new JobPostActivity();
        jobPost.setJobTitle("Java Developer");
        jobPost.setDescriptionOfJob("Spring Boot project work");
        jobPost.setPostedById(recruiter);
        jobPost.setJobCompanyId(company);
        jobPost.setJobLocationId(location);
        jobPost.setJobType("Full-time");
        jobPost.setSalary("10 LPA");
        jobPost.setRemote("Yes");
        jobPost.setPostedDate(new Date());

        jobPostActivityRepository.save(jobPost);
    }

    @Test
    void testGetRecruiterJobs() {
        List<IRecruiterJobs> recruiterJobs = jobPostActivityRepository.getRecruiterJobs(recruiter.getUserId());

        assertThat(recruiterJobs).isNotEmpty();
        IRecruiterJobs job = recruiterJobs.get(0);

        assertThat(job.getJob_title()).isEqualTo("Java Developer");
        assertThat(job.getCity()).isEqualTo("Chennai");
        assertThat(job.getName()).isEqualTo("Test Company");
    }

    private class JobCompanyRepository {
        public JobCompany save(JobCompany company) {
            return company;
        }
    }

    private class JobLocationRepository {
        public JobLocation save(JobLocation location) {
            return location;
        }
    }
}
