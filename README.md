# QATCH-min
## Introduction
This project is a fork of the QATCH project found from QuthEceSoftEng's [GitHub](https://github.com/AuthEceSoftEng/qatch) and [Website](http://83.212.105.167:8080/OnlineProjectEvaluator/).  

This fork intends to modify QATCH to behave more like a library by modularizing code, introducing maven project structure, removing GUI elements, removing main methods, and having all methods be language and tool agnostic.

Legacy build, config, rulesets, and default model files are left in an archive folder.
___

## Change Log (newest to oldest)
- Removed tool-specific ant build files and config files
- Removed Web Service directory and files
- Project structure change to match Maven archetype
- Added maven functionality