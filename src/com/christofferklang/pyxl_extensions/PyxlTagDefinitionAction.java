package com.christofferklang.pyxl_extensions;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collection;

public class PyxlTagDefinitionAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Editor editor = PlatformDataKeys.EDITOR.getData(actionEvent.getDataContext());
        String currentIdentifier = getPyxlTagUnderCursor(editor);

        if(currentIdentifier == null) {
            return; // no identifier under cursor
        }

        System.out.println("Identifier: " + currentIdentifier);
        final String pyxlClassName = "x_" + currentIdentifier;
        System.out.println("Pyxl class name: " + pyxlClassName);

        Project project = actionEvent.getProject();
        Collection<PyClass> navigationTargets = PyClassNameIndex.find(pyxlClassName, project, false);

        if(navigationTargets.size() == 0) {
            showHintMessage("Pyxl class " + pyxlClassName + " not found.", editor);
            return;
        } else if(navigationTargets.size() > 1) {
            showHintMessage("More than one instance of Pyxl class " + pyxlClassName
                    + " found, this is probably wrong. Jumping to first one.", editor);
        }

        new ArrayList<NavigationItem>(navigationTargets).get(0).navigate(true);
    }

    private void showHintMessage(String htmlMessage, Editor editor) {
        HintManager hm = HintManager.getInstance();
        hm.showInformationHint(editor, StringEscapeUtils.escapeHtml(htmlMessage));
    }

    private String getPyxlTagUnderCursor(Editor editor) {
        if(editor == null) {
            return null; // editor not ready
        }

        final String text = editor.getDocument().getText();
        final int offset = editor.getCaretModel().getOffset();

        if(text.length() == 0) {
            return null;
        }

        char currentChar = text.charAt(offset);

        // Assume that we are inside a pyxl tag.
        // Back up until the opening "<", and abort if our assumption was proven wrong
        // by encountering a closing ">". Also abort if we have whitespace just before the
        // opening bracket.
        int start = offset - 1;
        boolean foundStart = false;
        boolean previousWasWhiteSpace = false;
        while(start > 0) {
            currentChar = text.charAt(start);
            System.out.println("< checking: " + currentChar);
            if(currentChar == '>') {
                foundStart = false;
                System.out.println("found closing tag when looking for start tag");
                break;
            }
            previousWasWhiteSpace = Character.isWhitespace(currentChar);
            foundStart = currentChar == '<';
            if(foundStart) {
                break;
            }
            start--;
        }
        start += 1; // back off one char to drop the '<'

        if(!foundStart) {
            System.out.println("didn't find start, bailing out");
            return null;
        } else if(previousWasWhiteSpace) {
            System.out.println("white space before start tag, bailing out");
            return null;
        }

        int end = start;
        final int editTextLength = text.length();
        boolean foundEnd = false;
        while(end <= editTextLength) {
            currentChar = text.charAt(end);
            System.out.println("> checking: " + currentChar);
            final boolean isClosingTagOpening = currentChar == '/' && text.charAt(end - 1) == '<';
            boolean validEndTagCharacter = Character.isJavaIdentifierPart(currentChar) || isClosingTagOpening;
            foundEnd = !validEndTagCharacter;
            if(foundEnd) {
                break;
            }
            end++;
        }

        if(!foundEnd) {
            System.out.println("Did not find end, bailing out");
            return null;
        }

        String pyxlTagName = text.substring(start, end);
        if(pyxlTagName.startsWith("/")) {
            pyxlTagName = pyxlTagName.substring(1, pyxlTagName.length());
        }

        return pyxlTagName;
    }
}
