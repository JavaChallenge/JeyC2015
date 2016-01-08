package core.model;

import com.google.gson.Gson;
import core.Context;
import model.Position;
import util.Constants;
import util.ServerConstants;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class Map {
    private int nodeCount;
    private String mapName;
    private ArrayList<Node> nodes;

    public Map(String mapName) {
        this.nodes = new ArrayList<Node>();
        this.mapName = mapName;
        try {
            FileReader fr = new FileReader(mapName + ".text");
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            this.nodeCount = Integer.valueOf(line);
            for(int i = 0; i < this.nodeCount; i++){
                line = br.readLine();
                String args[] = line.split("[ ]");
                Node temp = new Node(Integer.valueOf(args[1]), Integer.valueOf(args[2]), i);
                this.nodes.add(temp);
//                System.out.println("node" + args[0] + " added with (" + args[1] + "," + args[2] + ")");
            }
            line = br.readLine();
            while (line != null){
                String args[] = line.split("[ ]");
                int neighborCount = args.length - 1;
                for(int j = 0; j < neighborCount; j++){
                    this.nodes.get(Integer.valueOf(args[0])).addNeighbor(this.nodes.get(Integer.valueOf(args[j + 1])));
                    this.nodes.get(Integer.valueOf(args[j + 1])).addNeighbor(this.nodes.get(Integer.valueOf(args[0])));
//                    System.out.println("node" + args[j + 1] + " and node" + args[0]);
                }
                line = br.readLine();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node getNodeAt(int index){
        return this.nodes.get(index);
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public String getMapName() {
        return mapName;
    }
}
