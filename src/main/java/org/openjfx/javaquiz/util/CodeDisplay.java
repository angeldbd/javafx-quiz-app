package org.openjfx.javaquiz.util;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeDisplay {
    public static CodeArea createCodeArea(String code) {
        CodeArea codeArea = new CodeArea();
        
        codeArea.setEditable(false);
        codeArea.replaceText(code);
        codeArea.getStylesheets().add(CodeDisplay.class.getResource("javaCodePane.css").toExternalForm());
        codeArea.setStyleSpans(0, computeHighlighting(code));
        return codeArea;
    }

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