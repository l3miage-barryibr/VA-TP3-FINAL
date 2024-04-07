package fr.uga.l3miage.spring.tp3.controller;

import fr.uga.l3miage.spring.tp3.components.SessionComponent;
import fr.uga.l3miage.spring.tp3.enums.SessionStatus;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.request.SessionCreationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.Set;

import static fr.uga.l3miage.spring.tp3.responses.enums.SessionStatus.CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class SessionControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private SessionComponent sessionComponent;

    @Test
    void canCreateSession() {
        final HttpHeaders headers = new HttpHeaders();

        final SessionCreationRequest sessionCreationRequest = SessionCreationRequest.builder()
                .name("Session1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .status(CREATED)
                .build();

        // Création de l'entité EcosSessionProgrammationEntity
        EcosSessionProgrammationEntity ecosSessionProgrammationEntity = EcosSessionProgrammationEntity.builder()
                .id(1L)
                .build();

        // Création des entités ExamEntity
        ExamEntity examEntity1 = ExamEntity.builder()
                .id(1L)
                .build();

        ExamEntity examEntity2 = ExamEntity.builder()
                .id(2L)
                .build();

       // when(sessionComponent.createSession(sessionCreationRequest)).thenReturn(ecosSessionProgrammationEntity);

        // Envoi de la requête de création de session à l'API
        ResponseEntity<EcosSessionProgrammationEntity> response = testRestTemplate.exchange(
                "/api/sessions/create",
                HttpMethod.POST,
                new HttpEntity<>(sessionCreationRequest, headers),
                EcosSessionProgrammationEntity.class
        );

        // Vérification que la création de session a réussi
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(ecosSessionProgrammationEntity);
    }
}
