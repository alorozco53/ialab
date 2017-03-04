/**
 * We model all the possible directions for the robot
 * to move towards, with this type definition.
 * @author AlOrozco53
 */
public enum Angle {

    N(0, -1, 90.0),   // North
    S(0, 1, 270.0),    // South
    E(1, 0, 0.0),    // East
    W(-1, 0, 180.0),   // West
    NE(1, -1, 45.0),  // North East
    NW(-1, -1, 135.0), // North West
    SE(1, 1, 315.0),   // South East
    SW(-1, 1, 225.0);  // South West

    private final int offsetX;
    private final int offsetY;
	private final double angleValue;

    /**
     * Constructs a new Angle type given its two
     * coordinate offsets
     * @param ox -- X coordinate offset
     * @param oy -- Y coordinate offset
     */
    Angle(int ox, int oy, double av) {
      this.offsetX = ox;
      this.offsetY = oy;
	  this.angleValue = av;
    }

    /**
     * Returns the X coordinate offset
     * @return int -- X coordinate offset
     */
    public int getX() {
	return offsetX;
    }

    /**
     * Returns the Y coordinate offset
     * @return int -- Y coordinate offset
     */
    public int getY() {
	return offsetY;
    }

	/**
	 * Returns the corresponding angle value
	 * @return double -- the angle in degrees
	 */
	public double getAngle() {
		return angleValue;
	}
}
