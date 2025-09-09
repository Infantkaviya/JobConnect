package com.jobportal.repository;

import com.jobportal.entity.Users;
import com.jobportal.entity.UsersType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void testSaveAndFindByEmail() {
        // Create a user type
        UsersType userType = new UsersType();
        userType.setUserTypeName("Recruiter");

        // Create a user
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setUserTypeId(userType);

        // Save user
        usersRepository.save(user);

        // Fetch by email
        Optional<Users> fetched = usersRepository.findByEmail("test@example.com");

        assertThat(fetched).isPresent();
        assertThat(fetched.get().getEmail()).isEqualTo("test@example.com");
        assertThat(fetched.get().getPassword()).isEqualTo("password");
        assertThat(fetched.get().getUserTypeId()).isEqualTo(userType);
    }
}
