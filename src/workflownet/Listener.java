package workflownet;

/**
 * Interface fuer einfaches Eventhandling
 */
public interface Listener {
    /**
     * Methode die aufgerufen wird wenn ein bestimmtes Ereignis eingetreten ist.
     */
    void handle();
}
