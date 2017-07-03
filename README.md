

# Predicting Individual Trip Destinations With Artificial Potential Fields

This reporitory holds the complete source code accompanying the paper ["Predicting Individual Trip Destinations With Artificial Potential Fields"](https://research.vu.nl/en/publications/predicting-individual-trip-destinations-with-artificial-potential), i.e.:
* the population and activity generator ([IDSA"](https://github.com/TNOCS/idsa));
* loading system for third part trajectory dataset ([LGDS](https://github.com/AleZonta/lgds))
* potential field computation
* (2D) real-time visualization of the system

## How to build
The source code is written in Java 8 and uses Apache Maven for project dependencies and settings (see pom.xml). 
It can be built and run using popular Java IDEs such as Eclipse, NetBeans and IntelliJ.

## How to run
Check and substitute in the config-file your own path of the files required by the simulator.
Fill the parameter file with the value you want to test.
Start nl.tno.idsa.viewer.GUI (with the name of the parameter file as a parameter or directly the parameters) to run the experimentation environment.



