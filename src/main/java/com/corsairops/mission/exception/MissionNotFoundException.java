package com.corsairops.mission.exception;

import com.corsairops.shared.exception.HttpResponseException;
import org.springframework.http.HttpStatus;

public class MissionNotFoundException extends HttpResponseException {
    public MissionNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}