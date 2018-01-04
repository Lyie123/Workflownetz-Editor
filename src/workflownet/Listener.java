package workflownet;

/**
 * Interface für einfaches Eventhandling
 */
public interface Listener {
    /**
     * Methode die aufgerufen wird wenn ein bestimmtes Ereignis eingetreten ist.
     */
    void handle();
}
