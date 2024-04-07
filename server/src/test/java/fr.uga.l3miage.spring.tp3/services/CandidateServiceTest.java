package fr.uga.l3miage.spring.tp3.services;

import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import fr.uga.l3miage.spring.tp3.exceptions.technical.CandidateNotFoundException;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK)
public class CandidateServiceTest {
    @Autowired
    private CandidateService candidateService;
    @MockBean
    private CandidateComponent candidateComponent;
    @Test
    void RequestGetCandidateAverage() throws CandidateNotFoundException {

        CandidateEvaluationGridEntity candidateEvaluationGridEntity = CandidateEvaluationGridEntity
                .builder()
                .grade(15)
                .build();

        CandidateEvaluationGridEntity candidateEvaluationGridEntity1 = CandidateEvaluationGridEntity
                .builder()
                .grade(16)
                .build();

        ExamEntity examEntity = ExamEntity
                .builder()
                .weight(1)
                .build();

        candidateEvaluationGridEntity.setExamEntity(examEntity);
        candidateEvaluationGridEntity1.setExamEntity(examEntity);

        Set<CandidateEvaluationGridEntity> candidateEvaluationGridEntities = new HashSet<>();
        candidateEvaluationGridEntities.add(candidateEvaluationGridEntity);
        candidateEvaluationGridEntities.add(candidateEvaluationGridEntity1);

        examEntity.setCandidateEvaluationGridEntities(candidateEvaluationGridEntities);

        CandidateEntity candidateEntity = CandidateEntity
                .builder()
                .id(2L)
                .birthDate(LocalDate.of(2004,7,6))
                .candidateEvaluationGridEntities(candidateEvaluationGridEntities)
                .build();
        // When
        when(candidateComponent.getCandidatById(anyLong())).thenReturn(candidateEntity);
        // then
        double average = candidateService.getCandidateAverage(2L);
        assertThat(average).isEqualTo(15.5);
        verify(candidateComponent,times(1)).getCandidatById(2L);


    }


}