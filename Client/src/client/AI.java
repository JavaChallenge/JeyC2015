package client;

import client.model.Block;
import client.model.Cell;
import model.Direction;
import model.Position;
import util.Constants;
import util.ServerConstants;

import java.util.Random;

/**
 * AI class.
 * You should fill body of the method {@link #doTurn}.
 * Do not change name or modifiers of the methods or fields.
 * You can add as many methods or fields as you want!
 * You could use model to get information about current state of
 * the game.
 */
public class AI {

    public void doTurn(World world) {
        long start = System.currentTimeMillis();
        System.out.println(start);
//        System.out.println(model.getTurnRemainingTime());
        /*System.out.println(world.getMapSize().getWidth());
        System.out.println(world.getMapSize().getHeight());
        System.out.println(world.getMyId());
        System.out.println(world.getMyName());
        System.out.println(world.getTeams().length);
        System.out.println(world.getTurn());*/
        System.out.println(world.getMyCells().size());
        Random rnd = new Random();
        for(Cell c : world.getMyCells())
        {
            boolean attack = false;
            for(Direction dir : Direction.values())
            {
                Position nextPos = c.getPos().getNextPos(dir);
                for(Cell e : world.getEnemyCells())
                {
                    if(e.getPos().equals(nextPos))
                    {
                        c.attack(dir);
                        attack = true;
                        break;
                    }
                }
                if(attack)
                    break;
            }

            Block block = world.getMap().at(c.getPos());
            if(attack)continue;
            if(c.getEnergy() >= Constants.CELL_MIN_ENERGY_FOR_MITOSIS && block.getType().equals(Constants.BLOCK_TYPE_MITOSIS))
            {
                System.out.println("mitosis");
                System.out.println(c.getEnergy());
                c.mitosis();
            }
            else if(c.getEnergy() < Constants.CELL_MAX_ENERGY &&
                    block.getType().equals(Constants.BLOCK_TYPE_RESOURCE)
                    && block.getResource() > 0)
            {
                //System.out.println("gain");
                //System.out.println(block.getResource());
                //System.out.println(c.getEnergy());
                c.gainResource();
            }
            else {
                c.move(Direction.values()[rnd.nextInt(6)]);
            }
        }
        //GameEvent event = new GameEvent();
        //model.addEvent(event);
        // you should fill this method
        // an example is included here
    }

}
