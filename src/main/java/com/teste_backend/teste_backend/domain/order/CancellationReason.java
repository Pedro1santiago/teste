package com.teste_backend.teste_backend.domain.order;

public final class CancellationReason {

    public static final int MINIMUM_LENGTH = 10;

    private CancellationReason() {
    }

    public static boolean isValid(String text) {
        return text != null && text.strip().length() >= MINIMUM_LENGTH;
    }
}
