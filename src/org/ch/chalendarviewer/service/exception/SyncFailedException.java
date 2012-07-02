/**
 * 
 */
package org.ch.chalendarviewer.service.exception;

/**
 * Represents an exception on synchronization process
 * @author vitor
 */
public class SyncFailedException extends Exception {

    /** Serial version uid         */
    private static final long serialVersionUID = 6431831993719788266L;

    /**
     * Empty constructor
     */
    public SyncFailedException() {
        /** Empty constructor*/
    }

    /**
     * @param detailMessage exception message
     */
    public SyncFailedException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * @param throwable encapsulated exception
     */
    public SyncFailedException(Throwable throwable) {
        super(throwable);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param detailMessage exception message
     * @param throwable encapsulated exception
     */
    public SyncFailedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        // TODO Auto-generated constructor stub
    }

}
