import processing.core.PApplet;
import processing.core.PFont;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 *
 * @author AlOrozco53
 */
public class Localizacion extends PApplet {

    PFont font;
    int cellSize = 60;    // size for each cell in the grid
    int columns = 20;     // the grid's number of columns
    int rows = 10;        // the grid's number of rows

    World world;          // the robot's world

	// five colors for the beliefs
	int[] deg0 = {224, 255, 255};
	int[] deg1 = {187, 255, 255};
	int[] deg2 = {0, 255, 255};
	int[] deg3 = {0, 191, 255};
	int[] deg4 = {30, 144, 255};

    public void settings() {
        size(columns * cellSize, rows * cellSize + 70);
    }


    /** Configuracion inicial */
    @Override
    public void setup(){
        background(50);
        font = createFont("Arial", 12, true);
        textFont(font, 12);

		world = new World(columns, rows, cellSize);
		world.computeDistances();
		world.initializeBeliefs();
		world.startRobot();

    }

	private void drawBeliefs(Cell cell) {
		double bel = cell.belief;
		double max = 0.0;
		for (int x = 0; x < columns; x++)
			for (int y = 0; y < rows; y++)
				if (!world.grid[x][y].content.equals(CellContent.OBSTACLE))
					if (world.grid[x][y].belief > max)
						max = world.grid[x][y].belief;
		stroke(0);
		if (bel < 0.0 && bel < max / 5.0)
			fill(deg0[0], deg0[1], deg0[2]);
		else if (bel >= max / 5.0 && bel < 2 * max / 5.0)
			fill(deg1[0], deg1[1], deg1[2]);
		else if (bel >= 2 * max / 5.0 && bel < 3 * max / 5.0)
			fill(deg2[0], deg2[1], deg2[2]);
		else if (bel >= 3 * max / 5.0 && bel < 4 * max / 5.0)
			fill(deg3[0], deg3[1], deg3[2]);
		else
			fill(deg4[0], deg4[1], deg4[2]);
	}

    /** Dibuja la imagen en cada ciclo */
    @Override
    public void draw(){
		// perform a step of the localization algorithm
		world.markov();

		for (int x = 0; x < columns; x++)
			for (int y = 0; y < rows; y++) {
				switch (world.grid[x][y].content) {
				case ROBOT:
					stroke(0); fill(0, 0, 128); break;
				case OBSTACLE:
					stroke(0); fill(150, 0, 150); break;
				case EMPTY:
					drawBeliefs(world.grid[x][y]);
					break;
				default:
					stroke(0); fill(0);
				}
				rect(x*cellSize, y*cellSize, cellSize, cellSize);
			}
		fill(0);
        rect(0, rows * cellSize, columns *  cellSize, 70);

        fill(0, 0, 128);
        rect(2 * cellSize, rows * cellSize + 10, 20, 20);
        fill(255);
        text("Robot", 2 * cellSize + 30, rows * cellSize + 30);

        fill(150, 0, 150);
        rect(4 * cellSize, rows * cellSize + 10, 20, 20);
        fill(255);
        text("Obstáculos", 4 * cellSize + 30, rows * cellSize + 30);
    }

    public static void main(String args[]) {
        PApplet.main(new String[] { "Localizacion" });
    }
}
