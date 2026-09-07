package com.zam.vendy.entities.enums;

// Cómo se autenticó un Usuario originalmente. No restringe el método de login: una
// cuenta LOCAL puede vincularse a Google después (ver GoogleAuthService), y desde ahí
// puede entrar por cualquiera de los dos — es solo informativo/de auditoría.
public enum ProveedorAuth {
    LOCAL,
    GOOGLE
}
