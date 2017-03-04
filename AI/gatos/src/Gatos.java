import processing.core.PApplet;
import processing.core.PFont;

import java.util.LinkedList;
import java.util.Hashtable;

/**
 *
 * @author Verónica Arriola
 */
public class Gatos extends PApplet {

    PFont fuente;              // Fuente para mostrar texto en pantalla

    int tamanioMosaico = 6;    // Tamanio de cada mosaico cuadrado (en pixeles).
    int profundidad = 0;       // Profundidad a la que se ha expandido el árbol.
    int diametroMaximo = 1;    // Mayor número de estados expandidos a la misma profundidad.

    int anchoImagen;
    int altoImagen;
    int anchoGato, altoGato;           // Dimensiones en pixeles del dibujo de cada gato.
    boolean modificaVentana = false;   // Indica cuando las dimensiones del árbol se han incrementado.
    boolean genera = false;            // Bandera para solicitar la expansión del siguiente nivel.

    static int MARCA1 = 1;             // Número usado en el tablero del gato para marcar al primer jugador.
    static int MARCA2 = 4;             // Se usan int en lugar de short porque coincide con el tamaño de la palabra, el código se ejecuta ligeramente más rápido.
    Gato gatoRaiz;                     // Estado inicial
    LinkedList<Gato> listaAbierta = new LinkedList();  // Nodos en el nivel más profundo que no han sido expandidos.

    // Se puede reducir aún más el espacio de búsqueda si se usa búsqueda en grafos en lugar de árboles, esto llevando un registro de los nodos que ya aparecieron anteriormente.
    // Para incluir esta parte se requiere definir una función hash consistente con equals, es decir, que asigne el mismo código a estados simétricos.
    //Hashtable<Gato, Gato> listaCerrada;

    public void settings() {
        size(100, altoImagen + 50);
    }

    /** Configuracion inicial */
    @Override
    public void setup(){
        anchoGato =  tamanioMosaico * 5;
        altoGato = tamanioMosaico * 10;
        anchoImagen = 100+30;
        altoImagen =  altoGato * 10;
        surface.setResizable(true);
        surface.setSize(anchoImagen + 50, altoImagen + 50);
        background(200);
        fuente = createFont("Arial",12,true);

        gatoRaiz = new Gato();
        listaAbierta.add(gatoRaiz);
    }

    /** Dibuja la imagen en cada ciclo */
    @Override
    public void draw(){
        try{
            if(genera) generaSiguienteNivel();
            if (modificaVentana){
                System.out.println("Profundidad " + profundidad + ':');
                System.out.println("Diámetro máximo " + diametroMaximo + ". Nodos en el último nivel: " + listaAbierta.size());
                anchoImagen = anchoGato * diametroMaximo + 30;
                surface.setSize(anchoImagen, altoImagen + 80);
                background(200);
                modificaVentana = false;
            }
            Gato actual;
            LinkedList<Gato> listaGatos = new LinkedList();
            listaGatos.add(gatoRaiz);
            int profundidadActual = 0;
            while(!listaGatos.isEmpty()) {
                int numGatos = listaGatos.size();  // Todos los gatos a esta profundidad
                int hijosIzquierda = 0; // Descendientes que irán siendo dibujados.
                for(int i = 0; i < numGatos; i++){
                    actual = listaGatos.remove();
                    if(actual.sucesores != null){
                        listaGatos.addAll(actual.sucesores);
                        int numHijos = actual.sucesores.size();
                        for(int h = 0; h < numHijos; h++){
                            line((int)((0.5+i)*anchoGato), altoGato * profundidadActual + 4 * tamanioMosaico,
                              (int)((0.5+hijosIzquierda)*anchoGato), altoGato * (profundidadActual + 1) + tamanioMosaico);
                            hijosIzquierda++;
                        }
                    }
                    dibujaGato(actual, anchoGato * i, altoGato * profundidadActual);
                }
                profundidadActual++;
            }
      
            // Pintar informacion del modelo en la parte inferior de la ventana.
            stroke(0);
            fill(50);
            rect(0, altoImagen, anchoImagen, 50);
            fill(255);
            textFont(fuente,10);
            text("Profundidad: " + profundidad, 5, altoImagen + 12);
            text("Diámetro: " + diametroMaximo, 5, altoImagen + 24);
          // Aqui se puede agregar codigo para imprimir cuantos juegos ganados lleva cada jugador
          // aqui imprimir el numero de rutas posibles generadas
        } catch (Exception e) {/*e.printStackTrace();*/}

    }

    /**
     * Dibuja un gato a partir de las coordenadas indicadas como esquina
     * superior izquierda.
     */
    void dibujaGato(Gato g, int x, int y){
        x += tamanioMosaico;
        y += tamanioMosaico;

        if(g.hayGanador){
            if(g.jugador1){
                fill(255,0,0);
                stroke(155,0,0);
            } else {
                fill(0,200,0);
                stroke(0,100,0);
            }
        } else if (g.tiradas == 9){
            fill(10,10,10);
            stroke(10,10,10);
        } else {
            fill(50);
            stroke(0);
        }

        // Dibuja el tablero
        int ancho = 3 * tamanioMosaico;
        for(int i = 0, y1 = y + tamanioMosaico, x1 = x + tamanioMosaico; i < 2; i++, y1 += tamanioMosaico, x1 += tamanioMosaico) {
            line(x, y1, x + ancho, y1);
            line(x1, y, x1, y + ancho);
        }
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                int jugada = g.tablero[i][j]; 
                if(jugada == 0) continue;
                if(jugada == MARCA1) ellipse(x + tamanioMosaico/2 + (j * tamanioMosaico),
                                      y + tamanioMosaico/2 + (i * tamanioMosaico),
                                      tamanioMosaico/2, tamanioMosaico/2);
                else {
                    int x1 = x + (j * tamanioMosaico);
                    int y1 = y + (i * tamanioMosaico);
                    line(x1, y1, x1 + tamanioMosaico, y1 + tamanioMosaico);
                    line(x1, y1 + tamanioMosaico, x1 + tamanioMosaico, y1);
                }
            }
        }
        fill(50);
        stroke(0);
    }

    /** Indica que se desea expandir el siguiente nivel. */
    @Override
    public void mouseClicked() {
        genera = true;
    }


    /**
     * Clase para representar un estado del juego del gato. 
     * Cada estado sabe cómo generar a sus sucesores.
     */
    class Gato{
        int[][] tablero = new int[3][3];     // Tablero del juego
        Gato padre;                          // Quién generó este estado.
        LinkedList<Gato> sucesores;          // Posibles jugadas desde este estado.
        boolean jugador1 = false;            // Jugador que tiró en este tablero.
        boolean hayGanador = false;          // Indica si la última tirada produjo un ganador.
        int tiradas = 0;                     // Número de casillas ocupadas.

        /** Constructor del estado inicial. */
        Gato() {}

        /** Constructor que copia el tablero de otro gato y el número de tiradas */
        Gato(Gato g){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    tablero[i][j] = g.tablero[i][j];
                }
            }
            tiradas = g.tiradas;
        }

        /** Indica si este estado tiene sucesores expandidos. */
        int getNumHijos(){
            if(sucesores != null) return sucesores.size();
            else return 0;
        }

        /* Función auxiliar.
        * Dada la última posición en la que se tiró y la marca del jugador
        * calcula si esta jugada produjo un ganador y actualiza el atributo correspondiente.
        * 
        * Esta función debe ser lo más eficiente posible para que la generación del árbol no sea demasiado lenta.
        */
        void hayGanador(int x, int y, int marca){
        // Horizontal
            if (tablero[y][(x + 1) % 3] == marca && tablero[y][(x + 2) % 3] == marca) { hayGanador = true; return; }
            // Vertical
            if (tablero[(y + 1) % 3][x] == marca && tablero[(y + 2) % 3][x] == marca) { hayGanador = true; return; }
            // Diagonal
            if((x == 1 && y != 1) || (y == 1 && x!= 1)) return; // No pueden hacer diagonal
            // Centro y esquinas
            if(x == 1 && y == 1){
              // Diagonal \
                if(tablero[0][0] == marca && tablero[2][2] == marca) { hayGanador = true; return; }
                if(tablero[2][0] == marca && tablero[0][2] == marca) { hayGanador = true; return; }
            } else if (x == y){
              // Diagonal \
                if (tablero[(y + 1) % 3][(x + 1) % 3] == marca && tablero[(y + 2) % 3][(x + 2) % 3] == marca) {
		    hayGanador = true;
		    return;
		}
            } else {
              // Diagonal /
                if (tablero[(y + 2) % 3][(x + 1) % 3] == marca && tablero[(y + 1) % 3][(x + 2) % 3] == marca) {
		    hayGanador = true;
		    return;
		}
            }
        }

        /* Función auxiliar.
        * Coloca la marca del jugador en turno para este estado en las coordenadas indicadas.
        * Asume que la casilla está libre.
        * Coloca la marca correspondiente, verifica y asigna la variable si hay un ganador.
        */
        void tiraEn(int x, int y){
            tiradas++;
            int marca = (jugador1) ? MARCA1 : MARCA2;
            tablero[y][x] = marca;
            hayGanador(x,y, marca);
        }

        /**
        * Crea la lista sucesores y agrega a todos los estados que surjen de tiradas válidas.
        * Se consideran tiradas válidas a aquellas en una casilla libre.
        * Además, se optimiza el proceso no agregando estados con jugadas simétricas.
        * Los estados nuevos tendrán una tirada más y el jugador en turno será el jugador contrario.
        */
        LinkedList<Gato> generaSucesores(){
            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
            // Hint: se debe verificar si el estado sigue siendo valido, si lo es, generar a sus sucesores
            // usando una lista ligada. recuerden que deben especificar que jugador jugó. No vayan a  
            // dejar sin padre a los sucesores.
	    if(hayGanador)
		return null;
	    else {
		LinkedList<Gato> sucesores = new LinkedList<Gato>();
		for(int i = 0; i < 3; i++)
		    for(int j = 0; j < 3; j++) {
			Gato sucesorCandidato = new Gato(this);
			sucesorCandidato.padre = this;
			sucesorCandidato.jugador1 = !jugador1;
			if(sucesorCandidato.tablero[j][i] == 0) {
			    sucesorCandidato.tiraEn(i,j);
			    if(!sucesores.contains(sucesorCandidato)) {
				sucesores.add(sucesorCandidato);
			    }
			}
		    }
		this.sucesores = sucesores;
		return sucesores.isEmpty() ? null : sucesores;
	    }
        }


        // ------- *** ------- *** -------
        // Serie de funciones que revisan la equivalencia de estados considerando las simetrías de un cuadrado.
        // ------- *** ------- *** -------
        // http://en.wikipedia.org/wiki/Examples_of_groups#The_symmetry_group_of_a_square_-_dihedral_group_of_order_8
        // ba es reflexion sobre / y ba3 reflexion sobre \.

        /** Revisa si ambos gatos son exactamente el mismo. */
        boolean esIgual(Gato otro){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(tablero[i][j] != otro.tablero[i][j])
			return false;
                }
            }
            return true;
        }

        /** Al reflejar el gato sobre la diagonal \ son iguales (ie traspuesta) */
        boolean esSimetricoDiagonalInvertida(Gato otro){
            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
	    for(int i = 0; i < 3; i++)
		for(int j = 0; j < 3; j++) {
		    if(tablero[j][i] != otro.tablero[i][j])
			return false;
		}
	    return true;
	}

        /** Al reflejar el gato sobre la diagonal / son iguales (ie traspuesta) */
        boolean esSimetricoDiagonal(Gato otro){
            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
	    return tablero[0][0] == otro.tablero[2][2]
		&& tablero[0][1] == otro.tablero[1][2]
		&& tablero[0][2] == otro.tablero[0][2]
		&& tablero[1][0] == otro.tablero[2][1]
		&& tablero[1][1] == otro.tablero[1][1]
		&& tablero[1][2] == otro.tablero[0][1]
		&& tablero[2][0] == otro.tablero[2][0]
		&& tablero[2][1] == otro.tablero[1][0]
		&& tablero[2][2] == otro.tablero[0][0];
        }

        /** Al reflejar el otro gato sobre la vertical son iguales */
        boolean esSimetricoVerticalmente(Gato otro){
            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
	    int flip = 2;
	    for(int i = 0; i < 3; i++) {
		flip = 2;
		for(int j = 0; j < 3; j++) {
		    if(tablero[i][j] != otro.tablero[i][flip])
			return false;
		    flip--;
		}
	    }
	    return true;	    
        }

        /** Al reflejar el otro gato sobre la horizontal son iguales */
        boolean esSimetricoHorizontalmente(Gato otro){
            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
	    int flip = 2;
	    for(int i = 0; i < 3; i++) {
		for(int j = 0; j < 3; j++) {
		    if(tablero[i][j] != otro.tablero[flip][j])
			return false;
		}
		flip--;
	    }
	    return true;	    
        }

        /** Rota el otro tablero 90° en la dirección de las manecillas del reloj. */
        boolean esSimetrico90(Gato otro){
            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
	    int flip = 2;
	    for(int i = 0; i < 3; i++) {
		flip = 2;
		for(int j = 0; j < 3; j++) {
		    if(tablero[i][j] != otro.tablero[flip][i])
			return false;
		    flip--;
		}
	    }
	    return true;
        }

	/** Rota el otro tablero 180° en la dirección de las manecillas del reloj. */
        boolean esSimetrico180(Gato otro){
            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
	    int flip1 = 2, flip2 = 2;
	    for(int i = 0; i < 3; i++) {
		flip2 = 2;
		for(int j = 0; j < 3; j++) {
		    if(tablero[i][j] != otro.tablero[flip1][flip2])
			return false;
		    flip2--;
		}
		flip1--;
	    }
	    return true;   
        }

        /** Rota el otro tablero 270° en la dirección de las manecillas del reloj. */
        boolean esSimetrico270(Gato otro){
            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
	    int flip = 2;
	    for(int i = 0; i < 3; i++) {
		for(int j = 0; j < 3; j++) {
		    if(tablero[i][j] != otro.tablero[j][flip])
			return false;
		}
		flip--;
	    }
	    return true;	    
        }

        /**
        * Indica si dos estados del juego del gato son iguales, considerando simetrías, 
        * de este modo el problema se vuelve manejable.
        */
        @Override
        public boolean equals(Object o){
            Gato otro = (Gato)o;
            if(esIgual(otro)) return true;

            if(esSimetricoDiagonalInvertida(otro)) return true;
            if(esSimetricoDiagonal(otro)) return true;
            if(esSimetricoVerticalmente(otro)) return true;
            if(esSimetricoHorizontalmente(otro)) return true;
            if(esSimetrico90(otro)) return true;
            if(esSimetrico180(otro)) return true;
            if(esSimetrico270(otro)) return true;

            return false;
        }

        /** Devuelve una representación con caracteres de este estado.
        *  Se puede usar como auxiliar al probar segmentos del código. 
        */
        @Override
        public String toString(){
            char simbolo = jugador1 ? 'o' : 'x';
            String gs = "";
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    gs += tablero[i][j] + " ";
                }
                gs += '\n';
            }
            return gs;
        }
    }

    /**
     * Función encargada de generar el siguiente nivel en BFS.
     * Para todos los estados en la lista abierta:
     * los extrae, genera sus sucesores y agrega los estados nuevos al final de la lista.
     */
    void generaSiguienteNivel(){
        // Si ya se alcanzó la profundidad máxima (9 jugadas) no hace nada.
        if(profundidad >= 9) return;

        // Genera sucesores.
        int numGatos = listaAbierta.size();
        for(int i = 0; i < numGatos; i++){
            Gato actual = listaAbierta.remove();
            LinkedList<Gato> sucesores = actual.generaSucesores();
            if(sucesores != null) listaAbierta.addAll(sucesores);
        }

        // Actualiza variables de estado para saber en qué punto se encuentra el proceso
        // y si hay que hacer ajustes al lienzo de dibujo.
        profundidad++;
        numGatos = listaAbierta.size();
        if(numGatos > diametroMaximo) diametroMaximo = numGatos;
        genera = false;
        modificaVentana = true;
    }


    static public void main(String args[]) {
        PApplet.main(new String[] { "Gatos" });
    }

}
