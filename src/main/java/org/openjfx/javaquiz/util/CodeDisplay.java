package org.openjfx.javaquiz.util;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidad para crear áreas de código con syntax highlighting de Java.
 * 
 * Utiliza RichTextFX para mostrar código con colores según sintaxis.
 * El código es de solo lectura (no editable) y aplica estilos CSS automáticamente.
 * 
 * Características:
 * - Highlighting de palabras clave de Java
 * - Área no editable (solo visualización)
 * - Estilos CSS personalizados desde archivo externo
 * - Resaltado automático al crear el área
 * 
 * Palabras clave soportadas:
 * - class, public, private, void, static
 * 
 * Ejemplo de uso:
 * <pre>
 * String javaCode = """
 *     public class Example {
 *         public static void main(String[] args) {
 *             System.out.println("Hello");
 *         }
 *     }
 *     """;
 * 
 * CodeArea codeArea = CodeDisplay.createCodeArea(javaCode);
 * VBox container = new VBox(codeArea);
 * </pre>
 * 
 * @author Angel
 * @version 1.0
 * @since 1.0
 * @see org.fxmisc.richtext.CodeArea
 */
public class CodeDisplay {
    
    /**
     * Crea un CodeArea configurado con syntax highlighting de Java.
     * 
     * El área creada:
     * - No es editable (solo lectura)
     * - Aplica estilos CSS desde /css/javaCodePane.css
     * - Resalta palabras clave automáticamente
     * - Muestra el código formateado
     * 
     * @param code Código Java a mostrar (puede contener saltos de línea)
     * @return CodeArea configurado y listo para agregar a la UI
     * 
     * @example
     * CodeArea display = CodeDisplay.createCodeArea("public class Test { }");
     * scrollPane.setContent(display);
     */
    public static CodeArea createCodeArea(String code) {
        CodeArea codeArea = new CodeArea();
        
        codeArea.setEditable(false);
        codeArea.replaceText(code);
        codeArea.getStylesheets().add(
            CodeDisplay.class.getResource("/org/openjfx/javaquiz/css/javaCodePane.css").toExternalForm()
        );
        codeArea.setStyleSpans(0, computeHighlighting(code));
        
        return codeArea;
    }
    
    /**
     * Calcula los estilos de resaltado de sintaxis para el código Java.
     * 
     * Busca palabras clave mediante expresiones regulares y crea spans de estilo.
     * Las palabras clave se marcan con la clase CSS "keyword".
     * 
     * @param text Código Java a analizar
     * @return StyleSpans con las posiciones y estilos aplicables
     */
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        String[] KEYWORDS = new String[]{"class", "public", "private", "void", "static"};
        String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        
        Pattern pattern = Pattern.compile(KEYWORD_PATTERN);
        Matcher matcher = pattern.matcher(text);
        
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKwEnd = 0;
        
        while (matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}