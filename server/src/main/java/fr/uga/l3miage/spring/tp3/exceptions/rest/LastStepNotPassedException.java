package fr.uga.l3miage.spring.tp3.exceptions.rest;

public class LastStepNotPassedException extends RuntimeException {
    public LastStepNotPassedException(String message) {
        super(message);
    }
}