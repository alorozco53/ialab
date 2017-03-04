******** Inteligencia Artificial ************
*************** Práctica 8 ******************
******** Albert Manuel Orozco Camacho *******
*************** 41308026-0 ******************

La práctica fue hecha en python. En la carpeta bin/ están los archivos
.py que corresponden a los scripts.

Lo único a destacar es que para obtener la aptitud (fitness) de cada
fenotipo, se calculó el recíproco del número de ataques y se multiplicó
por 100. Si el número de ataques es cero, la aptitud es 101.

El programa recibe dos argumentos:

-n <tamaño del tablero de ajedrez (número de reinas)>
-i <número máximo de iteraciones>

Si no se especifica argumento alguno, por default se ejecuta el problema
de las 8 reinas en 500 iteraciones.

Para correr el programa, hay que ejecutar algo del estilo:

$ python bin/aGeneticos.py -n 11 -i 1000
