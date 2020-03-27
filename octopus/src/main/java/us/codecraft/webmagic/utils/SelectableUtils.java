package us.codecraft.webmagic.utils;

import us.codecraft.webmagic.selector.Selectable;

public class SelectableUtils {

    public static String getValue(Selectable xpath) {
        return xpath.all().size() == 0 ? "-" : xpath.get();
    }
}
