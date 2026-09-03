package com.zam.vendy.util;

/**
 * Genera una página HTML mínima con etiquetas Open Graph / Twitter Card para que
 * bots como el de WhatsApp o Facebook (que no ejecutan JavaScript) puedan leer un
 * preview real, y redirige a personas reales hacia la SPA.
 */
public final class OgPreviewHtml {

    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private OgPreviewHtml() {
    }

    public static String build(String title, String description, String targetUrl, String siteName) {
        String safeTitle = escape(blankToDefault(title, siteName));
        String safeDescription = escape(truncate(blankToDefault(description, "Descúbrelo en " + siteName), MAX_DESCRIPTION_LENGTH));
        String safeUrl = escape(targetUrl);
        String safeSiteName = escape(siteName);

        return """
                <!doctype html>
                <html lang="es">
                <head>
                <meta charset="UTF-8" />
                <title>%s</title>
                <meta name="description" content="%s" />
                <meta property="og:type" content="website" />
                <meta property="og:site_name" content="%s" />
                <meta property="og:title" content="%s" />
                <meta property="og:description" content="%s" />
                <meta property="og:url" content="%s" />
                <meta name="twitter:card" content="summary" />
                <meta name="twitter:title" content="%s" />
                <meta name="twitter:description" content="%s" />
                <meta http-equiv="refresh" content="0; url=%s" />
                <script>window.location.replace(%s);</script>
                </head>
                <body>
                <p>Redirigiendo… si no ocurre automáticamente, <a href="%s">haz clic aquí</a>.</p>
                </body>
                </html>
                """.formatted(
                safeTitle, safeDescription, safeSiteName, safeTitle, safeDescription, safeUrl,
                safeTitle, safeDescription, safeUrl, jsStringLiteral(targetUrl), safeUrl);
    }

    private static String blankToDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private static String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 1).trim() + "…";
    }

    private static String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String jsStringLiteral(String value) {
        String escaped = value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("</", "<\\/");
        return "\"" + escaped + "\"";
    }
}
