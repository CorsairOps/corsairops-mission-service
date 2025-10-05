package com.corsairops.mission.exception;

import com.corsairops.shared.exception.HttpResponseException;
import org.springframework.http.HttpStatus;

public class InvalidMissionDateException extends HttpResponseException {
    public InvalidMissionDateException(String message, HttpStatus status) {
        super(message, status);
    }
}