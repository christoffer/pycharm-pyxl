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

4. Add Python plugin dependency: first install the Python plugin in IntelliJ (`Settings > Plugins`). Then locate `python.jar` in your home directory. The exact location will vary depending on OS and IntelliJ version. For Idea 2016.2 on Linux for example, the file can be found in `$HOME/.IntelliJIdea2016.2/config/plugins/python/lib`. On OSX it would be in ``$HOME/Library/Application Support/IntelliJIdea2016.2/python/lib`. You'll probably figure it out by running `find . -name 'python.jar' | grep python/lib` in your home directory. 

Once located, you need to add the jar file as a dependency to the project. 
`File > Project Structure... > Modules > pycharm-pyxl > Dependencies` 
(Click the green + to add a new dependency)
`> `JARs or directories` (locate python.jar). 

Make sure the Python JAR is above `<Module source>`, and that the scope is set to "Provided".

5. Open this project in IDEA, then right-click on pycharm-pyxl project and choose "Prepare plugin module for development"
at the bottom of context menu. This will generate new jar in the project root which you can add to your PyCharm.

6. Pro-tip - you can launch PyCharm in debug mode to actually debug the plugin.
