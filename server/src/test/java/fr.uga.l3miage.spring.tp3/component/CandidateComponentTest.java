package fr.uga.l3miage.spring.tp3.component;

import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import fr.uga.l3miage.spring.tp3.exceptions.rest.CandidateNotFoundRestException;
import fr.uga.l3miage.spring.tp3.exceptions.technical.CandidateNotFoundException;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateEvaluationGridRepository;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import fr.uga.l3miage.spring.tp3.repositories.ExamRepository;
import fr.uga.l3miage.spring.tp3.services.CandidateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CandidateComponentTest {
    @Autowired
    private CandidateComponent candidateComponent;
    @Autowired
    private  CandidateService candidateService;
    @MockBean
    private CandidateEvaluationGridEntity candidateEvaluationGridEntity;
    @MockBean
    private ExamEntity examEntity;
    @MockBean
    private ExamRepository examRepository;
    @MockBean
    private CandidateRepository candidateRepository;
    @MockBean
    private CandidateEvaluationGridRepository candidateEvaluationGridRepository;

    @Test
    void getCandidateAverageNotFound(){
        //given
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.empty());
        // then -when
        assertThrows(CandidateNotFoundException.class, () -> candidateComponent.getCandidatById(1L));
    }

    @Test
    void getCandidateAverageFound() {
        // Création d'un candidat avec des évaluations
        CandidateEntity candidateEntity = CandidateEntity
                .builder()
                .id(1L)
                .email("salut@gmail.com")
                .birthDate(LocalDate.of(2007, 5, 3))
                .build();
        candidateRepository.save(candidateEntity);

        ExamEntity examEntity = ExamEntity
                .builder()
                .weight(2)
                .build();
        examRepository.save(examEntity);

        // Création des évaluations associées au candidat
        CandidateEvaluationGridEntity candidateEvaluationGridEntity = CandidateEvaluationGridEntity
                .builder()
                .grade(14)
                .candidateEntity(candidateEntity)
                .examEntity(examEntity)
                .build();

        CandidateEvaluationGridEntity candidateEvaluationGridEntity1 = CandidateEvaluationGridEntity
                .builder()
                .grade(12)
                .candidateEntity(candidateEntity)
                .examEntity(examEntity)
                .build();

        // Sauvegarde des données dans les repositories
        candidateEvaluationGridRepository.save(candidateEvaluationGridEntity);
        candidateEvaluationGridRepository.save(candidateEvaluationGridEntity1);

        // Association des évaluations au candidat
        candidateEntity.setCandidateEvaluationGridEntities(Set.of(candidateEvaluationGridEntity, candidateEvaluationGridEntity1));
        examEntity.setCandidateEvaluationGridEntities(Set.of(candidateEvaluationGridEntity,candidateEvaluationGridEntity1));

        // Simulation de la recherche du candidat par son identifiant
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidateEntity));

        // Appel de la méthode à tester
        double averageGrade = candidateService.getCandidateAverage(1L)/2;

        // Assertion
        assertEquals(13,averageGrade);
        assertDoesNotThrow( () ->candidateService.getCandidateAverage(1L));

    }

}









