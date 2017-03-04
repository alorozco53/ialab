import processing.core.PApplet;
import processing.core.PFont;

import java.util.Stack;
import java.util.Random;
import java.lang.Math;
import java.util.Scanner;

/**
 *
 * @author AlOrozco53
 */
public class Main extends PApplet {

    PFont fuente;  // Fuente para mostrar texto en pantalla
    
    // Propiedades del canvas
    int alto = 200;
    int ancho = 250;
    int celda = 4;

    // Default size for each cell while drawn in the canvas
    final int CS = 20;
    
    final int redValue = 0;
    final int greenValue = 1;
    final int blueValue = 2;
    
    final int[] DEFAULT_LINE_COLOR = new int[] {0, 0, 0};
    final int[] DEFAULT_BACKGROUND_COLOR = new int[] {255, 255, 255};
    final int[] BLUE = new int[] {0, 0, 255};
    final int[] RED = new int[] {255, 0, 0};

    // Default values for the grid's size
    final int gridWidth = 45;
    final int gridHeight = 30;
    
    // The grid
    Grid cuadricula;

    // Flag that is true only when initializing the algorithm
    boolean start;

    // Flag that indicates whether the program has finished performing the main algorithm
    boolean done;

    Random rnd;
    Stack<Cell> cellStack;
    Cell current;


    /** Sets up the canvas' size **/
    public void settings() {
        size(ancho*celda, (alto*celda)+32);
    }

    /** Initializes all the global variables and colors the background white **/
    @Override
    public void setup() {
	rnd = new Random();
        background(DEFAULT_BACKGROUND_COLOR[redValue],
		   DEFAULT_BACKGROUND_COLOR[greenValue],
		   DEFAULT_BACKGROUND_COLOR[blueValue]);
        fuente = createFont("Arial", 12, true);
	stroke(DEFAULT_LINE_COLOR[redValue],
	       DEFAULT_LINE_COLOR[greenValue],
	       DEFAULT_LINE_COLOR[blueValue]);
	cuadricula = new Grid(gridWidth, gridHeight);
	cellStack = new Stack<Cell>();
	start = true;
	done = false;
    }


    /** 
     * Draws each step of the algorithm according to what is done by the user
     **/
    @Override
    public void draw() {
	if(!done)
	    cuadricula.drawGrid();
	else {
	    for(int i = 0; i < gridWidth; i++)
		for(int j = 0; j < gridHeight; j++)
		    cuadricula.grid[i][j].color = DEFAULT_BACKGROUND_COLOR;
	    cuadricula.drawGrid();
	}
    }


    /**
     * Randomly constructs the labyrinth by pressing the ENTER key or
     * RETURN (in MAC OS)
     **/
    @Override
    public void keyPressed() {
	if(key == ENTER || key == RETURN) {
	    if(!done) {
		if(start) {
		    int initX = rnd.nextInt(gridWidth);
		    int initY = rnd.nextInt(gridHeight);
		    current = cuadricula.mazeStepGeneration(cuadricula.grid[initX][initY],
							    cellStack);
		    start = false;
		} else
		    current = cuadricula.mazeStepGeneration(current, cellStack);
	    }
	}
    }
    
    /**
     * Class that abstracts the needed features to draw a line
     */
    class Line {
	// The following variables are used to store this line's position in the canvas
	int x1;
	int y1;
	int x2;
	int y2;
	
	// Variable that decides if this line is transparent
	boolean alpha;

	/**
	 * Constructor that takes four parameters
	 */
	Line(int a, int b, int c, int d) {
	    this.x1 = a;
	    this.y1 = b;
	    this.x2 = c;
	    this.y2 = d;
	    
	    // By default, the current line is not transparent
	    this.alpha = false;
	}

	/**
	 * Draws a line in the canvas
	 **/
	void drawLine() {
	    if(!alpha)
		line(x1, y1, x2, y2);
	    else {
		stroke(DEFAULT_LINE_COLOR[redValue],
		       DEFAULT_LINE_COLOR[greenValue],
		       DEFAULT_LINE_COLOR[blueValue], 0);
		line(x1, y1, x2, y2);
		stroke(DEFAULT_LINE_COLOR[redValue],
		       DEFAULT_LINE_COLOR[greenValue],
		       DEFAULT_LINE_COLOR[blueValue]);
	    }
	}
    }

    /**
     * Class that abstracts the needed features to draw a (squared) cell
     */
    class Cell {
	// Variables for the four lines that compose a cell
	Line top;
	Line bottom;
	Line right;
	Line left;
	
	// Variable for this cell's inner color
	int[] color;

	// Position in grid
	int posX;
	int posY;
	
	/**
	 * Constructor that initializes this cell with all required parameters
	 */
	Cell(Line t, Line b, Line r, Line l, int[] col, int x, int y) {
	    this.top = t;
	    this.bottom = b;
	    this.right = r;
	    this.left = l;
	    this.color = col;
	    this.posX = x;
	    this.posY = y;
	}

	/** Draws a cell in the canvas **/
	void drawCell() {
	    noStroke();
	    fill(color[redValue],
	    	 color[greenValue],
	    	 color[blueValue]);
	    rect(top.x1, top.y1, top.x2-top.x1, bottom.y2-top.y2);
	    fill(DEFAULT_BACKGROUND_COLOR[redValue],
	    	 DEFAULT_BACKGROUND_COLOR[greenValue],
	    	 DEFAULT_BACKGROUND_COLOR[blueValue]);
	    stroke(DEFAULT_LINE_COLOR[redValue],
	     	   DEFAULT_LINE_COLOR[greenValue],
	     	   DEFAULT_LINE_COLOR[blueValue]);
	    top.drawLine();
	    bottom.drawLine();
	    right.drawLine();
	    left.drawLine();
	}
    }

    /**
     * Class that abstracts the whole grid
     */
    class Grid {
	Cell[][] grid;
	int width;
	int height;

	/**
	 * Constructor that initializes the grid given the two size parameters.
	 */
	Grid(int n, int m) {
	    this.width = n;
	    this.height = m;
	    this.grid = new Cell[n][m];
	    for(int i = 0; i < width; i++)
		for(int j = 0; j < height; j++)
		    this.grid[i][j] = new Cell(new Line(CS*i, CS*j, (CS*i)+CS, CS*j),
					       new Line(CS*i, (CS*j)+CS, (CS*i)+CS, (CS*j)+CS),
					       new Line((CS*i)+CS, CS*j, (CS*i)+CS, (CS*j)+CS),
					       new Line(CS*i, CS*j, CS*i, (CS*j)+CS),
					       DEFAULT_BACKGROUND_COLOR,
					       i, j);
	}

	/** Draws a grid in the canvas **/
	void drawGrid() {
	    for(int i = 0; i < width; i++)
		for(int j = 0; j < height; j++)
		    grid[i][j].drawCell();
	}

	
	/**
	 * Returns true if it was possible to erase the given wall from the given cell.
	 * The only condition for a wall not to be erased is if it's an external border from the
	 * grid.
	 * @param cellX -- X coordinate of the given cell
	 * @param cellY -- Y coordinate of the given cell
	 * @param wall -- {top:1, bottom:2, right:3, left:4}
	 * @returns boolean -- true iff given wall was erased successfully
	 */
	boolean tearDownTheWall(int cellX, int cellY, int wall) {
	    switch(wall) {
		// top wall
	    case 1:
		if(cellY == 0) {return false;}
		grid[cellX][cellY].top.alpha = true;
		if(cellY - 1 > 0)
		    grid[cellX][cellY-1].bottom.alpha = true;
		break;
		// bottom wall
	    case 2:
		if(cellY == height - 1) {return false;}
		grid[cellX][cellY].bottom.alpha = true;
		if(cellY + 1 < height)
		    grid[cellX][cellY+1].top.alpha = true;
		break;
		// right wall
	    case 3:
		if(cellX == width-1) {return false;}
		grid[cellX][cellY].right.alpha = true;
		if(cellX + 1 < width)
		    grid[cellX+1][cellY].left.alpha = true;
		break;		
		// left wall
	    case 4:
		if(cellX == 0) {return false;}
		grid[cellX][cellY].left.alpha = true;
		if(cellX - 1 > 0)
		    grid[cellX-1][cellY].right.alpha = true;
		break;
	    default: break;
	    }
	    return true;
	}

	/**
	 * Returns a reference to a cell's neighbor given a direction:
	 * 1 - top, 2 - bottom, 3 - right, 4 - left.
	 * Returns null if the asked neighbor doesn't exist
	 * @param cellX -- X coordinate of the given cell
	 * @param cellY -- Y coordinate of the given cell
	 * @param wall -- {top:1, bottom:2, right:3, left:4}
	 * @returns Cell -- given cell's neighbor in the dir direction
	 */
	Cell neighbor(int cellX, int cellY, int dir) {
	    Cell neigh = null;
	    try {
		switch(dir) {
		case 1:
		    neigh = grid[cellX][cellY-1];
		    break;
		case 2:
		    neigh = grid[cellX][cellY+1];
		    break;
		case 3:
		    neigh = grid[cellX+1][cellY];
		    break;
		case 4:
		    neigh = grid[cellX-1][cellY];
		    break;
		default: break;
		}
	    } catch(Exception e) {
		return null;
	    }
	    return neigh;
	}

	/** 
	 * Decides if one can pass to the adjacent cell in the given direction
	 * i.e., it it's not marked (colored) yet.
	 * @param cell -- the current cell
	 * @param dir -- direction intended to move
	 * @returns boolean -- true iff the pointed cell hasn't been visited yet
	 **/
	boolean validDirection(Cell cell, int dir) {
	    boolean valid = true;
	    try {
		valid = neighbor(cell.posX, cell.posY, dir).color.equals(DEFAULT_BACKGROUND_COLOR);
	    } catch(Exception e) {
		return false;
	    }
	    return valid;
	}


	/**
	 * Decides if cell is surrounded by visited cell
	 * (checks only top, down, left, and right cells)
	 * @param cell -- current cell
	 * @returns boolean -- true iff the 4 surrounding cells have been visited already
	 **/
	boolean isEnclosed(Cell cell) {
	    for(int i = 1; i <= 4; i++) {
		Cell neigh = neighbor(cell.posX, cell.posY, i);
		if(neigh != null)
		    if(neigh.color.equals(DEFAULT_BACKGROUND_COLOR))
			return false;
	    }
	    return true;
	}
	
	/**
	 * Performs a single step of the labyrinth generation.
	 * The current cell will be marked with RED while the visited cells will be marked
	 * with BLUE.
	 * @param curr -- the current cell being visited
	 * @param stack -- a stack containing the alrready visited cells
	 * @returns Cell -- the current (colored RED) generated cell
	 */
	Cell mazeStepGeneration(Cell curr, Stack<Cell> stack) {
	    Random rnd = new Random();
	    int dir = 0;
	    Cell next;
	    
	    // Randomly choses a direction to move on, then erases
	    // corresponding wall from the initial cell
	    do
		dir = rnd.nextInt(4) + 1;
	    while(!validDirection(curr, dir));
	    tearDownTheWall(curr.posX, curr.posY, dir);
	    curr.color = BLUE;
	    next = neighbor(curr.posX, curr.posY, dir);
	    next.color = RED;
	    stack.push(curr);
	    while(isEnclosed(next)) {
		try {
		    next.color = BLUE;
		    next = (Cell) stack.pop();
		    next.color = RED;
		    curr = next;
		} catch(Exception e) {
		    // when the stack is finally empty
		    done = true;
		    break;
		}
	    }
	    return next;
	}
    }
    
    static public void main(String args[]) {
        PApplet.main(new String[] { "Main" });
    }
}
