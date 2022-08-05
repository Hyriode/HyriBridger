package fr.hyriode.bridger.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageUtils {

    public static String getCentredMultiLinesMessage(String message) {
        final List<String> lines = Arrays.asList(message.split("\n"));
        final List<String> returnLines = new ArrayList<>();

        lines.forEach(line -> returnLines.add(MessageUtils.getCentredMessage(line)));
        StringBuilder returnMessage = new StringBuilder();
        returnLines.forEach(line -> returnMessage.append(line).append("\n"));

        return returnMessage.toString();

    }

    public static String getCentredMessage(String message) {
        if(message == null || message.equals("")) {
            return "";
        }
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
            }else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        int CENTER_PX = 154;
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }
}
