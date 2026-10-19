package com.rutasur.service;

public final class PoliticaCupos {

    private PoliticaCupos() {
    }
    public static int disponibles(int capacidad, long ocupados) {
        return (int)Math.max(0, Math.min((long)capacidad, capacidad-ocupados));
    }
    public static boolean permiteBloqueo(int capacidad, long ocupados, int solicitados) {
        return capacidad>0 && ocupados>=0 && solicitados>0 && solicitados<=((long)capacidad-ocupados);
    }
}
