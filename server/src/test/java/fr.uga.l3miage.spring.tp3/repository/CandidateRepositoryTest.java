package fr.uga.l3miage.spring.tp3.repository;


import fr.uga.l3miage.spring.tp3.enums.TestCenterCode;
import fr.uga.l3miage.spring.tp3.models.CandidateEntity;
import fr.uga.l3miage.spring.tp3.models.CandidateEvaluationGridEntity;
import fr.uga.l3miage.spring.tp3.models.TestCenterEntity;
import fr.uga.l3miage.spring.tp3.repositories.CandidateEvaluationGridRepository;
import fr.uga.l3miage.spring.tp3.repositories.CandidateRepository;
import fr.uga.l3miage.spring.tp3.repositories.TestCenterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static fr.uga.l3miage.spring.tp3.enums.TestCenterCode.DIJ;
import static fr.uga.l3miage.spring.tp3.enums.TestCenterCode.NCE;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class CandidateRepositoryTest {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private TestCenterRepository testCenterRepository;
    @Autowired
    private CandidateEvaluationGridRepository candidateEvaluationGridRepository;
    @Test
    void findAllByTestCenterEntityCode(){
        TestCenterEntity testCenterEntity = TestCenterEntity
                .builder()
                .code(TestCenterCode.DIJ)
                .university("Toulouse III")
                .city("Hollande")
                .build();
        TestCenterEntity testCenterEntity1 = TestCenterEntity
                .builder()
                .code(DIJ)
                .university("Oxford")
                .city("Dallas")
                .build();
        testCenterRepository.save(testCenterEntity);
        testCenterRepository.save(testCenterEntity1);
      CandidateEntity candidateEntity = CandidateEntity
              .builder()
              .email("voir@gmail")
              .birthDate(LocalDate.of(2000,5,15))
              .testCenterEntity(testCenterEntity)
              .build();
      CandidateEntity candidateEntity1 = CandidateEntity
              .builder()
              .email("aller@gmail.com")
              .birthDate(LocalDate.of(2002,3,14))
              .testCenterEntity(testCenterEntity1)
              .build();
        candidateRepository.save(candidateEntity);
        candidateRepository.save(candidateEntity1);
        testCenterEntity.setCandidateEntities(Set.of(candidateEntity,candidateEntity1));

        Set<CandidateEntity> candidateEntitiesResponses = candidateRepository.findAllByTestCenterEntityCode(DIJ);
        assertThat(candidateEntitiesResponses).hasSize(2);
        assertThat(candidateEntitiesResponses.stream().findFirst().get().getTestCenterEntity().getCode()).isEqualTo(DIJ);
    }
    @Test
    public void testFindAllByCandidateEvaluationGridEntitiesGradeLessThan() {
        // Créez une instance de CandidateEntity
        CandidateEntity candidateEntity = CandidateEntity
                .builder()
                .email("voir@gmail")
                .birthDate(LocalDate.of(2000, 5, 15))
                .build();
        CandidateEntity candidateEntity1 = CandidateEntity
                .builder()
                .email("hol@gmail.com")
                .birthDate(LocalDate.of(2001,8,4))
                .build();
        candidateRepository.save(candidateEntity);
        candidateRepository.save(candidateEntity1);
        // Créez des instances de CandidateEvaluationGridEntity
        CandidateEvaluationGridEntity candidateEvaluationGridEntity1 = CandidateEvaluationGridEntity.builder()
                .grade(18.5)
                .candidateEntity(candidateEntity)
                .build();
        CandidateEvaluationGridEntity candidateEvaluationGridEntity2 = CandidateEvaluationGridEntity.builder()
                .grade(16.0)
                .candidateEntity(candidateEntity)
                .build();
        CandidateEvaluationGridEntity candidateEvaluationGridEntity3 = CandidateEvaluationGridEntity.builder()
                .grade(12.5)
                .candidateEntity(candidateEntity1)
                .build();
        candidateEvaluationGridRepository.save(candidateEvaluationGridEntity3);
        candidateEvaluationGridRepository.save(candidateEvaluationGridEntity1);
        candidateEvaluationGridRepository.save(candidateEvaluationGridEntity2);
        // Créez un list de CandidateEvaluationGridEntity pour le candidat
        candidateEntity1.setCandidateEvaluationGridEntities(Set.of(candidateEvaluationGridEntity3));
        candidateEntity.setCandidateEvaluationGridEntities(Set.of(candidateEvaluationGridEntity1,candidateEvaluationGridEntity2));
        Set<CandidateEntity> candidateEntitiesResponse = candidateRepository.findAllByCandidateEvaluationGridEntitiesGradeLessThan(15.0);
        // Vérifiez le résultat
        assertThat(candidateEntitiesResponse).hasSize(1);
    }
    @Test
    void findAllByHasExtraTimeFalseAndBirthDateBefore(){
        CandidateEntity candidateEntity = CandidateEntity
                .builder()
                .email("fello@gmail.com")
                .birthDate(LocalDate.of(1995,5,3))
                .hasExtraTime(true)
                .build();
        CandidateEntity candidateEntity1 = CandidateEntity
                .builder()
                .email("hola@gmail.com")
                .birthDate(LocalDate.of(1996,7,13))
                .hasExtraTime(false)
                .build();
        CandidateEntity candidateEntity2 = CandidateEntity
                .builder()
                .email("hum@gmail.com")
                .birthDate(LocalDate.of(1997,5,3))
                .hasExtraTime(false)
                .build();
        candidateRepository.save(candidateEntity);
        candidateRepository.save(candidateEntity1);
        candidateRepository.save(candidateEntity2);

        Set<CandidateEntity> candidateEntitiesResponse = candidateRepository.findAllByHasExtraTimeFalseAndBirthDateBefore(LocalDate.of(2000,8,4));
        assertThat(candidateEntitiesResponse).hasSize(2);
        assertThat(candidateEntitiesResponse.stream().findFirst().get().getBirthDate()).isEqualTo(LocalDate.of(1996, 7, 13)) ;

    }

}
