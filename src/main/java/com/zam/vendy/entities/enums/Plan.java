package com.zam.vendy.entities.enums;

// Plan de membresía del negocio (no del Usuario: dos negocios de la misma cuenta,
// si algún día existieran, podrían tener planes distintos). Se asigna a mano en la base
// de datos por el equipo de Vendy — no hay autoservicio de pago todavía, así que ningún
// endpoint público lo deja elegir (ver NegocioService.guardar, que solo lo LEE).
// "nivel" ordena los planes de menor a mayor para poder comparar "requiere al menos X"
// sin depender del orden de declaración (ordinal()), que se rompe si alguien reordena.
public enum Plan {

    GRATIS(0),
    GO(1),
    PREMIUM(2);

    private final int nivel;

    Plan(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

    public boolean alcanza(Plan minimo) {
        return this.nivel >= minimo.nivel;
    }
}
