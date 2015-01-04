# coding: pyxl

def x_hi():
    pass

def foo():
    yy = 20
    scaley = (yy<5) or (yy>100)

    return (
        <hi>
           <if cond="moo">
                goo
            </if>
            <else>
                zoo
            </else>
            <hi boo="#" id="Zasdf { " " } asdfjkl ">
            </hi>
        </hi>
    )

def unparenthesized_multiline_expression():
    return <hi
        id="foo"
        class="bar"
    ></hi>

def zoo():
    str = "abcdefg"
    return(
        <hi id="foo-footer" class="zoo-bar clearfix">

            <hi>
                {str}
            </hi>

            <hi />
        </hi>
    )

    return ( <select_option value=1>
        One
        </select_option>)

def goo():
    b = True
    return (<hi
        class="string over two lines without backslash
        {'a' if b else ''}"
        prop2="true" nohover="{True}"
        zoo="string with backslash \
            asdfjkl"
        >
        {5+5}
        </hi>
    )
