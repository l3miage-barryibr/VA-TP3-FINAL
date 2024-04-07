package fr.uga.l3miage.spring.tp3.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public final class CustomErrorResponse {
    private String uri;
    private String errorMessage;
    private String sessionStatus; // État actuel de la session
}