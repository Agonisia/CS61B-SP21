package byow.Core;

import byow.TileEngine.TETile;

import java.io.*;

public class Archive implements Serializable {

    @Serial
    private static final long serialVersionUID = 114514;

    private Mondial mondial;
    private TETile[][] world;
    private Player player;
    private Exit exit;

    Archive(Mondial mondial, TETile[][] world, Player player, Exit exit) {
        this.mondial = mondial;
        this.world = world;
        this.player = player;
        this.exit = exit;
    }

    public void save() {
        File file = new File("./save");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileOutputStream fs = new FileOutputStream(file);
                 ObjectOutputStream out = new ObjectOutputStream(fs)) {
                out.writeObject(this.mondial);
                out.writeObject(this.world);
                out.writeObject(this.player);
                out.writeObject(this.exit);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Archive load() {
        File file = new File("./save");

        if (!file.exists()) {
            throw new RuntimeException("Save file not found");
        }

        try (FileInputStream fs = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(fs)) {
            Mondial mondial = (Mondial) in.readObject();
            TETile[][] world = (TETile[][]) in.readObject();
            Player player = (Player) in.readObject();
            Exit exit = (Exit) in.readObject();
            return new Archive(mondial, world, player, exit);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Mondial getMondial() {
        return mondial;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public Exit getExit() {
        return exit;
    }
}
