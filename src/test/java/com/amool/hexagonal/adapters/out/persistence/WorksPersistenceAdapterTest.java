package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.FormatEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.UserEntity;
import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.domain.model.Work;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WorksPersistenceAdapterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<WorkEntity> typedQuery;

    @InjectMocks
    private WorksPersistenceAdapter worksPersistenceAdapter;

    private UserEntity testUser;
    private FormatEntity testFormat;
    private WorkEntity testWork1;
    private WorkEntity testWork2;

    @BeforeEach
    void setUp() {
        
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setPassword("password");

        testFormat = new FormatEntity();
        testFormat.setId(1L);
        testFormat.setName("Novel");

        testWork1 = new WorkEntity();
        testWork1.setId(1L);
        testWork1.setTitle("Test Work 1");
        testWork1.setDescription("Description 1");
        testWork1.setState("PUBLISHED");
        testWork1.setPrice(10.99);
        testWork1.setLikes(5);
        testWork1.setPublicationDate(LocalDate.of(2025, 1, 1));
        testWork1.setCreator(testUser);
        testWork1.setFormatEntity(testFormat);

        testWork2 = new WorkEntity();
        testWork2.setId(2L);
        testWork2.setTitle("Test Work 2");
        testWork2.setDescription("Description 2");
        testWork2.setState("DRAFT");
        testWork2.setPrice(15.99);
        testWork2.setLikes(10);
        testWork2.setPublicationDate(LocalDate.of(2025, 2, 1));
        testWork2.setCreator(testUser);
        testWork2.setFormatEntity(testFormat);
    }

    @Test
    void getWorksByUserId_WithValidUserId_ReturnsWorksList() {

        Long userId = 1L;
        List<WorkEntity> workEntities = Arrays.asList(testWork1, testWork2);

        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("userId"), eq(userId))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(workEntities);


        List<Work> result = worksPersistenceAdapter.getWorksByUserId(userId);


        assertNotNull(result);
        assertEquals(2, result.size());

        Work work1 = result.get(0);
        assertEquals(testWork1.getId(), work1.getId());
        assertEquals(testWork1.getTitle(), work1.getTitle());
        assertEquals(testWork1.getDescription(), work1.getDescription());
        assertEquals(testWork1.getState(), work1.getState());
        assertEquals(testWork1.getPrice(), work1.getPrice());
        assertEquals(testWork1.getLikes(), work1.getLikes());

        Work work2 = result.get(1);
        assertEquals(testWork2.getId(), work2.getId());
        assertEquals(testWork2.getTitle(), work2.getTitle());
        assertEquals(testWork2.getDescription(), work2.getDescription());

        verify(entityManager, times(1)).createQuery(anyString(), eq(WorkEntity.class));
        verify(typedQuery, times(1)).setParameter("userId", userId);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    void getWorksByUserId_WithNoWorks_ReturnsEmptyList() {
      
        Long userId = 1L;
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("userId"), eq(userId))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

     
        List<Work> result = worksPersistenceAdapter.getWorksByUserId(userId);

      
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(entityManager, times(1)).createQuery(anyString(), eq(WorkEntity.class));
        verify(typedQuery, times(1)).setParameter("userId", userId);
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    void getWorksByUserId_VerifyCorrectJPQLQuery() {
  
        Long userId = 1L;
        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("userId"), eq(userId))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        worksPersistenceAdapter.getWorksByUserId(userId);

        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(WorkEntity.class));
        String capturedJpql = jpqlCaptor.getValue();
        assertTrue(capturedJpql.contains("SELECT DISTINCT w FROM WorkEntity w"));
        assertTrue(capturedJpql.contains("WHERE w.creator.id = :userId"));
    }

    @Test
    void execute_WithValidWorkId_ReturnsWork() {
       
        Long workId = 1L;
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("workId"), eq(workId))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(testWork1);

       
        Work result = worksPersistenceAdapter.execute(workId);

      
        assertNotNull(result);
        assertEquals(testWork1.getId(), result.getId());
        assertEquals(testWork1.getTitle(), result.getTitle());
        assertEquals(testWork1.getDescription(), result.getDescription());
        assertEquals(testWork1.getState(), result.getState());
        assertEquals(testWork1.getPrice(), result.getPrice());
        assertEquals(testWork1.getLikes(), result.getLikes());

   
        verify(entityManager, times(1)).createQuery(anyString(), eq(WorkEntity.class));
        verify(typedQuery, times(1)).setParameter("workId", workId);
        verify(typedQuery, times(1)).getSingleResult();
    }

    @Test
    void execute_VerifyJoinFetchInQuery() {
   
        Long workId = 1L;
        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("workId"), eq(workId))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(testWork1);

  
        worksPersistenceAdapter.execute(workId);

   
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(WorkEntity.class));
        String capturedJpql = jpqlCaptor.getValue();
        assertTrue(capturedJpql.contains("SELECT DISTINCT w FROM WorkEntity w"));
        assertTrue(capturedJpql.contains("LEFT JOIN FETCH w.creator"));
        assertTrue(capturedJpql.contains("LEFT JOIN FETCH w.formatEntity"));
        assertTrue(capturedJpql.contains("LEFT JOIN FETCH w.chapters"));
        assertTrue(capturedJpql.contains("LEFT JOIN FETCH w.categories"));
        assertTrue(capturedJpql.contains("WHERE w.id = :workId"));
    }

    @Test
    void execute_WithNullEntity_ReturnsNull() {
     
        Long workId = 1L;
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("workId"), eq(workId))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(null);

        
        Work result = worksPersistenceAdapter.execute(workId);

        assertNull(result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(WorkEntity.class));
        verify(typedQuery, times(1)).setParameter("workId", workId);
        verify(typedQuery, times(1)).getSingleResult();
    }

    @Test
    void getWorksByUserId_WithSingleWork_ReturnsSingleWork() {
      
        Long userId = 1L;
        List<WorkEntity> workEntities = Collections.singletonList(testWork1);

        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("userId"), eq(userId))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(workEntities);
     
        List<Work> result = worksPersistenceAdapter.getWorksByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWork1.getId(), result.get(0).getId());
        assertEquals(testWork1.getTitle(), result.get(0).getTitle());
    }

    @Test
    void getWorksByUserId_VerifyMappingFromEntityToDomain() {

        Long userId = 1L;
        List<WorkEntity> workEntities = Arrays.asList(testWork1, testWork2);

        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("userId"), eq(userId))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(workEntities);

        List<Work> result = worksPersistenceAdapter.getWorksByUserId(userId);

        assertEquals(workEntities.size(), result.size());
        for (int i = 0; i < workEntities.size(); i++) {
            WorkEntity entity = workEntities.get(i);
            Work domain = result.get(i);
            
            assertEquals(entity.getId(), domain.getId());
            assertEquals(entity.getTitle(), domain.getTitle());
            assertEquals(entity.getDescription(), domain.getDescription());
            assertEquals(entity.getState(), domain.getState());
            assertEquals(entity.getPrice(), domain.getPrice());
            assertEquals(entity.getLikes(), domain.getLikes());
            assertEquals(entity.getPublicationDate(), domain.getPublicationDate());
        }
    }

    @Test
    void getWorksByUserId_WithDifferentUserIds_UsesCorrectParameter() {
    
        Long userId1 = 1L;
        Long userId2 = 2L;
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyLong())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

    
        worksPersistenceAdapter.getWorksByUserId(userId1);
        worksPersistenceAdapter.getWorksByUserId(userId2);

        verify(typedQuery, times(1)).setParameter("userId", userId1);
        verify(typedQuery, times(1)).setParameter("userId", userId2);
        verify(typedQuery, times(2)).getResultList();
    }

    @Test
    void execute_WithDifferentWorkIds_UsesCorrectParameter() {

        Long workId1 = 1L;
        Long workId2 = 2L;
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyLong())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(testWork1).thenReturn(testWork2);

        worksPersistenceAdapter.execute(workId1);
        worksPersistenceAdapter.execute(workId2);

        verify(typedQuery, times(1)).setParameter("workId", workId1);
        verify(typedQuery, times(1)).setParameter("workId", workId2);
        verify(typedQuery, times(2)).getSingleResult();
    }
}
