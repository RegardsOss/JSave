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
- carte[<type 'class fr.sitools.jsave.Matrix>]
- lati[<type 'class fr.sitools.jsave.Matrix>]
- carte_donnees[<type 'class fr.sitools.jsave.Matrix>]
- longi[<type 'class fr.sitools.jsave.Matrix>]
- wave[<type 'class fr.sitools.jsave.Matrix>]
- solarlongi[<type 'class java.lang.Float>]
```

All variables are store in a HashMap, to get one, use the variable name :

```java
save.getVariables.get("lati"); ==> 125,52
```

Variable with multidimensional (until 3D) data are cast into a Matrix Object. 

```java
save.getVariables.get("wave"); ==> Matrix
```

to retrieve all data as a flatten array :
```java
save.getVariables.get("wave").getData(); ==> Array[]
```

to retrieve a column vector : 
```java
save.getVariables.get("wave").getVectorX(1); ==> [5, 4, 9]
```

to retrieve a vector depending on the plan (z) and height (y) :
```java
save.getVariables.get("wave").getVectorYZ(2, 0);
```

to retrieving all data on a specific plan (z) :
```java
save.getVariables.get("wave").getPlan(2); ==> Array[1, 2, 3
                                                    4, 5, 6]
```

