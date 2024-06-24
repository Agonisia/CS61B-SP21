package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Hallway {
    Position start;
    Position end;

    Hallway(Position hallwayStart, Position hallwayEnd) {
        start = hallwayStart;
        end = hallwayEnd;
    }

    /**
     * After all the rooms are generated, draws hallways to connect the rooms.
     * Ensures that all rooms are connected and reachable.
     *
     * @param world The current game map.
     * @param mgp The map generation parameters.
     * @param rooms The list of rooms to be connected.
     */
    public static void generateHallway(TETile[][] world, Mondial mondial,
                                       ArrayList<Room> rooms) {
        ArrayList<Edge> edges = new ArrayList<>();

        // Map to track room indices
        Map<Room, Integer> roomIndices = new HashMap<>();
        for (int i = 0; i < rooms.size(); i++) {
            roomIndices.put(rooms.get(i), i);
        }

        // Create all possible edges between rooms
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                Room room0 = rooms.get(i);
                Room room1 = rooms.get(j);
                Position pos0 = room0.innerPosRandom(mondial);
                Position pos1 = room1.innerPosRandom(mondial);
                edges.add(new Edge(pos0, pos1, getDistance(pos0, pos1), room0, room1));
            }
        }

        // Sort edges by distance
        Collections.sort(edges, Comparator.comparingDouble(e -> e.distance));

        UnionFind uf = new UnionFind(rooms.size());

        // Kruskal's algorithm to create MST
        for (Edge edge : edges) {
            int root1 = uf.find(roomIndices.get(edge.startRoom));
            int root2 = uf.find(roomIndices.get(edge.endRoom));

            if (root1 != root2) {
                uf.union(root1, root2);
                connectRooms(world, edge.start, edge.end);
            }
        }
    }

    private static double getDistance(Position p1, Position p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private static void connectRooms(TETile[][] world, Position start, Position end) {
        if (start.x == end.x) {
            generateVerticalHallway(world, start, end);
        } else if (start.y == end.y) {
            generateHorizontalHallway(world, start, end);
        } else {
            // Generate hallway in two segments: one horizontal and one vertical
            Position corner = new Position(end.x, start.y);
            generateHorizontalHallway(world, start, corner);
            generateVerticalHallway(world, corner, end);
        }
    }

    /**
     * Generates the horizontal hallway if start.y == end.y.
     * The hallway will be drawn between the start and end positions inclusive.
     *
     * @param world The current game map.
     * @param start The start position of the hallway.
     * @param end The end position of the hallway.
     */
    private static void generateHorizontalHallway(TETile[][] world, Position start, Position end) {
        if (start.y != end.y) {
            return;
        }
        int largeX = Math.max(start.x, end.x);
        int smallerX = Math.min(start.x, end.x);
        for (int x = smallerX; x <= largeX; x++) {
            if (world[x][start.y - 1] != Tileset.FLOOR) {
                world[x][start.y - 1] = Tileset.WALL;
            }
            world[x][start.y] = Tileset.FLOOR;
            if (world[x][start.y + 1] != Tileset.FLOOR) {
                world[x][start.y + 1] = Tileset.WALL;
            }
        }
    }

    /**
     * Generates the vertical hallway if start.x == end.x.
     * The hallway will be drawn between the start and end positions inclusive.
     *
     * @param world The current game map.
     * @param start The start position of the hallway.
     * @param end The end position of the hallway.
     */
    private static void generateVerticalHallway(TETile[][] world, Position start, Position end) {
        if (start.x != end.x) {
            return;
        }
        int largeY = Math.max(start.y, end.y);
        int smallerY = Math.min(start.y, end.y);
        for (int y = smallerY; y <= largeY; y++) {
            if (world[start.x - 1][y] != Tileset.FLOOR) {
                world[start.x - 1][y] = Tileset.WALL;
            }
            world[start.x][y] = Tileset.FLOOR;
            if (world[start.x + 1][y] != Tileset.FLOOR) {
                world[start.x + 1][y] = Tileset.WALL;
            }
        }
    }

    static class Edge {
        Position start;
        Position end;
        double distance;
        Room startRoom;
        Room endRoom;

        Edge(Position start, Position end, double distance, Room startRoom, Room endRoom) {
            this.start = start;
            this.end = end;
            this.distance = distance;
            this.startRoom = startRoom;
            this.endRoom = endRoom;
        }
    }

    static class UnionFind {
        private int[] parent;
        private int[] rank;

        UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int p) {
            if (parent[p] != p) {
                parent[p] = find(parent[p]);
            }
            return parent[p];
        }

        void union(int p, int q) {
            int rootP = find(p);
            int rootQ = find(q);
            if (rootP == rootQ) {
                return;
            }

            if (rank[rootP] < rank[rootQ]) {
                parent[rootP] = rootQ;
            } else if (rank[rootP] > rank[rootQ]) {
                parent[rootQ] = rootP;
            } else {
                parent[rootQ] = rootP;
                rank[rootP]++;
            }
        }
    }
}
