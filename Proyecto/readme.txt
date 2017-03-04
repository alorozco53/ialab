******** Inteligencia Artificial ************
*************** Proyecto ********************
******** Albert Manuel Orozco Camacho *******
*************** 41308026-0 ******************

La práctica fue hecha en java. En la carpeta src/ están los archivos
.java que corresponden a los scripts mientras que en docs/ está la
documentación.

El script principal es Localizacion.java. Aquí se crea un mundo y va
trabajando con él.

La complicación más pesada fue trabajar con las probabilidades de creencia,
ya que dado el algoritmo del PDF, éstas se reducen significativamente
y muchas veces llegan hasta ser NaN.

De ahí que la mayoría de las veces que se corra el programa, el mundo estará
coloreado casi en su totalidad de un mismo color.

En la clase World, el método markov() es el que ejecuta el corazón del algoritmo.
Cada vez que se llama al método draw() de processing, se ejecuta markov() y
éste decide si se va a mover el robot, o no.

Al calcular la distancia entre una celda y su obstáculo más cercano, se tuvo
que normalizar multiplicando la distancia por 1.0 / 1000.0. Esto para evitar la
aparición de algunos NaN.

Las funciones exponenciales negativas fueron las causantes de dichos NaN, pues
en muchas ocasiones, recibían un exponente suficientemente grande.

Para correr el programa, hay que ejecutar algo del estilo:

$ ant
