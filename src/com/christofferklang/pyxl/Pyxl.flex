package com.christofferklang.pyxl;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.christofferklang.pyxl.psi.PyxlTypes;
import com.intellij.psi.TokenType;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.PyTokenTypes;
import com.intellij.openapi.util.text.StringUtil;
import java.util.Stack;

%%

%class PyxlLexer
%implements FlexLexer
%line
%unicode
%function advance
%type IElementType

%{
// Keep track of tag nesting level
private int tagLevel = 0;
private String tagName = null;

private int yyline = 0;
Stack<String> tagStack = new Stack<String>();

%}

Identifier = [a-zA-Z_][a-zA-Z_0-9]*
AttributeName = [a-zA-Z_][a-zA-Z\-0-9]*
TagHead = "<" {Identifier}
QuotedValue = \"[^\"\r\n]*\"

LineBreak = \r|\n|\r\n
WhiteSpace = {LineBreak} | [ \t\f]

DIGIT = [0-9]
NONZERODIGIT = [1-9]
OCTDIGIT = [0-7]
HEXDIGIT = [0-9A-Fa-f]
BINDIGIT = [01]

HEXINTEGER = 0[Xx]({HEXDIGIT})+
OCTINTEGER = 0[Oo]?({OCTDIGIT})+
BININTEGER = 0[Bb]({BINDIGIT})+
DECIMALINTEGER = (({NONZERODIGIT}({DIGIT})*)|0)
INTEGER = {DECIMALINTEGER}|{OCTINTEGER}|{HEXINTEGER}|{BININTEGER}
LONGINTEGER = {INTEGER}[Ll]

END_OF_LINE_COMMENT="#"[^\r\n]*

IDENT_START = [a-zA-Z_]|[:unicode_uppercase_letter:]|[:unicode_lowercase_letter:]|[:unicode_titlecase_letter:]|[:unicode_modifier_letter:]|[:unicode_other_letter:]|[:unicode_letter_number:]
IDENT_CONTINUE = [a-zA-Z0-9_]|[:unicode_uppercase_letter:]|[:unicode_lowercase_letter:]|[:unicode_titlecase_letter:]|[:unicode_modifier_letter:]|[:unicode_other_letter:]|[:unicode_letter_number:]|[:unicode_non_spacing_mark:]|[:unicode_combining_spacing_mark:]|[:unicode_decimal_digit_number:]|[:unicode_connector_punctuation:]
IDENTIFIER = {IDENT_START}{IDENT_CONTINUE}**

FLOATNUMBER=({POINTFLOAT})|({EXPONENTFLOAT})
POINTFLOAT=(({INTPART})?{FRACTION})|({INTPART}\.)
EXPONENTFLOAT=(({INTPART})|({POINTFLOAT})){EXPONENT}
INTPART = ({DIGIT})+
FRACTION = \.({DIGIT})+
EXPONENT = [eE][+\-]?({DIGIT})+

IMAGNUMBER=(({FLOATNUMBER})|({INTPART}))[Jj]

//STRING_LITERAL=[UuBb]?({RAW_STRING}|{QUOTED_STRING})
//RAW_STRING=[Rr]{QUOTED_STRING}
//QUOTED_STRING=({TRIPLE_APOS_LITERAL})|({QUOTED_LITERAL})|({DOUBLE_QUOTED_LITERAL})|({TRIPLE_QUOTED_LITERAL})

SINGLE_QUOTED_STRING=[UuBbCcRr]{0,2}({QUOTED_LITERAL} | {DOUBLE_QUOTED_LITERAL})
TRIPLE_QUOTED_STRING=[UuBbCcRr]{0,2}[UuBbCcRr]?({TRIPLE_QUOTED_LITERAL}|{TRIPLE_APOS_LITERAL})

DOCSTRING_LITERAL=({SINGLE_QUOTED_STRING}|{TRIPLE_QUOTED_STRING})

QUOTED_LITERAL="'" ([^\\\'\r\n] | {ESCAPE_SEQUENCE} | (\\[\r\n]))* ("'"|\\)?
DOUBLE_QUOTED_LITERAL=\"([^\\\"\r\n]|{ESCAPE_SEQUENCE}|(\\[\r\n]))*?(\"|\\)?
ESCAPE_SEQUENCE=\\[^\r\n]

ANY_ESCAPE_SEQUENCE = \\[^]

THREE_QUO = (\"\"\")
ONE_TWO_QUO = (\"[^\"]) | (\"\\[^]) | (\"\"[^\"]) | (\"\"\\[^])
QUO_STRING_CHAR = [^\\\"] | {ANY_ESCAPE_SEQUENCE} | {ONE_TWO_QUO}
TRIPLE_QUOTED_LITERAL = {THREE_QUO} {QUO_STRING_CHAR}* {THREE_QUO}?

PYXL_COMMENT_CHAR = [^-] | "-" [^-] | "--" [^>]
PYXL_COMMENTED_CONTENT = "<!--" {PYXL_COMMENT_CHAR}* "-->"

THREE_APOS = (\'\'\')
ONE_TWO_APOS = ('[^']) | ('\\[^]) | (''[^']) | (''\\[^])
APOS_STRING_CHAR = [^\\'] | {ANY_ESCAPE_SEQUENCE} | {ONE_TWO_APOS}
TRIPLE_APOS_LITERAL = {THREE_APOS} {APOS_STRING_CHAR}* {THREE_APOS}?

%state PENDING_DOCSTRING
%state IN_DOCSTRING_OWNER

%state PARSE_TAG_HEAD, PARSE_TAG_CONTENT, PARSE_TAG_TAIL, IN_PYTHON

%{
private int getSpaceLength(CharSequence string) {
    String string1 = string.toString();
    string1 = StringUtil.trimEnd(string1, "\\");
    string1 = StringUtil.trimEnd(string1, ";");
    final String s = StringUtil.trimTrailing(string1);
    return yylength()-s.length();
}

private void openTag(String tagName) {
    yybegin(PARSE_TAG_CONTENT);
    tagStack.push(tagName);
}

%}

%%

<YYINITIAL> {
    {END_OF_LINE_COMMENT}       { if (zzCurrentPos == 0) yybegin(PENDING_DOCSTRING); return PyxlTypes.COMMENT; }

    {SINGLE_QUOTED_STRING}          {
        if (zzInput == YYEOF && zzStartRead == 0)
            return PyxlTypes.PYTHON_TEXT;
        else return PyxlTypes.PYTHON_TEXT;
    }

    {TRIPLE_QUOTED_STRING} {
        if (zzInput == YYEOF && zzStartRead == 0)
            return PyxlTypes.PYTHON_TEXT;
        else return PyxlTypes.PYTHON_TEXT;
    }

    {SINGLE_QUOTED_STRING}[\ \t]*[\n;] {
        yypushback(getSpaceLength(yytext()));
        if (zzCurrentPos != 0) return PyxlTypes.PYTHON_TEXT;
        return PyxlTypes.PYTHON_TEXT;
    }

    {TRIPLE_QUOTED_STRING}[\ \t]*[\n;] {
        yypushback(getSpaceLength(yytext()));
        if (zzCurrentPos != 0) return PyxlTypes.PYTHON_TEXT;
        return PyxlTypes.PYTHON_TEXT;
    }

    {SINGLE_QUOTED_STRING}[\ \t]*"\\" {
        yypushback(getSpaceLength(yytext()));
        if (zzCurrentPos != 0) return PyxlTypes.PYTHON_TEXT;
        yybegin(PENDING_DOCSTRING); return PyxlTypes.PYTHON_TEXT;
    }

    {TRIPLE_QUOTED_STRING}[\ \t]*"\\" {
        yypushback(getSpaceLength(yytext()));
        if (zzCurrentPos != 0) return PyxlTypes.PYTHON_TEXT;
        yybegin(PENDING_DOCSTRING); return PyxlTypes.PYTHON_TEXT;
    }
}

{END_OF_LINE_COMMENT}       { return PyxlTypes.COMMENT; }

<YYINITIAL, IN_DOCSTRING_OWNER> {
    {WhiteSpace}+ { return PyxlTypes.PYTHON_TEXT; }
    {TagHead} {
        yypushback(yylength());
        yybegin(PARSE_TAG_HEAD);
    }
}

<IN_DOCSTRING_OWNER> {
    ":"(\ )*{END_OF_LINE_COMMENT}?"\n"          {
        yypushback(yylength()-1);
        yybegin(PENDING_DOCSTRING);
        return PyxlTypes.PYTHON_TEXT;
    }
}

<PENDING_DOCSTRING> {
    {SINGLE_QUOTED_STRING} {
        if (zzInput == YYEOF) return PyxlTypes.PYTHON_TEXT;
        else yybegin(YYINITIAL); return PyxlTypes.PYTHON_TEXT;
    }

    {TRIPLE_QUOTED_STRING} {
        if (zzInput == YYEOF) return PyxlTypes.PYTHON_TEXT;
        else yybegin(YYINITIAL); return PyxlTypes.PYTHON_TEXT;
    }

    {DOCSTRING_LITERAL}[\ \t]*[\n;] {
        yypushback(getSpaceLength(yytext()));
        yybegin(YYINITIAL);
        return PyxlTypes.PYTHON_TEXT;
    }

    {DOCSTRING_LITERAL}[\ \t]*"\\" {
        yypushback(getSpaceLength(yytext()));
        return PyxlTypes.PYTHON_TEXT;
    }

    . {
        yypushback(1); yybegin(YYINITIAL);
    }
}

<PARSE_TAG_HEAD> {
    "<" {
        return PyxlTypes.LEFT_ANGLE;
    }

    ">" {
        openTag(tagName);
        return PyxlTypes.RIGHT_ANGLE;
    }

    {Identifier} {
        tagName = yytext().toString();
        return PyxlTypes.IDENTIFIER;
    }
    {AttributeName} "=" {
        yypushback(1);
        return PyxlTypes.ATTR_NAME;
    }

    {QuotedValue} {
        return PyxlTypes.QUOTED_VALUE;
    }

    "/>" {
        yybegin(tagStack.size() == 0 ? YYINITIAL : PARSE_TAG_CONTENT);
        return PyxlTypes.SELF_CLOSE_END;
    }

    "=" {
        return PyxlTypes.EQ;
    }


}

<PARSE_TAG_CONTENT> {
    "<" {
        return PyxlTypes.LEFT_ANGLE;
    }

    "<" {Identifier} {
          yypushback(yylength());
          yybegin(PARSE_TAG_HEAD);
    }

    "</" {
        yybegin(PARSE_TAG_TAIL);
        return PyxlTypes.START_OF_CLOSE_TAG;
    }

    [^<]+ {
        return PyxlTypes.TEXT;
    }
}

<PARSE_TAG_CONTENT, YYINITIAL, IN_DOCSTRING_OWNER> {
    {PYXL_COMMENTED_CONTENT} {
        return PyxlTypes.COMMENT;
    }
}

<PARSE_TAG_TAIL> {
    ">" {
        tagStack.pop();
        yybegin(tagStack.size() == 0 ? YYINITIAL : PARSE_TAG_CONTENT);
        return PyxlTypes.RIGHT_ANGLE;
    }

    {Identifier} {
        tagName = yytext().toString();
        if(tagStack.size() == 0 || !tagName.equals(tagStack.peek())) {
            return TokenType.ERROR_ELEMENT;
        }

        return PyxlTypes.IDENTIFIER;
    }

    {WhiteSpace} { return PyxlTypes.WHITE_SPACE; }
    [^] { return TokenType.BAD_CHARACTER; }
}

{WhiteSpace}+ { return PyxlTypes.WHITE_SPACE; }
[^] { return PyxlTypes.PYTHON_TEXT; }
