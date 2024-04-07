package fr.uga.l3miage.spring.tp3.controller;

import fr.uga.l3miage.spring.tp3.components.CandidateComponent;
import fr.uga.l3miage.spring.tp3.exceptions.handlers.CandidatNotFoundHandler;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateEvaluationGridRepository;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import fr.uga.l3miage.spring.tp3.repositories.ExamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;


import java.time.LocalDate;
import java.util.*;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureTestDatabase
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class CandidateControllerTest {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CandidateEvaluationGridRepository candidateEvaluationGridRepository;

    @SpyBean
    private CandidateComponent candidateComponent;
// Ici,il me manque une configuration pour pouvoir supprimer tous en cascade sinon y'aura une erreur violation de contraintes
    /*@AfterEach
    public void clear(){
        candidateRepository.deleteAll();
    }*/



    @Test
    void NotFoundCandidateId() {
        // Given
        final HttpHeaders headers = new HttpHeaders();
        final Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("candidateId","le candidat n'existe pas");
        // when
        ResponseEntity<CandidatNotFoundHandler> response = testRestTemplate.exchange("/{candidateId}/average",HttpMethod.GET,new HttpEntity<>(null,headers),CandidatNotFoundHandler.class,urlParams);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetFoundCandidateAverage() {
        // Given
        // Création d'un candidat avec des évaluations
        final CandidateEntity candidateEntity = CandidateEntity
                .builder()
                .id(1L)
                .email("salut@gmail.com")
                .birthDate(LocalDate.of(2007, 5, 3))
                .build();
        candidateRepository.save(candidateEntity);

        // Création d'un examen
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

        // When
        ResponseEntity<Double> response = testRestTemplate.exchange("/api/candidates/{candidateId}/average", HttpMethod.GET, null, Double.class, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Calculate expected average
       double expectedAverage = (double)  (candidateEvaluationGridEntity.getGrade() + candidateEvaluationGridEntity1.getGrade());
       assertThat(response.getBody()).isEqualTo(expectedAverage);
    }



}
