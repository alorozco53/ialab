import java.util.HashMap;
import java.util.Random;
import java.lang.Math;

/**
 * Class that abstracts and manages the whole world
 * as a grid of Cells.
 * @author AlOrozco53
 */
public class World {

    // the grid
    protected Cell[][] grid;
    protected int columns;
    protected int rows;

    protected int numberObstacles;

    // the following variables store the robot's position in the grid
    protected int robotX;
    protected int robotY;

	// the size of each cell in centimeters
    protected double cellSize;

	// robot measurements
	protected double laser;
	protected Angle direction;
	protected double readTheta;
	protected double odometry;
	protected double noiseOdom;
	protected double noiseDirection;


    /** Constructor that initializes the grid given its sizes.
     * Then, it randomly initializes the state of this World
     * @param c -- number of columns
     * @param r -- number of rows
	 * @param cs -- (geometric) size of the cell
     */
    public World(int c, int r, double cs) {
		Random rand = new Random();

		this.cellSize = cs / 1000.0;
		this.grid = new Cell[c][r];
		this.columns = c;
		this.rows = r;
		this.numberObstacles = 0;

		// randomly chose a location for the robot
		this.robotX = rand.nextInt(c);
		this.robotY = rand.nextInt(r);

		// to initialize the grid, we loop cell by cell and initialize the robot
		// according to the determined position; for all the other cells
		// we toss a biased coin to determine if that cell has an obstacle
		double p = 80.0 / 100.0;
		double coin = 0.0;
		for (int x = 0; x < c; x++)
			for (int y = 0; y < r; y++) {
				if (x == robotX && y == robotY)
					this.grid[x][y] = new Cell(CellContent.ROBOT);
				else {
					coin = rand.nextDouble();
					if (coin <= p)
						this.grid[x][y] = new Cell(CellContent.EMPTY);
					else {
						this.grid[x][y] = new Cell(CellContent.OBSTACLE);
						this.numberObstacles++;
					}
				}
			}
    }

	/**
	 * Initializes all robot variables. We assume that all
	 * the distances and beliefs have been computed
	 */
	public void startRobot() {
		Random rand = new Random();

		// noises
		this.noiseDirection = rand.nextDouble();
		this.noiseOdom = rand.nextDouble();

		// initialize direction
		this.direction = Angle.N;
		this.readTheta = direction.getAngle() * noiseDirection;

		// initialize the laser
		this.laser = grid[robotX][robotY].distances.get(direction) * noiseDirection;

		// initialize odometry
		this.odometry = noiseOdom;
	}

    /**
     * Computes the distance from a given cell to the nearest obstacle
     * (or to the end of the grid) in the direction (angle) given
     * @param cellX -- the given cell's X coordinate
     * @param cellY -- the given cell's Y coordinate
     * @param angle -- direction towards the distance will be computed
     * @return double -- the distance computed
     */
    public double distance(int cellX, int cellY, Angle angle) {
    	if (grid[cellX][cellY].content.equals(CellContent.OBSTACLE))
			return 0.0;
		else {
			int counter = 1;
			int pathX = cellX + angle.getX();
			int pathY = cellY + angle.getY();
			if (pathX < 0 || pathX >= columns || pathY < 0 || pathY >= rows)
				return counter * cellSize;
			while (!grid[pathX][pathY].content.equals(CellContent.OBSTACLE)) {
				pathX += angle.getX();
				pathY += angle.getY();
				counter++;
				if (pathX < 0 || pathX >= columns || pathY < 0 || pathY >= rows)
					break;
			}
			return counter * cellSize;
		}
    }

	/**
	 * Computes the distances from a cell to its
	 * eight nearest obstacles.
     * @param cellX -- the given cell's X coordinate
     * @param cellY -- the given cell's Y coordinate
	 */
    public void cellDistances(int cellX, int cellY) {
		for (Angle angle : Angle.values())
			grid[cellX][cellY].distances.put(angle,
											 distance(cellX, cellY, angle));
    }

	/**
	 * Computes all the distances from each cell in the
	 * grid to its eight obstacles
	 */
    public void computeDistances() {
		for (int x = 0; x < columns; x++)
			for (int y = 0; y < rows; y++)
				cellDistances(x, y);
    }

	/**
	 * Initializes all the cell's beliefs in an uniform way
	 */
	public void initializeBeliefs() {
		double freeCells = (double)((columns * rows) - numberObstacles);
		for (int x = 0; x < columns; x++)
			for (int y = 0; y < rows; y++)
				if (!grid[x][y].content.equals(CellContent.OBSTACLE)) {
					grid[x][y].belief = 1.0 / freeCells;
					// System.out.println(Math.log(grid[x][y].belief));
				}
	}

	/**
	 * Decides if the robot can be moved towards the given
	 * direction. If so, moves the robot randomly
	 * @return boolean -- true iff the robot did actually move
	 */
	public boolean moveRobot() {
		Random rand = new Random();

		// compute new positions randomly
		int newX = -1;
		while (newX < 0 || newX >= columns)
			newX = robotX + direction.getX();
		int newY = -1;
		while (newY < 0 || newY >= rows)
			newY = robotY + direction.getY();
		Cell newPos = grid[newX][newY];

		if (newPos.content.equals(CellContent.EMPTY)) {
			if (rand.nextBoolean()) {
				grid[robotX][robotY].content = CellContent.EMPTY;
				robotX += direction.getX();
				robotY += direction.getY();
				grid[robotX][robotY].content = CellContent.ROBOT;

				// update robot readings
				laser = grid[robotX][robotY].distances.get(direction) * rand.nextDouble();
				direction = Angle.N;
				for (Angle angle : Angle.values())
					if (rand.nextBoolean()) {
						direction = angle;
						break;
					}
				readTheta = direction.getAngle() * noiseDirection;
				odometry += 1 * noiseOdom;
				return true;
			}
			return false;
		} else {
			// update direction to give the robot a chance to move
			direction = Angle.N;
			for (Angle angle : Angle.values())
				if (rand.nextBoolean()) {
					direction = angle;
					break;
				}
			readTheta = direction.getAngle() * noiseDirection;
			return false;
		}
	}

	/**
	 * Performs a step (iteration) in the Markov localization algorithm.
	 * We assume that all the beliefs have been initialized and the distances
	 * have been computed already.
	 */
	public void markov() {
		Random rand = new Random();

		// attempt to move the robot
		boolean moved = moveRobot();
		// update beliefs
		if (!moved) {
			System.out.println("no move");
			double alpha = 0.0;
			for (Angle angle : Angle.values())
				for (int x = 0; x < columns; x++)
					for (int y = 0; y < rows; y++)
						if (!grid[x][y].content.equals(CellContent.OBSTACLE)) {
							double variance = 2 * rand.nextDouble();
							double dist_diff = laser - grid[x][y].distances.get(angle);
							double exp = Math.exp(- Math.pow(dist_diff, 2) / (2 * Math.pow(variance, 2)));
							double prob = (1.0 / (Math.sqrt(2 * Math.PI) * variance)) * exp;
							grid[x][y].belief *= prob;
							System.out.println(grid[x][y].belief);
							alpha += grid[x][y].belief;
						}
			// normalize the belief
			System.out.println("----");
			for (Angle angle : Angle.values())
				for (int x = 0; x < columns; x++)
					for (int y = 0; y < rows; y++)
						if (!grid[x][y].content.equals(CellContent.OBSTACLE)) {
							grid[x][y].belief *= 1.0 / alpha;
							System.out.println(grid[x][y].belief);
						}
		} else {
			System.out.println("the robot moved");
			// update according to direction measurement
			for (Angle angle : Angle.values())
				for (int x = 0; x < columns; x++)
					for (int y = 0; y < rows; y++)
						if (!grid[x][y].content.equals(CellContent.OBSTACLE)) {
							double sum = 0;
							for (Angle ang : Angle.values())
								for (int i = 0; i < columns; i++)
									for (int j = 0; j < rows; j++)
										if (!grid[i][j].content.equals(CellContent.OBSTACLE)) {
											double exp = Math.pow(((ang.getAngle() - direction.getAngle()) - readTheta) /
																  noiseDirection, 2);
											double prob = (1.0 / (2 * Math.PI * noiseDirection)) * Math.exp(exp);
											sum += prob * grid[i][j].belief;
										}
							grid[x][y].belief = sum;
						}
			// update according to odometric measurement
			for (Angle angle : Angle.values())
				for (int x = 0; x < columns; x++)
					for (int y = 0; y < rows; y++)
						if (!grid[x][y].content.equals(CellContent.OBSTACLE)) {
							double sum = 0;
							for (Angle ang : Angle.values())
								for (int i = 0; i < columns; i++)
									for (int j = 0; j < rows; j++)
										if (!grid[i][j].content.equals(CellContent.OBSTACLE)) {
											double radian = Math.toRadians(ang.getAngle());
											double sigmaX = odometry * Math.cos(radian);
											double sigmaY = odometry * Math.sin(radian);
											double exp1 = Math.pow((i + (odometry * Math.cos(radian)) - x) /
																   sigmaX, 1);
											double exp2 = Math.pow((j + (odometry * Math.cos(radian)) - y) /
																   sigmaY, 1);
											double prob = - (1.0 / (2.0 * Math.PI * sigmaX * sigmaY)) *
												Math.exp((-0.5) * (exp1 + exp2));
											System.out.println(Math.exp((-0.5) * (exp1 + exp2)));
											sum += prob * grid[i][j].belief;
										}
							grid[x][y].belief = sum;
						}
		}
	}
}
