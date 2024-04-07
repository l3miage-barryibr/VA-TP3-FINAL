package fr.uga.l3miage.spring.tp3.exceptions.rest;

public class EndSessionRestException extends RuntimeException {
    public EndSessionRestException(String message) {
        super(message);
    }
}