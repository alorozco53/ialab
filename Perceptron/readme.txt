******** Inteligencia Artificial ************
*************** Práctica 9 ******************
******** Albert Manuel Orozco Camacho *******
*************** 41308026-0 ******************

La práctica fue hecha en python. En la carpeta bin/ están los archivos
.py que corresponden a los scripts.

En la carpeta train/ se encuentran los archivos con los datos de entrenamiento.
Cada uno está identificado con la compuerta lógica que entrenan.
El archivo andor.txt entrena algo que sería tanto AND como OR. El orden
especificado en los archivo es el mismo que el establecido en el PDF
de la práctica pero sólo con separación de comas y saltos de línea por
cada operación. (La primera columna corresponde al sesgo)

Como en el conjunto de entrenamiento, están especificadas las salidas,
es irrelevante especificar qué compuerta va a aprender el perceptrón

Después de haber entrenado al perceptrón, se solicita al usuario
dos números para hacer consultas. Estos deben ser dados separados por una
coma, por ejemplo:
0,1

Para terminar el programa se hace CTRL-C.

El programa recibe un argumento:

-t <ruta hacia el archivo con el conjunto de entrenamiento>

Para correr el programa, hay que ejecutar algo del estilo:

$ python bin/rNeuronal.py -t train/and0.txt
