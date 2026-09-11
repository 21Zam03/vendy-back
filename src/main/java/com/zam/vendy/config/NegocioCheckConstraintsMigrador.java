package com.zam.vendy.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.zam.vendy.entities.enums.AccentColor;
import com.zam.vendy.entities.enums.Background;
import com.zam.vendy.entities.enums.CatalogLayout;
import com.zam.vendy.entities.enums.Cover;
import com.zam.vendy.entities.enums.Font;
import com.zam.vendy.entities.enums.Radius;

import lombok.RequiredArgsConstructor;

// Corrige los CHECK constraints viejos que ddl-auto:update dejó desactualizados en
// negocio.{accent_color,background,cover,font,radius,catalog_layout}: se generaron la
// primera vez que Hibernate vio cada columna, con la lista de valores del enum de ESE
// momento — ddl-auto:update solo agrega columnas/tablas nuevas, nunca actualiza un
// constraint ya creado, así que cada vez que alguno de estos enums creció (ej. AccentColor
// sumó CUSTOM, Background sumó IMAGEN, Cover sumó IMAGEN y las cabeceras temáticas) el
// check se quedó para siempre con la lista vieja. Resultado real: elegir "Personalizado" en
// color de acento, o "Imagen" en fondo/cabecera, hacía fallar el guardado con
// "Check constraint '...' is violated" (DataIntegrityViolationException) aunque el dato en
// sí era válido para la app.
//
// Por cada columna, si el check existente no incluye TODOS los valores que el enum de Java
// acepta hoy, se elimina — la validación real de qué valores son válidos ya la hacen los
// AttributeConverter/enums en Java (ver entities/enums y entities/converters), así que el
// constraint nunca hace falta recrearlo. Idempotente: si ya no queda ninguno
// desactualizado, no hace nada. Generalizado a propósito (no solo accent_color) para que la
// próxima vez que alguno de estos enums crezca no vuelva a pasar lo mismo.
@Component
@RequiredArgsConstructor
public class NegocioCheckConstraintsMigrador implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(NegocioCheckConstraintsMigrador.class);

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        Map<String, List<String>> valoresVigentesPorColumna = new LinkedHashMap<>();
        valoresVigentesPorColumna.put("accent_color", valores(AccentColor.values(), AccentColor::getValue));
        valoresVigentesPorColumna.put("background", valores(Background.values(), Background::getValue));
        valoresVigentesPorColumna.put("cover", valores(Cover.values(), Cover::getValue));
        valoresVigentesPorColumna.put("font", valores(Font.values(), Font::getValue));
        valoresVigentesPorColumna.put("radius", valores(Radius.values(), Radius::getValue));
        valoresVigentesPorColumna.put("catalog_layout", valores(CatalogLayout.values(), CatalogLayout::getValue));

        valoresVigentesPorColumna.forEach(this::corregirSiDesactualizado);
    }

    private <E> List<String> valores(E[] constantes, java.util.function.Function<E, String> getValue) {
        return List.of(constantes).stream().map(getValue).toList();
    }

    private void corregirSiDesactualizado(String columna, List<String> valoresVigentes) {
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList(
                """
                select cc.CONSTRAINT_NAME as nombre, cc.CHECK_CLAUSE as clausula
                from information_schema.CHECK_CONSTRAINTS cc
                join information_schema.TABLE_CONSTRAINTS tc
                    on tc.CONSTRAINT_SCHEMA = cc.CONSTRAINT_SCHEMA
                    and tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME
                where tc.TABLE_SCHEMA = database()
                    and tc.TABLE_NAME = 'negocio'
                    and cc.CHECK_CLAUSE like ?
                """,
                "%`" + columna + "`%");

        for (Map<String, Object> fila : constraints) {
            String nombre = (String) fila.get("nombre");
            String clausula = ((String) fila.get("clausula")).toLowerCase();
            boolean faltaAlgunValor = valoresVigentes.stream().anyMatch(v -> !clausula.contains(v.toLowerCase()));
            if (faltaAlgunValor) {
                log.warn("Eliminando check constraint desactualizado '{}' en negocio.{} (no incluía todos los valores vigentes)",
                        nombre, columna);
                jdbcTemplate.execute("alter table negocio drop check `" + nombre + "`");
            }
        }
    }
}
