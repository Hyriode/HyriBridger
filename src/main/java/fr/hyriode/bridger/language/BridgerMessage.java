package fr.hyriode.bridger.language;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

public enum BridgerMessage {

    SCOREBOARD_BEST_TIME("scoreboard.best-time"),
    SCOREBOARD_ACTUAL_TIME("scoreboard.actual-time"),
    SCOREBOARD_TOP_3("scoreboard.top-3"),
    SCOREBOARD_MEDAL_ACTUAL("scoreboard.medal.actual"),

    HOLOGRAM_STATS("hologram.stats"),
    HOLOGRAM_PLACED_BLOCKS("hologram.placed-blocks"),
    HOLOGRAM_MADE_BRIDGES("hologram.made-bridges"),
    HOLOGRAM_FAILED_BRIDGED("hologram.failed-bridged"),
    HOLOGRAM_PLAYED_TIME("hologram.played-time"),

    NPC_NAME("npc.name"),
    NPC_LORE("npc.lore"),

    GUI_ITEM_SHORT("gui.item.short"),
    GUI_ITEM_NORMAL("gui.item.normal"),
    GUI_ITEM_DIAGONAL("gui.item.diagonal"),
    GUI_ITEM_CHOOSE_ISLAND("gui.item.choose-island"),
    GUI_ITEM_ISLAND("gui.item.island"),
    GUI_ITEM_CHANGE_ISLAND("gui.item.change-island"),
    GUI_ITEM_CHANGE_MODE("gui.item.change-mode"),
    GUI_ITEM_CHANGE_BLOCK("gui.item.change-block"),
    GUI_ITEM_NAME_GO_BACK("gui.item-name.go-back"),
    GUI_ITEM_NAME_BUY_BLOCK("gui.item-name.buy-block"),

    GUI_LORE_STATUS_OCCUPIED("gui.lore.status.occupied"),
    GUI_LORE_STATUS_SELF_OCCUPIED("gui.lore.status.self-occupied"),
    GUI_LORE_STATUS_FREE("gui.lore.status.free"),
    GUI_LORE_BRIDGER_MODE("gui.lore.bridger-mode"),
    GUI_LORE_BRIDGER_MODE_SELECTED("gui.lore.bridger-mode-selected"),
    GUI_LORE_BLOCK_SELECTED("gui.lore.block.selected"),
    GUI_LORE_BLOCK_POSSESSED_BLOCK("gui.lore.block.possessed-block"),
    GUI_LORE_BLOCK_BUYABLE_BLOCK("gui.lore.block.buyable-block"),
    GUI_LORE_BLOCK_NOT_BUYABLE_BLOCK("gui.lore.block.not-buyable-block"),
    GUI_ITEM_LORE_CHANGE_ISLAND("gui.item-lore.change-island"),
    GUI_ITEM_LORE_CHANGE_BLOCK("gui.item-lore.change-block"),
    GUI_LORE_STATUS("gui.lore.status"),
    GUI_LORE_CLICK_TO_TELEPORT("gui.lore.click-to-teleport"),

    GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_VIP("gui.lore.block.rank-needed-block.vip"),
    GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_VIP_PLUS("gui.lore.block.rank-needed-block.vip+"),
    GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_EPIC("gui.lore.block.rank-needed-block.epic"),
    GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_PARTNER("gui.lore.block.rank-needed-block.partner"),

    GUI_LORE_BLOCK_MEDAL_NEEDED_BLOCK_BRONZE("gui.lore.block.medal-needed-block.bronze"),
    GUI_LORE_BLOCK_MEDAL_NEEDED_BLOCK_IRON("gui.lore.block.medal-needed-block.iron"),
    GUI_LORE_BLOCK_MEDAL_NEEDED_BLOCK_GOLD("gui.lore.block.medal-needed-block.gold"),
    GUI_LORE_BLOCK_MEDAL_NEEDED_BLOCK_ULTIMATE("gui.lore.block.medal-needed-block.ultimate"),
    GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_STAFF("gui.lore.block.rank-needed-block.staff"),

    MEDAL_BRONZE("medal.bronze"),
    MEDAL_IRON("medal.iron"),
    MEDAL_GOLD("medal.gold"),
    MEDAL_ULTIMATE("medal.ultimate"),

    TITLE_SUB_PLAYER_PB("title.sub.player.pb"),

    MESSAGE_PLAYER_OOB_BLOCK("message.player.oob.block"),
    MESSAGE_PLAYER_OOB("message.player.oob"),
    MESSAGE_PLAYER_FAILED_BRIDGE("message.player.failed-bridge"),
    MESSAGE_PLAYER_SUCCEED_PB("message.player.succeed-pb"),
    MESSAGE_PLAYER_FAILED_PB("message.player.failed-pb"),
    MESSAGE_PLAYER_REWARDS_HYRIS("message.player.rewards.hyris"),
    MESSAGE_PLAYER_REWARDS_XP("message.player.rewards.xp"),
    MESSAGE_PLAYER_ALREADY_ON_THE_SERVER("message.player.already-on-the-server"),
    MESSAGE_PLAYER_PLAYER_DOES_NOT_EXIST("message.player.player-does-not-exist"),
    MESSAGE_PLAYER_CAN_NOT_SPECTATE_HIMSELF("message.player.can-not-spectate-himself"),
    MESSAGE_PLAYER_CAN_NOT_SPECTATE_SPECTATOR("message.player.can-not-spectate-spectator"),
    MESSAGE_PLAYER_IS_SPECTATING("message.player.is-spectating"),
    MESSAGE_PLAYER_BOUGHT("message.player.bought"),
    MESSAGE_PLAYER_WATCHED_PLAYER_DISCONNECTED("message.player.watched-player-disconnected"),
    MESSAGE_PLAYER_WATCHED_PLAYER_CHANGED("message.player.watched-player-changed"),

    BLOCK_NOT_FOUND("block.not-found"),

    BLOCK_SANDSTONE("block.sandstone"),
    BLOCK_SANDSTONE_SLAB("block.sandstone-slab"),
    BLOCK_SANDSTONE_STAIRS("block.sandstone-stairs"),
    BLOCK_WHITE_WOOL("block.white-wool"),
    BLOCK_ORANGE_WOOL("block.orange-wool"),
    BLOCK_MAGENTA_WOOL("block.magenta-wool"),
    BLOCK_LIGHT_BLUE_WOOL("block.light-blue-wool"),
    BLOCK_YELLOW_WOOL("block.yellow-wool"),
    BLOCK_LIME_WOOL("block.lime-wool"),
    BLOCK_PINK_WOOL("block.pink-wool"),
    BLOCK_GRAY_WOOL("block.gray-wool"),
    BLOCK_LIGHT_GRAY_WOOL("block.light-gray-wool"),
    BLOCK_CYAN_WOOL("block.cyan-wool"),
    BLOCK_PURPLE_WOOL("block.purple-wool"),
    BLOCK_BLUE_WOOL("block.blue-wool"),
    BLOCK_BROWN_WOOL("block.brown-wool"),
    BLOCK_GREEN_WOOL("block.green-wool"),
    BLOCK_RED_WOOL("block.red-wool"),
    BLOCK_BLACK_WOOL("block.black-wool"),
    BLOCK_WHITE_CLAY("block.white-clay"),
    BLOCK_ORANGE_CLAY("block.orange-clay"),
    BLOCK_MAGENTA_CLAY("block.magenta-clay"),
    BLOCK_LIGHT_BLUE_CLAY("block.light-blue-clay"),
    BLOCK_YELLOW_CLAY("block.yellow-clay"),
    BLOCK_LIME_CLAY("block.lime-clay"),
    BLOCK_PINK_CLAY("block.pink-clay"),
    BLOCK_GRAY_CLAY("block.gray-clay"),
    BLOCK_LIGHT_GRAY_CLAY("block.light-gray-clay"),
    BLOCK_CYAN_CLAY("block.cyan-clay"),
    BLOCK_PURPLE_CLAY("block.purple-clay"),
    BLOCK_BLUE_CLAY("block.blue-clay"),
    BLOCK_BROWN_CLAY("block.brown-clay"),
    BLOCK_GREEN_CLAY("block.green-clay"),
    BLOCK_RED_CLAY("block.red-clay"),
    BLOCK_BLACK_CLAY("block.black-clay"),
    BLOCK_COAL_ORE("block.coal-ore"),
    BLOCK_IRON_ORE("block.iron-ore"),
    BLOCK_REDSTONE_ORE("block.redstone-ore"),
    BLOCK_LAPIS_ORE("block.lapis-ore"),
    BLOCK_QUARTZ_ORE("block.quartz-ore"),
    BLOCK_GOLD_ORE("block.gold-ore"),
    BLOCK_DIAMOND_ORE("block.diamond-ore"),
    BLOCK_EMERALD_ORE("block.emerald-ore"),
    BLOCK_BEDROCK("block.bedrock"),
    BLOCK_CHISELED_SANDSTONE("block.chiseled-sandstone"),
    BLOCK_SMOOTH_SANDSTONE("block.smooth-sandstone"),
    BLOCK_RED_SANDSTONE("block.red-sandstone"),
    BLOCK_CHISELED_RED_SANDSTONE("block.chiseled-red-sandstone"),
    BLOCK_SMOOTH_RED_SANDSTONE("block.smooth-red-sandstone"),
    BLOCK_WHITE_GLASS("block.white-glass"),
    BLOCK_ORANGE_GLASS("block.orange-glass"),
    BLOCK_MAGENTA_GLASS("block.magenta-glass"),
    BLOCK_LIGHT_BLUE_GLASS("block.light-blue-glass"),
    BLOCK_YELLOW_GLASS("block.yellow-glass"),
    BLOCK_LIME_GLASS("block.lime-glass"),
    BLOCK_PINK_GLASS("block.pink-glass"),
    BLOCK_GRAY_GLASS("block.gray-glass"),
    BLOCK_LIGHT_GRAY_GLASS("block.light-gray-glass"),
    BLOCK_CYAN_GLASS("block.cyan-glass"),
    BLOCK_PURPLE_GLASS("block.purple-glass"),
    BLOCK_BLUE_GLASS("block.blue-glass"),
    BLOCK_BROWN_GLASS("block.brown-glass"),
    BLOCK_GREEN_GLASS("block.green-glass"),
    BLOCK_RED_GLASS("block.red-glass"),
    BLOCK_BLACK_GLASS("block.black-glass"),
    BLOCK_COAL_BLOCK("block.coal-block"),
    BLOCK_IRON_BLOCK("block.iron-block"),
    BLOCK_GOLD_BLOCK("block.gold-block"),
    BLOCK_DIAMOND_BLOCK("block.diamond-block"),
    BLOCK_BRIDGER_TNT("block.bridger-tnt"),

    UTILS_DAYS("utils.days"),
    UTILS_HOURS("utils.hours"),
    UTILS_MINUTES("utils.minutes"),

    UTILS_DAY("utils.day"),
    UTILS_HOUR("utils.hour"),
    UTILS_MINUTE("utils.minute"),

    UTILS_YES("utils.yes"),
    UTILS_NO("utils.no"),

    ;

    private HyriLanguageMessage languageMessage;

    private final String key;
    private final BiFunction<IHyriPlayer, String, String> formatter;

    BridgerMessage(String key, BiFunction<IHyriPlayer, String, String> formatter) {
        this.key = key;
        this.formatter = formatter;
    }

    BridgerMessage(String key, BridgerMessage prefix) {
        this.key = key;
        this.formatter = (target, input) -> prefix.asString(target) + input;
    }

    BridgerMessage(String key) {
        this(key, (target, input) -> input);
    }
    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(IHyriPlayer account) {
        return this.formatter.apply(account, this.asLang().getValue(account));
    }

    public String asString(Player player) {
        return this.asString(IHyriPlayer.get(player.getUniqueId()));
    }

    public String asString(UUID uuid) {
        return this.asString(IHyriPlayer.get(uuid));
    }

    public void sendTo(Player player) {
        player.sendMessage(this.asString(player));
    }

    public List<String> asList(IHyriPlayer account) {
        return new ArrayList<>(Arrays.asList(this.asString(account).split("\n")));
    }

    public List<String> asList(Player player) {
        return this.asList(IHyriPlayer.get(player.getUniqueId()));
    }

    public String getKey() {
        return key;
    }
}
