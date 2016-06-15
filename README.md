Pyxl Extensions for PyCharm
===========================

This PyCharm plugin aims to provide extensions to PyCharm for working with [Pyxl](https://github.com/dropbox/pyxl) files.

Written by [Nils Bunger](https://github.com/nilsbunger), [Robert Kajic](https://github.com/kajic) and [Christoffer Klang](https://github.com/christoffer).

Installation
============

Download the [plugin jar](/pycharm-pyxl.jar?raw=true) and switch over to `PyCharm > Settings > Plugins > Install plugin from disk...` and select the Jar.

## Release 1.1
- Performance fixes
- Add proper support for language level Python syntax (`with as:`, etc).
- Fix xml namespaced attributes
- Add support for `<!DOCTYPE>` and `<![CDATA[ data data ]]>`

## Release 1.2
- PyCharm 2016.1 support

Development
===========

1. Set up plugin development environment - follow [helful guide from Jetbrains](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html)

2. Git clone this repo.

3. Generate lexer file using provided JFlex (provided in repo root):
```
$ java -jar JFlex.jar --skel idea-flex.skeleton --charat src/com/christofferklang/pyxl/parsing/Pyxl.flex
```

4. Open this project in IDEA, then right-click on pycharm-pyxl project and choose "Prepare plugin module for development"
at the bottom of context menu. This will generate new jar in the project root which you can add to your PyCharm.

5. Pro-tip - you can launch PyCharm in debug mode to actually debug the plugin.