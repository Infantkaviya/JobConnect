package com.jobportal.repository;

import com.jobportal.entity.UsersType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsersTypeRepositoryTest {

    @Autowired
    private UsersTypeRepository usersTypeRepository;

    @Test
    void testSaveAndFindAll() {
        // Create user types
        UsersType type1 = new UsersType();
        type1.setUserTypeName("Recruiter");

        UsersType type2 = new UsersType();
        type2.setUserTypeName("JobSeeker");

        // Save
        usersTypeRepository.save(type1);
        usersTypeRepository.save(type2);

        // Fetch all
        List<UsersType> allTypes = usersTypeRepository.findAll();

        assertThat(allTypes).isNotNull();
        assertThat(allTypes.size()).isEqualTo(2);
        assertThat(allTypes).extracting("userTypeName").containsExactlyInAnyOrder("Recruiter", "JobSeeker");
    }
}
