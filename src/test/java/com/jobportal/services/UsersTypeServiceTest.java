package com.jobportal.services;

import com.jobportal.entity.UsersType;
import com.jobportal.repository.UsersTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersTypeServiceTest {

    @Mock
    private UsersTypeRepository usersTypeRepository;

    @InjectMocks
    private UsersTypeService usersTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        UsersType type1 = new UsersType(1, "Recruiter", null);
        UsersType type2 = new UsersType(2, "JobSeeker", null);

        when(usersTypeRepository.findAll()).thenReturn(List.of(type1, type2));

        List<UsersType> result = usersTypeService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Recruiter", result.get(0).getUserTypeName());
        assertEquals("JobSeeker", result.get(1).getUserTypeName());

        verify(usersTypeRepository, times(1)).findAll();
    }
}
