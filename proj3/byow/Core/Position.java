package byow.Core;

import java.io.Serial;
import java.io.Serializable;

/** Position is a class with two variables p.x and p.y */
public class Position implements Serializable {
    @Serial
    private static final long serialVersionUID = 114514;
    int x;
    int y;

    public Position(int xPos, int yPos) {
        this.x = xPos;
        this.y = yPos;
    }
}
