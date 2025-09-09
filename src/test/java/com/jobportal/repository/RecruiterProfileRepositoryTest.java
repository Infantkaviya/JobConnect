package com.jobportal.repository;

import com.jobportal.entity.RecruiterProfile;
import com.jobportal.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RecruiterProfileRepositoryTest {

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Test
    void testSaveAndFindById() {
        // Create dummy user
        Users user = new Users();
        user.setEmail("recruiter@example.com");
        user.setPassword("password");

        // Create recruiter profile
        RecruiterProfile profile = new RecruiterProfile(user);
        profile.setUserAccountId(1);
        profile.setFirstName("Alice");
        profile.setLastName("Smith");
        profile.setCompany("ABC Corp");

        // Save
        recruiterProfileRepository.save(profile);

        // Fetch
        RecruiterProfile fetched = recruiterProfileRepository.findById(profile.getUserAccountId()).orElse(null);

        assertThat(fetched).isNotNull();
        assertThat(fetched.getFirstName()).isEqualTo("Alice");
        assertThat(fetched.getLastName()).isEqualTo("Smith");
        assertThat(fetched.getCompany()).isEqualTo("ABC Corp");
    }
}
