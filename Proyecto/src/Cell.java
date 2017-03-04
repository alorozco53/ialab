import java.util.HashMap;

/**
 * Class that abstracts the concept of a single cell location in the world.
 * @author AlOrozco53
 */
public class Cell {

    // variable that stores this cell's content, if there is
    protected CellContent content;

    // the following variable stores the 8 distances towards the closest obstacles
    // or 'the end of the world' (map);
    // it is a map from angles to actual distances
    protected HashMap<Angle, Double> distances;

	// stores the degree of belief that the robot is located
	// in the current cell
	protected double belief;

    /**
     * Cell constructor that initializes all global variables
     * @param cont -- new content
     */
    public Cell(CellContent cont) {
		this.content = cont;
		this.distances = new HashMap<Angle, Double>();
		this.belief = 0;
    }
}
