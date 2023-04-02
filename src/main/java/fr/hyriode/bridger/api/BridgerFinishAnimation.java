package fr.hyriode.bridger.api;

import fr.hyriode.bridger.game.blocks.Specification;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BridgerFinishAnimation {

    private final String id;

    private final List<Specification> specifications =  new ArrayList<>();

    public BridgerFinishAnimation(String id) {
        this.id = id;
    }

    public BridgerFinishAnimation(String id, Specification... specifications) {
        this.id = id;
        this.specifications.addAll(Arrays.asList(specifications));
    }

    abstract void play(final List<Location> blocks);

    public String getId() {
        return id;
    }

    public List<Specification> getSpecifications() {
        return specifications;
    }
}
