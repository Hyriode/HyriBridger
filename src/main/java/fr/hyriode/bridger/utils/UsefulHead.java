package fr.hyriode.bridger.utils;

public enum UsefulHead {

    BACK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDNjNWNlYWM0ZjViN2YzZDhlMzUxN2ViNTdkOTc3ZmM2ZGU0MTRhMmNiZTE4NDljMTYzMmRjMDhmNTJmZDgifX19");

    private final String texture;

    UsefulHead(String texture) {
        this.texture = texture;
    }

    public String getTexture() {
        return texture;
    }
}
