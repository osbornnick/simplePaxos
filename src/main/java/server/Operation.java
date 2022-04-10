package server;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An operation defines a change to make to the key-value store. Apply the operation by calling
 * run()
 */
public interface Operation extends Serializable {

  /**
   * Commit this operation on the given hashmap
   *
   * @param on hashmap to apply the operation too
   * @return true if the operation succeeded.
   */
  boolean run(ConcurrentHashMap<String, String> on);
}
