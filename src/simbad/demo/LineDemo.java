/*
 * Simbad - Robot Simulator Copyright (C) 2004 Louis Hugues
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -----------------------------------------------------------------------------
 * $Author: sioulseuguh $ 
 * $Date: 2005/03/17 17:55:54 $ $Revision: 1.6 $ $Source: /cvsroot/simbad/src/simbad/demo/BaseDemo.java,v $
 */
package simbad.demo;

import javax.vecmath.Vector3d;
import simbad.sim.*;
/** A basic demo with a Line sensor.
 * The robot follow the black line.
 *
 */
public class LineDemo extends Demo {

	public class Robot extends Agent {
		LineSensor linesensor;
		CameraSensor camera;
		public Robot (Vector3d position, String name) {     
			super(position,name);
			// Add sensors
			linesensor = RobotFactory.addLineSensor(this);
			// Add camera
			camera = RobotFactory.addCameraSensor(this);
			camera.rotateZ(-Math.PI/4);
		}

		/** Initialize Agent's Behavior*/
		public void initBehavior() {
			// nothing particular in this case
		}

		/** Perform one step of Agent's Behavior */
		public void performBehavior() {

			if (linesensor.hasHit(0)) {
				setTranslationalVelocity(0.1);
				setRotationalVelocity(Math.PI/4);
			}
			else if (linesensor.hasHit(4)) {
				setTranslationalVelocity(0.1);
				setRotationalVelocity(-Math.PI/4); 
			}
			else if (linesensor.hasHit(1) && linesensor.hasHit(2) && linesensor.hasHit(3)) {
				setTranslationalVelocity(0.5);
				setRotationalVelocity(0);
			}
			else {
				setTranslationalVelocity(0.1);
				setRotationalVelocity(-Math.PI/4);
			}
		}
	}
	public LineDemo() {
		light1IsOn = true;
		light2IsOn = false;
		Wall w1 = new Wall(new Vector3d(9, 0, 0), 19, 1, this);
		w1.rotate90(1);
		add(w1);
		Wall w2 = new Wall(new Vector3d(-9, 0, 0), 19, 2, this);
		w2.rotate90(1);
		add(w2);
		Wall w3 = new Wall(new Vector3d(0, 0, 9), 19, 1, this);
		add(w3);
		Wall w4 = new Wall(new Vector3d(0, 0, -9), 19, 2, this);
		add(w4);
		Line l1 = new Line(new Vector3d(0, 0, 0), 2, this);
		l1.rotate90(1);
		add(l1);
		Line l2 = new Line(new Vector3d(2, 0, 0), 2, this);
		add(l2);
		Line l3 = new Line(new Vector3d(-2, 0, 2), 4, this);
		l3.rotate90(1);
		add(l3);
		Line l4 = new Line(new Vector3d(-2, 0, -2), 4, this);
		add(l4);
		Line l5 = new Line(new Vector3d(-2, 0, -2), 6, this);
		l5.rotate90(1);
		add(l5);
		Line l6 = new Line(new Vector3d(4, 0, -2), 6, this);
		add(l6);
		Line l7 = new Line(new Vector3d(-4, 0, 4), 8, this);
		l7.rotate90(1);
		add(l7);
		Line l8 = new Line(new Vector3d(-4, 0, -4), 8, this);
		add(l8);
		Line l9 = new Line(new Vector3d(-4, 0, -4), 10, this);
		l9.rotate90(1);
		add(l9);
		Line l10 = new Line(new Vector3d(6, 0, -4), 10, this);
		add(l10);
		Line l11 = new Line(new Vector3d(-6, 0, 6), 12, this);
		l11.rotate90(1);
		add(l11);
		Line l12 = new Line(new Vector3d(-6, 0, -6), 12, this);
		add(l12);
		Line l13 = new Line(new Vector3d(-6, 0, -6), 13, this);
		l13.rotate90(1);
		add(l13);

		add(new Robot(new Vector3d(0, 0, 0), "robot 1"));

	}
}

