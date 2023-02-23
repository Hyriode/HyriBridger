package fr.hyriode.bridger.game;

import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.game.IHyriGameType;

import java.util.ArrayList;
import java.util.List;

public class BridgerGameInfo implements IHyriGameInfo {

    private final String name;
    private String displayName;
    private final List<IHyriGameType> types;

    public BridgerGameInfo(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
        this.types = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public IHyriGameType getType(String name) {
        return this.types.stream().filter(iHyriGameType -> iHyriGameType.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void addType(int id, String name, String displayName) {

    }

    @Override
    public void removeType(String name) {
        this.types.remove(name);
    }

    @Override
    public List<IHyriGameType> getTypes() {
        return this.types;
    }

    @Override
    public void update() {
        IHyriGameInfo.super.update();
    }
}
