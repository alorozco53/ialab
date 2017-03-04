public enum Accion {
    MOVE_UP(10), MOVE_DOWN(10), MOVE_LEFT(10), MOVE_RIGHT(10),
    MOVE_NW(14), MOVE_NE(14), MOVE_SW(14), MOVE_SE(14);
    
    private final int costo;
    Accion(int costo) {
      this.costo = costo;
    }
    int costo() { return costo; }
}

