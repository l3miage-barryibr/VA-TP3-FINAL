package fr.uga.l3miage.spring.tp3.exceptions.handlers;
import fr.uga.l3miage.spring.tp3.exceptions.rest.EndSessionRestException;
import fr.uga.l3miage.spring.tp3.exceptions.rest.LastStepNotPassedException;
import fr.uga.l3miage.spring.tp3.exceptions.rest.PreviousStateNotStartedException;
import fr.uga.l3miage.spring.tp3.responses.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({LastStepNotPassedException.class, PreviousStateNotStartedException.class})
    public ResponseEntity<CustomErrorResponse> handleConflictException(RuntimeException ex) {
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .uri("URI")
                .errorMessage(ex.getMessage())
                .sessionStatus("État actuel de la session")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(EndSessionRestException.class)
    public ResponseEntity<CustomErrorResponse> handleNotFoundException(EndSessionRestException ex) {
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .uri("URI")
                .errorMessage(ex.getMessage())
                .sessionStatus(null) // laisser null si la session n'est pas trouvée
                .build();
        return ResponseEntity.notFound().build();
    }
}