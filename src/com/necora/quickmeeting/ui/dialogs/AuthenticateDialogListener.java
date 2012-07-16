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
    
    
    based on code http://www.londatiga.net/featured-articles/how-to-use-foursquare-api-on-android-application/
*/

package com.necora.quickmeeting.ui.dialogs;


/**
 * TODO review this class
 *
 */
public interface AuthenticateDialogListener {
    public abstract void onComplete(String authorizationCode);
    public abstract void onError(String error);
}
