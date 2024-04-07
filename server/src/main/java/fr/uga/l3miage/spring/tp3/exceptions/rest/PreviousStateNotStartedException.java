package fr.uga.l3miage.spring.tp3.exceptions.rest;

public class PreviousStateNotStartedException extends RuntimeException {
    public PreviousStateNotStartedException(String message) {
        super(message);
    }
}
