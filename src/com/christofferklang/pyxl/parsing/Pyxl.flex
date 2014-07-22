package com.christofferklang.pyxl.parsing;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.PyTokenTypes;
import com.christofferklang.pyxl.PyxlTokenTypes;
import com.intellij.openapi.util.text.StringUtil;
import java.util.Stack;
import com.intellij.psi.tree.IElementType;


// NOTE: JFlex lexer file is defined in http://www.jflex.de/manual.pdf

%%
// %debug uncomment for verbose output from the lexer
%class PyxlLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%eof{  return;
%eof}

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
PYXL_ENCODING_STRING = "#"{S}"coding:"{S}[^\n\r]*
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

THREE_APOS = (\'\'\')
ONE_TWO_APOS = ('[^']) | ('\\[^]) | (''[^']) | (''\\[^])
APOS_STRING_CHAR = [^\\'] | {ANY_ESCAPE_SEQUENCE} | {ONE_TWO_APOS}
TRIPLE_APOS_LITERAL = {THREE_APOS} {APOS_STRING_CHAR}* {THREE_APOS}?


S = [\ \t\n]*
PYXL_ATTRNAME = {IDENT_START}[a-zA-Z0-9_-]** // tag name and attr-name matcher. Supports dashes, which makes it diff than IDENTIFIER
PYXL_ATTR = {S}{PYXL_ATTRNAME}{S}"="{S}{PYXL_ATTRVALUE}{S}
PYXL_TAG = "<" {PYXL_ATTRNAME}{S}{PYXL_ATTR}*(">"|"/>")
PYXL_TAGCLOSE = "</" ({IDENTIFIER}) ">"
PYXL_COMMENT = "<!--" ([^\-]|(-[^\-])|(--[^>]))* "-->"

// approximate matches (slightly optimistic - can match on some syntax errors) used for looking for tag.
PYXL_ATTRVALUE_LITERAL = (\"|')({PYXL_ATTRVALUE_2Q}|{PYXL_ATTRVALUE_1Q}|{PYXL_PYTHON_EMBED}?)+(\"|')
PYXL_ATTRVALUE = ({PYXL_ATTRVALUE_LITERAL}|(\{.*\}))

// attribute value insides (different for single-quoted and double-quoted strings)
PYXL_ATTRVALUE_2Q = ([^\\\"\r\n{]|{ESCAPE_SEQUENCE}|(\\[\r\n]))*?
PYXL_ATTRVALUE_1Q = ([^\\'\r\n{]|{ESCAPE_SEQUENCE}|(\\[\r\n]))*?

// a normal python embed (with no quotes)
PYXL_PYTHON_EMBED = \{([^\\\r\n]|{ESCAPE_SEQUENCE}|(\\[\r\n]))*?\}
// a string in a pyxl block, outside tags and quotes (can't contain {}  <> # etc)
PYXL_BLOCK_STRING = ([^<{#])*?

%state IN_PYXL_DOCUMENT
%state IN_PYXL_BLOCK
%state IN_PYXL_TAG_NAME
%state IN_PYXL_PYTHON_EMBED

%state ATTR_VALUE_1Q
%state ATTR_VALUE_2Q
%state IN_ATTR
%state IN_CLOSE_TAG

%{
private void enterState(int state) {
    stateStack.push(new State(yystate(), embedBraceCount));
    yybegin(state);
    embedBraceCount = 0;
}
private boolean exitState() {
    int size = stateStack.size();
    if (size <= 0) {
        yybegin(YYINITIAL);
        return false;   // error condition
    } else {
        State mystate = stateStack.pop();
        yybegin(mystate.lexState);
        embedBraceCount = mystate.embedBraceCount;
        return true;
    }
}

// Counter for keeping track of when an embed statment ends, as opposed to when inner braces closes.
int embedBraceCount = 0;

class State {
    public int lexState;
    public int embedBraceCount;

    State (int lexState, int embedBraceCount) {
        this.lexState = lexState;
        this.embedBraceCount = embedBraceCount;
    }
}
Stack<State> stateStack = new Stack<State>();

%}

%state PENDING_DOCSTRING
%state IN_DOCSTRING_OWNER
%{

private int getSpaceLength(CharSequence string) {
String string1 = string.toString();
string1 = StringUtil.trimEnd(string1, "\\");
string1 = StringUtil.trimEnd(string1, ";");
final String s = StringUtil.trimTrailing(string1);
return yylength()-s.length();

}

%}
%%

[\ ]                        { return PyTokenTypes.SPACE; }
[\t]                        { return PyTokenTypes.TAB; }
[\f]                        { return PyTokenTypes.FORMFEED; }
"\\"                        { return PyTokenTypes.BACKSLASH; }

<IN_PYXL_PYTHON_EMBED> {
{SINGLE_QUOTED_STRING} { return PyTokenTypes.SINGLE_QUOTED_STRING; }
{TRIPLE_QUOTED_STRING} { return PyTokenTypes.TRIPLE_QUOTED_STRING; }
"{"                     { embedBraceCount++; return PyTokenTypes.LBRACE; }
"}"                    {   if (embedBraceCount-- == 0) {
                               exitState();
                               return PyxlTokenTypes.EMBED_END;
                           } else {
                               return PyTokenTypes.RBRACE;
                           }
                        }
// remainder of python is defined below in the python states.
}


<IN_PYXL_BLOCK, IN_DOCSTRING_OWNER, IN_PYXL_DOCUMENT, IN_PYXL_PYTHON_EMBED> {
    {PYXL_TAG} {
        enterState(IN_PYXL_TAG_NAME);
        yypushback(yylength()-1);
        return PyxlTokenTypes.TAGBEGIN;
    }
}

<IN_CLOSE_TAG> {
"if" { return PyxlTokenTypes.BUILT_IN_TAG; }
"else" { return PyxlTokenTypes.BUILT_IN_TAG; }
{PYXL_ATTRNAME}       { return PyxlTokenTypes.TAGNAME; }
">"                   { return exitState() ? PyxlTokenTypes.TAGEND : PyxlTokenTypes.BADCHAR; }
.                     { return PyxlTokenTypes.BADCHAR; }
}

<IN_PYXL_BLOCK> {
{PYXL_COMMENT} { return PyTokenTypes.END_OF_LINE_COMMENT; }
"{"                   { enterState(IN_PYXL_PYTHON_EMBED); return PyxlTokenTypes.EMBED_START; }
{PYXL_TAGCLOSE}        { yybegin(IN_CLOSE_TAG); yypushback(yylength()-2); return PyxlTokenTypes.TAGCLOSE; }
{END_OF_LINE_COMMENT}       { return PyTokenTypes.END_OF_LINE_COMMENT; }
{PYXL_BLOCK_STRING}   { return PyxlTokenTypes.STRING; }
.                       { return PyxlTokenTypes.BADCHAR; }

}

<ATTR_VALUE_1Q> {
"'" { exitState(); return PyxlTokenTypes.ATTRVALUE_END; }  // end of attribute value
{PYXL_ATTRVALUE_1Q} { return PyxlTokenTypes.ATTRVALUE;}
"{"                   { enterState(IN_PYXL_PYTHON_EMBED); return PyxlTokenTypes.EMBED_START; }
. { return PyxlTokenTypes.BADCHAR;}
}

<ATTR_VALUE_2Q> {
"\"" { exitState(); return PyxlTokenTypes.ATTRVALUE_END;}  // end of attribute value
{PYXL_ATTRVALUE_2Q} { return PyxlTokenTypes.ATTRVALUE;}
"{"                   { enterState(IN_PYXL_PYTHON_EMBED); return PyxlTokenTypes.EMBED_START; }
[^] { return PyxlTokenTypes.BADCHAR;}

}

<IN_ATTR> { // parse an attribute name and value
{PYXL_ATTRNAME}       { return PyxlTokenTypes.ATTRNAME; }
"="                   { return PyTokenTypes.EQ; }
"'" { enterState(ATTR_VALUE_1Q); return PyxlTokenTypes.ATTRVALUE_START; }
"\"" { enterState(ATTR_VALUE_2Q); return PyxlTokenTypes.ATTRVALUE_START; }

// python embed without quotes -- should we really return here after this? Or is only a single value possible?
"{"                 { enterState(IN_PYXL_PYTHON_EMBED); return PyxlTokenTypes.EMBED_START; }
">"                 { yybegin(IN_PYXL_BLOCK); return PyxlTokenTypes.TAGEND;}
"/>"                { return exitState() ? PyxlTokenTypes.TAGENDANDCLOSE : PyxlTokenTypes.BADCHAR; }
{END_OF_LINE_COMMENT} { return PyTokenTypes.END_OF_LINE_COMMENT; }
. { return PyxlTokenTypes.BADCHAR; }
}

<IN_PYXL_TAG_NAME> { // parse a tag name
//">"                     {  yybegin(IN_PYXL_BLOCK); return PyxlTokenTypes.TAGEND; }
"if" { yybegin(IN_ATTR); return PyxlTokenTypes.BUILT_IN_TAG; }
"else" { yybegin(IN_ATTR); return PyxlTokenTypes.BUILT_IN_TAG; }

{PYXL_ATTRNAME}       { yybegin(IN_ATTR); return PyxlTokenTypes.TAGNAME; }
.                       { return PyxlTokenTypes.BADCHAR; }

}

<IN_DOCSTRING_OWNER> {
":"(\ )*{END_OF_LINE_COMMENT}?"\n"          { yypushback(yylength()-1); enterState(PENDING_DOCSTRING); return PyTokenTypes.COLON; }
}

<PENDING_DOCSTRING> {
{SINGLE_QUOTED_STRING}          { if (zzInput == YYEOF) return PyTokenTypes.DOCSTRING;
                                 else exitState(); return PyTokenTypes.SINGLE_QUOTED_STRING; }
{TRIPLE_QUOTED_STRING}          { if (zzInput == YYEOF) return PyTokenTypes.DOCSTRING;
                                 else exitState(); return PyTokenTypes.TRIPLE_QUOTED_STRING; }
{DOCSTRING_LITERAL}[\ \t]*[\n;]   { yypushback(getSpaceLength(yytext())); exitState(); return PyTokenTypes.DOCSTRING; }
{DOCSTRING_LITERAL}[\ \t]*"\\"  { yypushback(getSpaceLength(yytext())); return PyTokenTypes.DOCSTRING; }

.                               { yypushback(1); exitState(); }
}

// NOTE(christoffer): Must be above YYINITIAL:{END_OF_LINE_COMMENT} as the length is identical, and
// this must match before.
<YYINITIAL> {
    {PYXL_ENCODING_STRING} {    // Look for # coding: pyxl
        if(zzCurrentPos == 0) {
            enterState(IN_PYXL_DOCUMENT);
            return PyTokenTypes.END_OF_LINE_COMMENT;
        }
    }
}

<YYINITIAL, IN_PYXL_DOCUMENT> {
[\n]                        { if (zzCurrentPos == 0) enterState(PENDING_DOCSTRING); return PyTokenTypes.LINE_BREAK; }
{END_OF_LINE_COMMENT}       { if (zzCurrentPos == 0) enterState(PENDING_DOCSTRING); return PyTokenTypes.END_OF_LINE_COMMENT; }

{SINGLE_QUOTED_STRING}          { if (zzInput == YYEOF && zzStartRead == 0) return PyTokenTypes.DOCSTRING;
                                 else return PyTokenTypes.SINGLE_QUOTED_STRING; }
{TRIPLE_QUOTED_STRING}          { if (zzInput == YYEOF && zzStartRead == 0) return PyTokenTypes.DOCSTRING;
                                 else return PyTokenTypes.TRIPLE_QUOTED_STRING; }

{SINGLE_QUOTED_STRING}[\ \t]*[\n;]   { yypushback(getSpaceLength(yytext())); if (zzCurrentPos != 0) return PyTokenTypes.SINGLE_QUOTED_STRING;
return PyTokenTypes.DOCSTRING; }

{TRIPLE_QUOTED_STRING}[\ \t]*[\n;]   { yypushback(getSpaceLength(yytext())); if (zzCurrentPos != 0) return PyTokenTypes.TRIPLE_QUOTED_STRING;
return PyTokenTypes.DOCSTRING; }

{SINGLE_QUOTED_STRING}[\ \t]*"\\"  {
 yypushback(getSpaceLength(yytext())); if (zzCurrentPos != 0) return PyTokenTypes.SINGLE_QUOTED_STRING;
 enterState(PENDING_DOCSTRING); return PyTokenTypes.DOCSTRING; }

{TRIPLE_QUOTED_STRING}[\ \t]*"\\"  {
 yypushback(getSpaceLength(yytext())); if (zzCurrentPos != 0) return PyTokenTypes.TRIPLE_QUOTED_STRING;
 enterState(PENDING_DOCSTRING); return PyTokenTypes.DOCSTRING; }

}

[\n]                        { return PyTokenTypes.LINE_BREAK; }
<YYINITIAL, IN_DOCSTRING_OWNER, PENDING_DOCSTRING, IN_PYXL_DOCUMENT> {
// this rule was for ALL states in python; with Pyxl addition we have to limit it to  python states only.
{END_OF_LINE_COMMENT}       { return PyTokenTypes.END_OF_LINE_COMMENT; }
}

<YYINITIAL, IN_DOCSTRING_OWNER, IN_PYXL_PYTHON_EMBED, IN_PYXL_DOCUMENT> {
{LONGINTEGER}         { return PyTokenTypes.INTEGER_LITERAL; }
{INTEGER}             { return PyTokenTypes.INTEGER_LITERAL; }
{FLOATNUMBER}         { return PyTokenTypes.FLOAT_LITERAL; }
{IMAGNUMBER}          { return PyTokenTypes.IMAGINARY_LITERAL; }

{SINGLE_QUOTED_STRING} { return PyTokenTypes.SINGLE_QUOTED_STRING; }
{TRIPLE_QUOTED_STRING} { return PyTokenTypes.TRIPLE_QUOTED_STRING; }

"and"                 { return PyTokenTypes.AND_KEYWORD; }
"assert"              { return PyTokenTypes.ASSERT_KEYWORD; }
"break"               { return PyTokenTypes.BREAK_KEYWORD; }
"class"               { yybegin(IN_DOCSTRING_OWNER); return PyTokenTypes.CLASS_KEYWORD; }
"continue"            { return PyTokenTypes.CONTINUE_KEYWORD; }
"def"                 { yybegin(IN_DOCSTRING_OWNER); return PyTokenTypes.DEF_KEYWORD; }
"del"                 { return PyTokenTypes.DEL_KEYWORD; }
"elif"                { return PyTokenTypes.ELIF_KEYWORD; }
"else"                { return PyTokenTypes.ELSE_KEYWORD; }
"except"              { return PyTokenTypes.EXCEPT_KEYWORD; }
"finally"             { return PyTokenTypes.FINALLY_KEYWORD; }
"for"                 { return PyTokenTypes.FOR_KEYWORD; }
"from"                { return PyTokenTypes.FROM_KEYWORD; }
"global"              { return PyTokenTypes.GLOBAL_KEYWORD; }
"if"                  { return PyTokenTypes.IF_KEYWORD; }
"import"              { return PyTokenTypes.IMPORT_KEYWORD; }
"in"                  { return PyTokenTypes.IN_KEYWORD; }
"is"                  { return PyTokenTypes.IS_KEYWORD; }
"lambda"              { return PyTokenTypes.LAMBDA_KEYWORD; }
"not"                 { return PyTokenTypes.NOT_KEYWORD; }
"or"                  { return PyTokenTypes.OR_KEYWORD; }
"pass"                { return PyTokenTypes.PASS_KEYWORD; }
"raise"               { return PyTokenTypes.RAISE_KEYWORD; }
"return"              { return PyTokenTypes.RETURN_KEYWORD; }
"try"                 { return PyTokenTypes.TRY_KEYWORD; }
"while"               { return PyTokenTypes.WHILE_KEYWORD; }
"yield"               { return PyTokenTypes.YIELD_KEYWORD; }

{IDENTIFIER}          { return PyTokenTypes.IDENTIFIER; }

"+="                  { return PyTokenTypes.PLUSEQ; }
"-="                  { return PyTokenTypes.MINUSEQ; }
"**="                 { return PyTokenTypes.EXPEQ; }
"*="                  { return PyTokenTypes.MULTEQ; }
"//="                 { return PyTokenTypes.FLOORDIVEQ; }
"/="                  { return PyTokenTypes.DIVEQ; }
"%="                  { return PyTokenTypes.PERCEQ; }
"&="                  { return PyTokenTypes.ANDEQ; }
"|="                  { return PyTokenTypes.OREQ; }
"^="                  { return PyTokenTypes.XOREQ; }
">>="                 { return PyTokenTypes.GTGTEQ; }
"<<="                 { return PyTokenTypes.LTLTEQ; }
"<<"                  { return PyTokenTypes.LTLT; }
">>"                  { return PyTokenTypes.GTGT; }
"**"                  { return PyTokenTypes.EXP; }
"//"                  { return PyTokenTypes.FLOORDIV; }
"<="                  { return PyTokenTypes.LE; }
">="                  { return PyTokenTypes.GE; }
"=="                  { return PyTokenTypes.EQEQ; }
"!="                  { return PyTokenTypes.NE; }
"<>"                  { return PyTokenTypes.NE_OLD; }
"+"                   { return PyTokenTypes.PLUS; }
"-"                   { return PyTokenTypes.MINUS; }
"*"                   { return PyTokenTypes.MULT; }
"/"                   { return PyTokenTypes.DIV; }
"%"                   { return PyTokenTypes.PERC; }
"&"                   { return PyTokenTypes.AND; }
"|"                   { return PyTokenTypes.OR; }
"^"                   { return PyTokenTypes.XOR; }
"~"                   { return PyTokenTypes.TILDE; }
"<"                   { return PyTokenTypes.LT; }
">"                   { return PyTokenTypes.GT; }
"("                   { return PyTokenTypes.LPAR; }
")"                   { return PyTokenTypes.RPAR; }
"["                   { return PyTokenTypes.LBRACKET; }
"]"                   { return PyTokenTypes.RBRACKET; }
"{"                   { return PyTokenTypes.LBRACE; }
"}"                   { return PyTokenTypes.RBRACE; }
"@"                   { return PyTokenTypes.AT; }
","                   { return PyTokenTypes.COMMA; }
":"                   { return PyTokenTypes.COLON; }

"."                   { return PyTokenTypes.DOT; }
"`"                   { return PyTokenTypes.TICK; }
"="                   { return PyTokenTypes.EQ; }
";"                   { return PyTokenTypes.SEMICOLON; }


.                     { return PyTokenTypes.BAD_CHARACTER; }
}

//[\n]                        { return PyTokenTypes.LINE_BREAK; }
.                     { return PyxlTokenTypes.BADCHAR; }
