/* 
*    Ref-Finder
*    Copyright (C) <2015>  <PLSE_UCLA>
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * Created on Jun 28, 2004
 */
package tyRuBa.util;


/**
 * An interface to a statistics gatherer
 * @author riecken
 */
public interface Statistics {
    
    /** Start gathering statistics if we have not already done so */
    public void stopGathering();
    /** Stop gathering statistics */
    public void startGathering();
    /** Reset all statistics */
    public void reset();
    
    /** Retrieve an integer valued statistic keyed by the specified statistic name */
    public int getIntStat(String statName);
    /** Retrieve a long valued statistic keyed by the specified statistic name */
    public long getLongStat(String statName);
    /** Retrieve a float valued statistic keyed by the specified statistic name */
    public float getFloatStat(String statName);
    /** Retrieve an Object valued statistic keyed by the specified statistic name */
    public Object getObjectStat(String statName);
    
}
