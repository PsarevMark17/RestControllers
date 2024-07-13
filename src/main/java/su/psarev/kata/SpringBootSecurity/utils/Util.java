package su.psarev.kata.SpringBootSecurity.utils;

import java.util.Collection;

public class Util {
    public static <T> String setToString(Collection<T> set) {
        StringBuilder sb = new StringBuilder();
        for (T t : set) {
            sb.append(t).append(" ");
        }
        if (!sb.isEmpty()) {
            return sb.delete(sb.length() - 1, sb.length()).toString();
        }
        return "";
    }
}
