

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




Start nl.tno.idsa.viewer.GUI (no arguments) to run the experimentation environment.

## TODO
1. Configuration file.
2. Guide to adding your own GIS and census data (replacing the example NL data).
3. Code documentation and cleanup.


