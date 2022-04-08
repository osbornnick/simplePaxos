package server;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public interface Operation extends Serializable {

    boolean run(ConcurrentHashMap<String, String> on);
}
