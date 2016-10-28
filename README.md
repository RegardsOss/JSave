JSave
=====
JSave is a library to read the <i>save</i> format from IDL.

## Installing JSave

### Getting the sources

	$ git clone https://github.com/xxxx
	
### Building the sources

Build the sources using maven

	$ mvn package

## Getting Started

**HOW TO USE JSave**

```java
 JSave save = new JSave(new File("path/to/save/file"));
 save.read();
```

***Display informations***

```java
save.displayFileMetadata();
```
```xml
date--> Thu May 26 11:10:25 2016
os--> linux
release--> 8.2.3
host--> mex-omegj
format--> 11
```

***Retrieving available variables***

```java
save.displayAvailableVariables();
```
```xml
- carte[<type 'class fr.sitools.jsave.Matrix>]496 x 120 x 91
- lati[<type 'class fr.sitools.jsave.Matrix>]496 x 91 x 1
- carte_donnees[<type 'class fr.sitools.jsave.Matrix>]496 x 6 x 91
- longi[<type 'class fr.sitools.jsave.Matrix>]496 x 91 x 1
- wave[<type 'class fr.sitools.jsave.Matrix>]120 x 1 x 1
- solarlongi[<type 'class java.lang.Float>]
```

All variables are store in a HashMap, to get one, use the variable name :

```java
save.getVariables.get("lati"); ==> 125,52
```

Variable with multidimensional (until 3D) data are cast into a Matrix Object. 

```java
save.getVariables.get("carte_donnees"); ==> Matrix
```

to retrieve all data as a flatten array :
```java
save.getVariables.get("carte_donnees").getData(); ==> Array[]
```

to retrieve a column vector as an array along X axis following (y,z)=(1,2): 
```java
save.getVariables.get("carte_donnees").getVectorAlongX(1,2);
```

to retrieve a column vector as an array along Y axis following (x,z)=(1,2): 
```java
save.getVariables.get("carte_donnees").getVectorAlongY(1,2); 
```

to retrieve a column vector as an array along Z axis following (x,y)=(1,2): 
```java
save.getVariables.get("carte_donnees").getVectorAlongZ(1,2); 
```

to retrieve a plane XY as a fatten array for a given z=1 :
```java
Matrix planeXY = save.getVariables.get("carte_donnees").getPlane(1);
```

to get a specific value (x,y)=(3,4) in the previous extracted plane XY :
```java
planeXY.getValueFromPlaneXY(3,4);
```

to retrieve a plane XZ as a fatten array for a given y=1 :
```java
Matrix planeXZ = save.getVariables.get("carte_donnees").getPlaneXZ(1);
```

to get a specific value (x,z)=(3,4) in the previous extracted plane XZ :
```java
planeXZ.getValueFromPlaneXZ(3,4);
```

to retrieve a plane YZ as a fatten array for a given x=1 :
```java
Matrix planeYZ = save.getVariables.get("carte_donnees").getPlaneYZ(1);
```

to get a specific value (y,z)=(3,4) in the previous extracted plane XZ :
```java
planeYZ.getValueFromPlaneYZ(3,4);
```

to get the dim of the matrix:
```java
planeYZ.getShape();
or
planeYZ.getWidth(); planeYZ.getHeight(); planeYZ.getDeep();
```

to get statistics (min/max) the matrix group by deep:
```java
planeYZ.getStats();
```

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Copyright & Credits

All names are listed in alphabetical order

### Development

#### Copyright

This Java code is under Copyright 2016 CNES

#### Main Code

Bastien Fiorito (AKKA), Jean-Christophe Malapert (CNES)

#### Additional Codes

[Stack Overflow](http://stackoverflow.com/questions/15992076/java-3d-array-storing-cube-retreiving-slice-of-cube) from [Aquillo](http://stackoverflow.com/users/1336310/aquillo) under the [creative commons license](https://creativecommons.org/licenses/by-sa/3.0/)

## License

This project is licensed under the GPLV3 License - see the [LICENSE](LICENSE) file for details
 