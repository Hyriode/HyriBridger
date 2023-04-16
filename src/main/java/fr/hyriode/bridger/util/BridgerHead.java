package fr.hyriode.bridger.util;

import fr.hyriode.hyrame.item.ItemHead;

/**
 * Created by AstFaster
 * on 16/04/2023 at 22:13
 */
public enum BridgerHead implements ItemHead {

    TROPHY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM0YTU5MmE3OTM5N2E4ZGYzOTk3YzQzMDkxNjk0ZmMyZmI3NmM4ODNhNzZjY2U4OWYwMjI3ZTVjOWYxZGZlIn19fQ==")

    ;

    private final String texture;

    BridgerHead(String texture) {
        this.texture = texture;
    }

    @Override
    public String getTexture() {
        return this.texture;
    }

}
