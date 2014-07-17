package com.christofferklang.pyxl;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.PyTokenTypes;
import com.christofferklang.pyxl.PyxlTokenTypes;
import com.intellij.openapi.util.text.StringUtil;
import java.util.Stack;
import com.intellij.psi.tree.IElementType;


// NOTE: JFlex lexer file is defined in http://www.jflex.de/manual.pdf

%%

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

THREE_APOS = (\'\'\')
ONE_TWO_APOS = ('[^']) | ('\\[^]) | (''[^']) | (''\\[^])
APOS_STRING_CHAR = [^\\'] | {ANY_ESCAPE_SEQUENCE} | {ONE_TWO_APOS}
TRIPLE_APOS_LITERAL = {THREE_APOS} {APOS_STRING_CHAR}* {THREE_APOS}?


// NILS:
S = [\ \t]*
PYXL_ATTRNAME = {IDENT_START}[a-zA-Z0-9_-]** // tag name and attr-name matcher. Supports dashes, which makes it diff than IDENTIFIER
PYXL_ATTR = {PYXL_ATTRNAME}{S}"="(PYXL_ATTRVALUE_2Q|PYXL_ATTRVALUE_1Q){S}
PYXL_TAG = "<" {PYXL_ATTRNAME} // {S}{PYXL_ATTR}*(">"|"/>")
PYXL_TAGCLOSE = "</" ({IDENTIFIER}) ">"
// a string that doesn't contain a {} (e.g. no python embed)
PYXL_STRING_INSIDES = ([^\\\"\r\n]|{ESCAPE_SEQUENCE}|(\\[\r\n]))*?

// attribute value, double-quoted without a python embed
PYXL_ATTRVALUE_2Q = ([^\\\"\r\n{]|{ESCAPE_SEQUENCE}|(\\[\r\n]))*?
PYXL_ATTRVALUE_1Q = ([^\\'\r\n{]|{ESCAPE_SEQUENCE}|(\\[\r\n]))*?

// a quoted string without a python embed
//PYXL_ATTRVALUE1 = '{PYXL_STRING_INSIDES}'
//PYXL_ATTRVALUE2 = \"{PYXL_STRING_INSIDES}\"
// a quoted string with a python embed
//PYXL_QUOTED_PYTHON_EMBED = [\"']{PYXL_PYTHON_EMBED}[\"']
// a normal python embed (with no quotes)
PYXL_PYTHON_EMBED = \{{PYXL_STRING_INSIDES}\}
// a string in a pyxl block, outside tags and quotes (can't contain {}  <> # etc)
PYXL_BLOCK_STRING = ([^<{#])*?

%state IN_PYXL_BLOCK
%state IN_PYXL_COMMENT
%state IN_PYXL_TAG_NAME
%state IN_PYXL_PYTHON_EMBED

%state ATTR_VALUE_1Q
%state ATTR_VALUE_2Q
%state IN_ATTR
%state IN_CLOSE_TAG

%{


private void pushState(Integer state) {
    stateStack.push(state);
}

private Integer popState() {
    return stateStack.pop();
}


int commentStartState = YYINITIAL;
int embedBraceCount = 0;

boolean inpyxltag;

Stack<String> tagStack = new Stack<String>();
Stack<Integer> stateStack = new Stack<Integer>();


private void openTag() {
    tagStack.push("Moo");
    yybegin(IN_PYXL_TAG_NAME);
}

private boolean closeTag() {
    int size = tagStack.size();
    if (size == 1) {
        tagStack.pop();
        yybegin(IN_DOCSTRING_OWNER);    // done with pyxl code.
        return true;
    } else if (size == 0) {
        yybegin(IN_DOCSTRING_OWNER);    // done with pyxl code.
        return false;   // error
    } else {
        tagStack.pop();
        yybegin(IN_PYXL_BLOCK); // back to pyxl-block state
        return true;
    }
}

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

// handle a right brace in a python embed
private IElementType handleRightBrace() {

    if (--embedBraceCount == 0) {
        yybegin(popState());
        return PyxlTokenTypes.EMBED_END;
    } else {
        return PyTokenTypes.RBRACE;
    }
}

%}
%%

[\ ]                        { return PyTokenTypes.SPACE; }
[\t]                        { return PyTokenTypes.TAB; }
[\f]                        { return PyTokenTypes.FORMFEED; }
"\\"                        { return PyTokenTypes.BACKSLASH; }



<IN_PYXL_COMMENT> {
    "-->" { yybegin(commentStartState); return PyTokenTypes.END_OF_LINE_COMMENT; }
    [^\-]|(-[^\-])|(--[^>]) { return PyTokenTypes.END_OF_LINE_COMMENT; }
    [^] { return PyxlTokenTypes.BADCHAR; }
}

<IN_PYXL_PYTHON_EMBED> {
{SINGLE_QUOTED_STRING} { return PyTokenTypes.SINGLE_QUOTED_STRING; }
{TRIPLE_QUOTED_STRING} { return PyTokenTypes.TRIPLE_QUOTED_STRING; }
"{"                     { embedBraceCount++; return PyTokenTypes.LBRACE; }
"}"                   { return handleRightBrace(); }
// remainder of python is defined below in the python states.
}


<IN_PYXL_BLOCK, IN_DOCSTRING_OWNER, YYINITIAL> {

{PYXL_TAG}               { openTag(); yypushback(yylength()-1); return PyxlTokenTypes.TAGBEGIN; }

}

<IN_CLOSE_TAG> {
"if" { return PyxlTokenTypes.IFTAG; }
"else" { return PyxlTokenTypes.ELSETAG; }
{PYXL_ATTRNAME}       { return PyxlTokenTypes.TAGNAME; }
">"                   { yybegin(IN_PYXL_BLOCK); return closeTag() ? PyxlTokenTypes.TAGCLOSE_END : PyxlTokenTypes.BADCHAR; }
.                     { return PyxlTokenTypes.BADCHAR; }
}

<IN_PYXL_BLOCK> {
"<!--" {
    commentStartState = yystate(); // Remember which state we should return to after the comment
    yybegin(IN_PYXL_COMMENT);
    return PyTokenTypes.END_OF_LINE_COMMENT;
}
"{"                   { pushState(IN_PYXL_BLOCK); embedBraceCount++; yybegin(IN_PYXL_PYTHON_EMBED); return PyxlTokenTypes.EMBED_START; }
{PYXL_TAGCLOSE}        { yybegin(IN_CLOSE_TAG); yypushback(yylength()-2); return PyxlTokenTypes.TAGCLOSE_START; }
{END_OF_LINE_COMMENT}       { return PyTokenTypes.END_OF_LINE_COMMENT; }
{PYXL_BLOCK_STRING}   { return PyxlTokenTypes.STRING; }
.                       { return PyxlTokenTypes.BADCHAR; }


}

<ATTR_VALUE_1Q> {
"'" { yybegin(IN_ATTR); return PyxlTokenTypes.ATTRVALUE_END; }  // end of attribute value
{PYXL_ATTRVALUE_1Q} { return PyxlTokenTypes.ATTRVALUE;}
"{"                   { pushState(ATTR_VALUE_1Q); embedBraceCount++; yybegin(IN_PYXL_PYTHON_EMBED); return PyxlTokenTypes.EMBED_START; }
. { return PyxlTokenTypes.BADCHAR;}
}

<ATTR_VALUE_2Q> {
"\"" { yybegin(IN_ATTR); return PyxlTokenTypes.ATTRVALUE_END;}  // end of attribute value
{PYXL_ATTRVALUE_2Q} { return PyxlTokenTypes.ATTRVALUE;}
"{"                   { pushState(ATTR_VALUE_2Q);embedBraceCount++; yybegin(IN_PYXL_PYTHON_EMBED); return PyxlTokenTypes.EMBED_START; }
[^] { return PyxlTokenTypes.BADCHAR;}

}

<IN_ATTR> { // parse an attribute name and value
{PYXL_ATTRNAME}       { return PyxlTokenTypes.ATTRNAME; }
"="                   { return PyTokenTypes.EQ; }
"'" { yybegin(ATTR_VALUE_1Q); return PyxlTokenTypes.ATTRVALUE_START; }
"\"" { yybegin(ATTR_VALUE_2Q); return PyxlTokenTypes.ATTRVALUE_START; }

// python embed without quotes -- should we really return here after this? Or is only a single value possible?
"{"                 { pushState(IN_ATTR); embedBraceCount++; yybegin(IN_PYXL_PYTHON_EMBED); return PyxlTokenTypes.EMBED_START; }
">"                 { yybegin(IN_PYXL_BLOCK); return PyxlTokenTypes.TAGEND;}
"/>"                    { return closeTag() ? PyxlTokenTypes.TAGENDANDCLOSE : PyxlTokenTypes.BADCHAR; }

. { return PyxlTokenTypes.BADCHAR; }
}

<IN_PYXL_TAG_NAME> { // parse a tag name
//">"                     {  yybegin(IN_PYXL_BLOCK); return PyxlTokenTypes.TAGEND; }
"if" { yybegin(IN_ATTR); return PyxlTokenTypes.IFTAG; }
"else" { yybegin(IN_ATTR); return PyxlTokenTypes.ELSETAG; }

{PYXL_ATTRNAME}       { yybegin(IN_ATTR); return PyxlTokenTypes.TAGNAME; }
//{PYXL_ATTRVALUE1} { return PyxlTokenTypes.ATTRVALUE; }
//{PYXL_ATTRVALUE2} { return PyxlTokenTypes.ATTRVALUE; }
.                       { return PyxlTokenTypes.BADCHAR; }

}

<IN_DOCSTRING_OWNER> {
":"(\ )*{END_OF_LINE_COMMENT}?"\n"          { yypushback(yylength()-1); yybegin(PENDING_DOCSTRING); return PyTokenTypes.COLON; }
}

<PENDING_DOCSTRING> {
{SINGLE_QUOTED_STRING}          { if (zzInput == YYEOF) return PyTokenTypes.DOCSTRING;
                                 else yybegin(YYINITIAL); return PyTokenTypes.SINGLE_QUOTED_STRING; }
{TRIPLE_QUOTED_STRING}          { if (zzInput == YYEOF) return PyTokenTypes.DOCSTRING;
                                 else yybegin(YYINITIAL); return PyTokenTypes.TRIPLE_QUOTED_STRING; }
{DOCSTRING_LITERAL}[\ \t]*[\n;]   { yypushback(getSpaceLength(yytext())); yybegin(YYINITIAL); return PyTokenTypes.DOCSTRING; }
{DOCSTRING_LITERAL}[\ \t]*"\\"  { yypushback(getSpaceLength(yytext())); return PyTokenTypes.DOCSTRING; }

.                               { yypushback(1); yybegin(YYINITIAL); }
}


<YYINITIAL> {
[\n]                        { if (zzCurrentPos == 0) yybegin(PENDING_DOCSTRING); return PyTokenTypes.LINE_BREAK; }
{END_OF_LINE_COMMENT}       { if (zzCurrentPos == 0) yybegin(PENDING_DOCSTRING); return PyTokenTypes.END_OF_LINE_COMMENT; }

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
 yybegin(PENDING_DOCSTRING); return PyTokenTypes.DOCSTRING; }

{TRIPLE_QUOTED_STRING}[\ \t]*"\\"  {
 yypushback(getSpaceLength(yytext())); if (zzCurrentPos != 0) return PyTokenTypes.TRIPLE_QUOTED_STRING;
 yybegin(PENDING_DOCSTRING); return PyTokenTypes.DOCSTRING; }

}

[\n]                        { return PyTokenTypes.LINE_BREAK; }


<YYINITIAL, IN_DOCSTRING_OWNER, IN_PYXL_PYTHON_EMBED> {
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
