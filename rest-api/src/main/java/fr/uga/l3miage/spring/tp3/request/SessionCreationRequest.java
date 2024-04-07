package fr.uga.l3miage.spring.tp3.request;

import fr.uga.l3miage.spring.tp3.responses.enums.SessionStatus;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class SessionCreationRequest {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<Long> examsId;
    @Enumerated(EnumType.ORDINAL)
    private SessionStatus status;
    private SessionProgrammationCreationRequest ecosSessionProgrammation;

}
