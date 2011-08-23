/*
 *  Simbad - Robot Simulator
 *  Copyright (C) 2004 Louis Hugues
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 -----------------------------------------------------------------------------
 * $Author: sioulseuguh $ 
 * $Date: 2005/01/08 16:51:31 $
 * $Revision: 1.3 $
 * $Source: /cvsroot/simbad/src/simbad/sim/WorldFactory.java,v $
 */
package simbad.sim;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
/**
 *  A helper class to aid building specific environments.
 */
public class WorldFactory extends Factory {

	/** Sets a RoboSoccer terrain in the given environment.
	 * 
	 * @param wd : environment to set.
	 */ 
	static public void setRoboSoccerEnvironment(EnvironmentDescription wd) {

		// set lights, floor and wall color, and set physics
		wd.light1IsOn = true;
		wd.light2IsOn = false;
		wd.floorColor = wd.green;
		wd.wallColor = wd.white;
		wd.showAxis(false);
		wd.setUsePhysics(true);

		// add walls
		Wall w1 = new Wall(new Vector3d(-8, 0, 0), 18.3f, 1, wd);
		w1.rotate90(1);
		Wall w2 = new Wall(new Vector3d(8, 0, 0), 18.3f, 1, wd);
		w2.rotate90(1);
		wd.add(w1);
		wd.add(w2);
		wd.add(new Wall(new Vector3d(-5, 0, 9), 6, 1, wd));
		wd.add(new Wall(new Vector3d(5, 0, 9), 6, 1, wd));
		wd.add(new Wall(new Vector3d(-5, 0, -9), 6, 1, wd));
		wd.add(new Wall(new Vector3d(5, 0, -9), 6, 1, wd));

		// add goals
		wd.add(new Wall(new Vector3d(0, 0, -10), 4, 2, wd));
		wd.add(new Wall(new Vector3d(0, 0, 10), 4, 2, wd));
		Wall w3 = new Wall(new Vector3d(-2, 0, -9.5f), 1.33f, 2, wd);
		w3.rotate90(1);
		Wall w4 = new Wall(new Vector3d(2, 0, -9.5f), 1.33f, 2, wd);
		w4.rotate90(1);
		Wall w5 = new Wall(new Vector3d(-2, 0, 9.5f), 1.33f, 2, wd);
		w5.rotate90(1);
		Wall w6 = new Wall(new Vector3d(2, 0, 9.5f), 1.33f, 2, wd);
		w6.rotate90(1);
		wd.add(w3);
		wd.add(w4);
		wd.add(w5);
		wd.add(w6);

		// add lines
		Line l1 = new Line(new Vector3d(-8, 0, 0), 16, wd, new Color3f(2.5f,2.5f,2.5f));
		l1.rotate90(1);
		Line l2 = new Line(new Vector3d(-2, 0, -9), 4, wd, new Color3f(2.5f,2.5f,2.5f));
		l2.rotate90(1);
		Line l3 = new Line(new Vector3d(-2, 0, 9), 4, wd, new Color3f(2.5f,2.5f,2.5f));
		l3.rotate90(1);
		Line l4 = new Line(new Vector3d(-3.5f, 0, -6.5f), 7, wd, new Color3f(2.5f,2.5f,2.5f));
		l4.rotate90(1);
		Line l5 = new Line(new Vector3d(-3.5f, 0, 6.5f), 7, wd, new Color3f(2.5f,2.5f,2.5f));
		l5.rotate90(1);
		wd.add(l1);
		wd.add(l2);
		wd.add(l3);
		wd.add(l4);
		wd.add(l5);
		wd.add(new Line(new Vector3d(-3.5f, 0, -9), 2.5f, wd, new Color3f(2.5f,2.5f,2.5f)));
		wd.add(new Line(new Vector3d(3.5f, 0, -9), 2.5f, wd, new Color3f(2.5f,2.5f,2.5f)));
		wd.add(new Line(new Vector3d(-3.5f, 0, 6.5f), 2.5f, wd, new Color3f(2.5f,2.5f,2.5f)));
		wd.add(new Line(new Vector3d(3.5f, 0, 6.5f), 2.5f, wd, new Color3f(2.5f,2.5f,2.5f)));
	}

}
