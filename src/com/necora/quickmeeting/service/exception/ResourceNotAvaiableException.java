/**
    This file is part of QuickMeeting.

    QuickMeeting is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QuickMeeting is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with QuickMeeting.  If not, see <http://www.gnu.org/licenses/>.    
*/
package com.necora.quickmeeting.service.exception;

/**
 * When a remote service is not available, this exception is thrown
 * @author vitor
 */
public class ResourceNotAvaiableException extends Exception {

    /** serial version uid  */
    private static final long serialVersionUID = 1696583117774348039L;

    /**
     * Empty constructor
     */
    public ResourceNotAvaiableException() {
        /* Empty constructor */
    }

    /**
     * @param detailMessage exception message
     */
    public ResourceNotAvaiableException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * @param throwable encapsulated exception
     */
    public ResourceNotAvaiableException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param detailMessage exception message
     * @param throwable encapsulated exception
     */
    public ResourceNotAvaiableException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
