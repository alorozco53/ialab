******** Inteligencia Artificial ************
*************** Práctica 5 ******************
******** Albert Manuel Orozco Camacho *******
*************** 41308026-0 ******************

La práctica fue hecha en python. En la carpeta bin/ están los archivos
.py que corresponden a los scripts mientras que en states/ están especificados
algunos archivos que corresponden a los estados del jugo de gato.
Cada uno tiene tres líneas con tres carácteres (sin espacios): '-' significa una celda vacía,
'O' el símbolo para el jugador Min, 'X' el símbolo para el jugador Max.

En los archivos state[i].txt, con 0 <= i <= 2 se guardan los tres ejemplos
de prueba propuestos en el pdf.

Dado que el algoritmo puede regresar más de una solución óptima, se escogió
una heurística para satisfacer dichos ejemplos. Sin embargo, es importante
tener esto en cuenta para futuras pruebas.

El programa recibe dos argumentos:

-state <ruta del archivo que guarda el estado a calcular>
-player <'max' o 'min', jugador que va a hacer la movida>

Si no se especifica -player, por default se escoge Max.

Para correr el programa, hay que ejecutar algo del estilo:

$ python bin/minimax.py -state states/state0.txt -player max
