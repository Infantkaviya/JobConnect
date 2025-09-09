package com.jobportal.repository;

import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JobSeekerProfileRepositoryTest {

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Test
    void testSaveAndFindById() {
        // Create dummy user
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");

        // Create job seeker profile
        JobSeekerProfile profile = new JobSeekerProfile(user);
        profile.setUserAccountId(1);
        profile.setFirstName("John");
        profile.setLastName("Doe");

        // Save
        jobSeekerProfileRepository.save(profile);

        // Fetch
        JobSeekerProfile fetched = jobSeekerProfileRepository.findById(profile.getUserAccountId()).orElse(null);

        assertThat(fetched).isNotNull();
        assertThat(fetched.getFirstName()).isEqualTo("John");
        assertThat(fetched.getLastName()).isEqualTo("Doe");
    }
}
