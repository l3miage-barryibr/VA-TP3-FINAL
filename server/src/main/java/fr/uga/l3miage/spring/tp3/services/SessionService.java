package fr.uga.l3miage.spring.tp3.services;

import fr.uga.l3miage.spring.tp3.components.ExamComponent;
import fr.uga.l3miage.spring.tp3.components.SessionComponent;
import fr.uga.l3miage.spring.tp3.enums.SessionStatus;
import fr.uga.l3miage.spring.tp3.exceptions.rest.CreationSessionRestException;
import fr.uga.l3miage.spring.tp3.exceptions.rest.EndSessionRestException;
import fr.uga.l3miage.spring.tp3.exceptions.rest.LastStepNotPassedException;
import fr.uga.l3miage.spring.tp3.exceptions.rest.PreviousStateNotStartedException;
import fr.uga.l3miage.spring.tp3.exceptions.technical.ExamNotFoundException;
import fr.uga.l3miage.spring.tp3.mappers.SessionMapper;
import fr.uga.l3miage.spring.tp3.models.EcosSessionEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationEntity;
import fr.uga.l3miage.spring.tp3.models.EcosSessionProgrammationStepEntity;
import fr.uga.l3miage.spring.tp3.models.ExamEntity;
import fr.uga.l3miage.spring.tp3.repositories.EcosSessionRepository;
import fr.uga.l3miage.spring.tp3.request.SessionCreationRequest;
import fr.uga.l3miage.spring.tp3.responses.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionMapper sessionMapper;
    private final ExamComponent examComponent;
    private final SessionComponent sessionComponent;
    private final EcosSessionRepository ecosSessionRepository;


    public SessionResponse createSession(SessionCreationRequest sessionCreationRequest){
        try {
            EcosSessionEntity ecosSessionEntity = sessionMapper.toEntity(sessionCreationRequest);
            EcosSessionProgrammationEntity programmation = sessionMapper.toEntity(sessionCreationRequest.getEcosSessionProgrammation());
            Set<EcosSessionProgrammationStepEntity> stepEntities = sessionCreationRequest.getEcosSessionProgrammation()
                    .getSteps()
                    .stream()
                    .map(sessionMapper::toEntity)
                    .collect(Collectors.toSet());

            Set<ExamEntity> exams = examComponent.getAllById(sessionCreationRequest.getExamsId());

            ecosSessionEntity.setExamEntities(exams);
            programmation.setEcosSessionProgrammationStepEntities(stepEntities);
            ecosSessionEntity.setEcosSessionProgrammationEntity(programmation);

            ecosSessionEntity.setStatus(SessionStatus.CREATED);

            return sessionMapper.toResponse(sessionComponent.createSession(ecosSessionEntity));
        }catch (RuntimeException | ExamNotFoundException e){
            throw new CreationSessionRestException(e.getMessage());
        }
    }
    @PutMapping("/sessions/{sessionId}/end")
    public ResponseEntity<String> endSession(@PathVariable Long sessionId) {
        try {
            EcosSessionEntity sessionEntity = ecosSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new EndSessionRestException("Session not found"));

            // Vérifier si l'état actuel de la session est EVAL_STARTED
            if (sessionEntity.getStatus() != SessionStatus.EVAL_STARTED) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Session is not in EVAL_STARTED state");
            }

            // Vérifier si la dernière étape a été passée
            if (!isLastStepPassed(sessionEntity)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Last step of the session is not yet passed");
            }

            // Mettre à jour l'état de la session à EVAL_ENDED
            sessionEntity.setStatus(SessionStatus.EVAL_ENDED);
            ecosSessionRepository.save(sessionEntity);

            return ResponseEntity.ok("Session ended successfully.");
        } catch (EndSessionRestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (LastStepNotPassedException | PreviousStateNotStartedException e) {
            // Si l  a vérification métier échoue, retourner 409 CONFLIT
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            // Si une autre exception se produit, retourner 500 INTERNAL SERVER ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to end session: " + e.getMessage());
        }
    }


    // Méthode pour vérifier si la dernière étape a été passée
    private boolean isLastStepPassed(EcosSessionEntity sessionEntity) {
        // Récupérer la dernière étape de la session
        EcosSessionProgrammationStepEntity lastStep = sessionEntity
                .getEcosSessionProgrammationEntity()
                .getEcosSessionProgrammationStepEntities()
                .stream()
                .max(Comparator.comparing(EcosSessionProgrammationStepEntity::getDateTime))
                .orElse(null); // si aucune étape n'est trouvée, la variable lastStep sera affectée à null

        // Vérifier si la dernière étape existe et si sa date et heure sont avant maintenant
        return lastStep != null && lastStep.getDateTime().isBefore(LocalDateTime.now());
    }

}
