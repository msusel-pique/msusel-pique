# msusel-qatch
## Introduction
This project is a fork of the QATCH project found from QuthEceSoftEng's [GitHub](https://github.com/AuthEceSoftEng/qatch) and [Website](http://83.212.105.167:8080/OnlineProjectEvaluator/).  

This fork intends to modify QATCH to behave more like a library by modularizing code, introducing maven project structure, removing GUI elements, removing main methods, and having all methods be language and tool agnostic.

Legacy build, config, rulesets, and default model files are left in an archive folder.
___

## Qatch Overview
[Qatch Research Paper](https://www.sciencedirect.com/science/article/pii/S0957417417303883)

(todo)
___

## Features
MSUSEL-Qatch functions as a framework and library and currently supports generation of comparison matrices, quality model derivation, single project evaluation, and multi-project evaluation all in a language agnostic approach.  

While MSUSEL-Qatch provides the library calls necessary for quality analysis tasks, it does not provide a driver. Extend the framework by providing the langugage-specific objects (static-analysis tools). 
___

## Dependencies (pre-requisites)
- Maven
- R (only needed for model derivation)
  - with library 'xlsx'
    - Note: the xlsx is extremely outdated and will be replaced soon
  - with library 'jsonlite'
- Java 8+

## Compiling
- (TODO: add msusel-qatch to the Maven central repository)
1. Clone repository into `<project_root>` folder
2. Run `mvn test` from `<project_root>`. Fix test errors if needed.
3. Run `mvn install` from `<project_root>`. 
msusel-qatch is now available as a dependency to extend in a personal project. 
*(Eventually `mvn deploy` will be used instead.)*
4. qatch currently has one runnable class, `qatch.runnable.ComparisonMatricesGenerator`. 
Run the main method to generate comparison matrices primed for hand-entry based off of a quality model description.
5. For project evaluation, extend the framework in your own language-specific project and call `qatch.runnable.SingleProjectEvaluator.runEvaluator()`.
