package com.jobportal.services;

import com.jobportal.entity.RecruiterProfile;
import com.jobportal.repository.RecruiterProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecruiterProfileServiceTest {

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @InjectMocks
    private RecruiterProfileService recruiterProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNew() {
        RecruiterProfile profile = new RecruiterProfile();
        profile.setUserAccountId(1); // use correct field name

        when(recruiterProfileRepository.save(profile)).thenReturn(profile);

        RecruiterProfile savedProfile = recruiterProfileService.addNew(profile);

        assertNotNull(savedProfile);
        assertEquals(1, savedProfile.getUserAccountId());
        verify(recruiterProfileRepository, times(1)).save(profile);
    }

    @Test
    void testGetOne_Found() {
        RecruiterProfile profile = new RecruiterProfile();
        profile.setUserAccountId(1);

        when(recruiterProfileRepository.findById(1)).thenReturn(Optional.of(profile));

        Optional<RecruiterProfile> result = recruiterProfileService.getOne(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getUserAccountId());
        verify(recruiterProfileRepository, times(1)).findById(1);
    }

    @Test
    void testGetOne_NotFound() {
        when(recruiterProfileRepository.findById(1)).thenReturn(Optional.empty());

        Optional<RecruiterProfile> result = recruiterProfileService.getOne(1);

        assertFalse(result.isPresent());
        verify(recruiterProfileRepository, times(1)).findById(1);
    }
}
