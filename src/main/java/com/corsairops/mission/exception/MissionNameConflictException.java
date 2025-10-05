package com.corsairops.mission.exception;

import com.corsairops.shared.exception.HttpResponseException;
import org.springframework.http.HttpStatus;

public class MissionNameConflictException extends HttpResponseException {
    public MissionNameConflictException(String message, HttpStatus status) {
        super(message, status);
    }
}