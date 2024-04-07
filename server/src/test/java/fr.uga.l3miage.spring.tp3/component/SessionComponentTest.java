package fr.uga.l3miage.spring.tp3.component;

import fr.uga.l3miage.spring.tp3.components.SessionComponent;
import fr.uga.l3miage.spring.tp3.enums.SessionStatus;
import fr.uga.l3miage.spring.tp3.exceptions.rest.CreationSessionRestException;
import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionProgrammationRepository;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionRepository;
import fr.uga.l3miage.spring.tp3.repositories.ExamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SessionComponentTest {
    @Autowired
    private SessionComponent sessionComponent;
    @MockBean
    private EcosSessionRepository ecosSessionRepository;
    @MockBean
    private EcosSessionProgrammationRepository ecosSessionProgrammationRepository;
    @MockBean
    private ExamRepository examRepository;
    @Test
    void creationSessionNotFound() {
        when(ecosSessionRepository.findById(anyLong())).thenReturn(Optional.empty());
    }
    @Test
    void creationSessionFound() {
        // Création d'une session
        EcosSessionEntity sessionEntity = EcosSessionEntity.builder()
                .name("Session1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .status(SessionStatus.CREATED)
                .build();
        EcosSessionProgrammationEntity ecosSessionProgrammationEntity = EcosSessionProgrammationEntity
                .builder()
                .id(1L)
                .build();
        ExamEntity examEntity1 = ExamEntity.builder()
                .id(2L)
                .sessionEntity(sessionEntity)
                .build();
        ExamEntity examEntity2 = ExamEntity.builder()
                .id(1L)
                .sessionEntity(sessionEntity)
                .build();
        sessionEntity.setExamEntities(Set.of(examEntity1, examEntity2));
        sessionEntity.setEcosSessionProgrammationEntity(ecosSessionProgrammationEntity);

        when(ecosSessionRepository.findById(1L)).thenReturn(Optional.of(sessionEntity));
        // Assertion
        assertDoesNotThrow( () -> ecosSessionRepository.findById(1L));
    }
}
