package com.amool.hexagonal.adapters.out.persistence;

import com.amool.hexagonal.adapters.out.persistence.entity.WorkEntity;
import com.amool.hexagonal.adapters.out.persistence.mappers.WorkMapper;
import com.amool.hexagonal.domain.model.Work;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorksPersistenceAdapterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private WorkMapper workMapper;

    @InjectMocks
    private WorksPersistenceAdapter worksPersistenceAdapter;

    @Mock
    private TypedQuery<WorkEntity> workEntityTypedQuery;

    @Mock
    private EntityTransaction entityTransaction;

    private Work work;
    private WorkEntity workEntity;

    @BeforeEach
    void setUp() {
        work = new Work();
        work.setId(1L);
        work.setTitle("Test Work");
        
        workEntity = new WorkEntity();
        workEntity.setId(1L);
        workEntity.setTitle("Test Work");
        
        when(entityManager.getTransaction()).thenReturn(entityTransaction);
        when(workMapper.toEntity(any(Work.class))).thenReturn(workEntity);
        when(workMapper.toDomain(any(WorkEntity.class))).thenReturn(work);
    }

    @Test
    void obtainWorkById_shouldReturnWork_whenWorkExists() {
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(workEntityTypedQuery);
        when(workEntityTypedQuery.setParameter(anyString(), any())).thenReturn(workEntityTypedQuery);
        when(workEntityTypedQuery.setMaxResults(anyInt())).thenReturn(workEntityTypedQuery);
        when(workEntityTypedQuery.getResultList()).thenReturn(List.of(workEntity));

        Optional<Work> result = worksPersistenceAdapter.obtainWorkById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Work", result.get().getTitle());
    }

    @Test
    void obtainWorkById_shouldReturnEmpty_whenWorkDoesNotExist() {
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(workEntityTypedQuery);
        when(workEntityTypedQuery.setParameter(anyString(), any())).thenReturn(workEntityTypedQuery);
        when(workEntityTypedQuery.setMaxResults(anyInt())).thenReturn(workEntityTypedQuery);
        when(workEntityTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        Optional<Work> result = worksPersistenceAdapter.obtainWorkById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getWorksByUserId_shouldReturnWorks() {
        when(entityManager.createQuery(anyString(), eq(WorkEntity.class))).thenReturn(workEntityTypedQuery);
        when(workEntityTypedQuery.setParameter(anyString(), any())).thenReturn(workEntityTypedQuery);
        when(workEntityTypedQuery.getResultList()).thenReturn(List.of(workEntity));

        List<Work> result = worksPersistenceAdapter.getWorksByUserId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void createWork_shouldPersistAndReturnId() {
        when(entityManager.merge(any(WorkEntity.class))).thenReturn(workEntity);

        Long result = worksPersistenceAdapter.createWork(work);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(entityManager).persist(any(WorkEntity.class));
        verify(entityManager).flush();
    }

    @Test
    void updateWork_shouldReturnTrue_whenWorkExists() {
        when(entityManager.find(WorkEntity.class, 1L)).thenReturn(workEntity);
        when(entityManager.merge(any(WorkEntity.class))).thenReturn(workEntity);

        boolean result = worksPersistenceAdapter.updateWork(work);

        assertTrue(result);
        verify(entityManager).merge(any(WorkEntity.class));
        verify(entityManager).flush();
    }

    @Test
    void updateWork_shouldReturnFalse_whenWorkDoesNotExist() {
        when(entityManager.find(WorkEntity.class, 999L)).thenReturn(null);
        work.setId(999L);

        boolean result = worksPersistenceAdapter.updateWork(work);

        assertFalse(result);
        verify(entityManager, never()).merge(any(WorkEntity.class));
        verify(entityManager, never()).flush();
    }

    @Test
    void deleteWork_shouldReturnTrue_whenWorkExists() {
        WorkEntity workEntity = new WorkEntity();
        workEntity.setId(1L);
        workEntity.setChapters(Collections.emptyList());
        workEntity.setCategories(Collections.emptyList());
        
        when(entityManager.find(WorkEntity.class, 1L)).thenReturn(workEntity);
        when(entityManager.merge(workEntity)).thenReturn(workEntity);

        boolean result = worksPersistenceAdapter.deleteWork(1L);

        assertTrue(result);
        verify(entityManager).remove(workEntity);
        verify(entityManager, times(2)).flush();
    }

    @Test
    void deleteWork_shouldReturnFalse_whenWorkDoesNotExist() {
        when(entityManager.find(WorkEntity.class, 999L)).thenReturn(null);

        boolean result = worksPersistenceAdapter.deleteWork(999L);

        assertFalse(result);
        verify(entityManager, never()).remove(any());
        verify(entityManager, never()).flush();
    }

    @Test
    void deleteWork_shouldClearRelationsBeforeDeletion() {
        WorkEntity workEntity = new WorkEntity();
        workEntity.setId(1L);
        workEntity.setChapters(Collections.emptyList());
        workEntity.setCategories(Collections.emptyList());
        
        when(entityManager.find(WorkEntity.class, 1L)).thenReturn(workEntity);
        when(entityManager.merge(workEntity)).thenReturn(workEntity);

        worksPersistenceAdapter.deleteWork(1L);

        assertTrue(workEntity.getChapters().isEmpty());
        assertTrue(workEntity.getCategories().isEmpty());
        verify(entityManager).remove(workEntity);
    }
}
