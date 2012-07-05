/**
    This file is part of ChalendarViewer.

    ChalendarViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ChalendarViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ChalendarViewer.  If not, see <http://www.gnu.org/licenses/>.    
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
