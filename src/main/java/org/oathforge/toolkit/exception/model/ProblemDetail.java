package org.oathforge.toolkit.exception.model;

public record ProblemDetail(String timestamp, Integer status, String error, String message, String code, String path) {
}