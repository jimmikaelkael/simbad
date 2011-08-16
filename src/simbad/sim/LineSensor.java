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
 * $Date: 2005/03/17 17:49:37 $
 * $Revision: 1.13 $
 * $Source: /cvsroot/simbad/src/simbad/sim/LineSensor.java,v $
 * 21/02/2005 measurrment init value is Double.POSITIVE_INFINITY.
 * 
 * History:
 * BUGFIX Louis 01/10/2006  correct getLeftQuadrantMeasurement.
 * BUGFIX Louis 30/12/2006  now range sensor belt returns infinity or MaxRange(default) if no obstacle. 
 */
package simbad.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.media.j3d.Appearance;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PickCylinderRay;
import javax.media.j3d.PickSegment;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;




/**
 * This class models a line sensor device.
 */
public class LineSensor extends PickSensor {

	private float maxRange;
	private int nbSensors;
	/** for storing results */
	private double measurements[];
	private boolean hits[];
	/*** position of each sensor relative to center */
	private Vector3d positions[];
	/** direction vector of each sensor - relative to sensor position. */
	private Vector3d directions[];


	private Transform3D t3d;
	private Point3d start;
	private Point3d end;
	private Color3f color;
	/** for picking */
	private PickCylinderRay pickCylinder;
	private PickSegment pickSegment;
	private Point3d cylinderStart;
	private float cylinderRadius;
	private Vector3d cylinderDirection;

	private int flags;


	// FLAGS
	public final static int FLAG_SHOW_FULL_SENSOR_RAY=0X01;
	public final static int FLAG_RETURN_INFINITY=0X02;

	/**
	 * Constructs a LineSensor.
	 * Ranges are measured from the belt perimeter (not from the belt center).
	 * @param radius - the radius of the belt.
	 * @param minRange - the minimal range of each sensor ray. Not used for TYPE_BUMPER.
	 * @param maxRange - the maximal range of each sensor ray. Not used for TYPE_BUMPER.
	 * @param nbsensors - the number of sensors in the belt (typically 4,6,12,24 or 36).
	 * @param flags  FLAG_RETURN_INFINITY  Set this flag if you want sensor to return Infinity if no obstacles.  (default return maxRange) 
	 */
	public LineSensor(float radius, float minRange, float maxRange,
			int nbsensors, int flags) {
		// compute positions and directions
		positions = new Vector3d[nbsensors];
		directions = new Vector3d[nbsensors];
		Vector3d frontDir = new Vector3d(0, -maxRange, 0);
		Transform3D transform = new Transform3D();
		for (int i = 0; i < nbsensors; i++) {
			Vector3d frontPos = new Vector3d(radius,0,(i*2-nbsensors+1)*0.015f);
			transform.setIdentity();
			Vector3d pos = new Vector3d(frontPos);
			transform.transform(pos);
			positions[i] = pos;
			Vector3d dir = new Vector3d(frontDir);
			transform.transform(dir);
			directions[i] = dir;
		}
		initialize(radius, maxRange, nbsensors,flags);
	}



	/**
	 * Constructs a LineSensor.
	 * @param positions : the position of each sensor relative to belt center.
	 * @param directions : the sensing ray direction of each sensor relative to sensor positions.
	 * the magnitude of the vector corresponds to the max range.
	 * @param flags  FLAG_RETURN_INFINITY  Set this flag if you want sensor to return Infinity if no obstacles.  (default return maxRange)
	 */
	public LineSensor(Vector3d []positions,Vector3d[] directions,int flags){

		int nbsensors = positions.length;

		// compute angles
		float radius = Float.MIN_VALUE;
		float maxRange = Float.MIN_VALUE;
		for (int i=0;i< nbsensors;i++){
			Vector3d v = positions[i];
			double norm = v.length();
			// find the max radius
			if (norm> radius) radius = (float)norm;
			double range = directions[i].length();
			if (range > maxRange) maxRange = (float)range;

		}
		this.directions = directions;
		this.positions = positions;

		initialize(radius,maxRange,nbsensors,flags);

	}

	private void initialize(float radius, float maxRange,int nbsensors,int flags){
		this.flags = flags;
		this.nbSensors = nbsensors;
		this.maxRange = maxRange;
		// reserve to avoid gc.
		t3d = new Transform3D();
		pickSegment = new PickSegment();
		pickCylinder= new PickCylinderRay();
		cylinderDirection = new Vector3d(0.0,0.5,0.0);
		cylinderRadius = maxRange+radius;
		cylinderStart = new Point3d(0f,0f,0.f);
		start = new Point3d();
		end = new Point3d();

		color = new Color3f(1.0f, 0.5f, 0.25f);

		measurements = new double[nbsensors];
		hits = new boolean[nbsensors];
		for (int i=0;i< nbsensors;i++){
			// BUGFIX  Louis 30-dec-2006
			if ((flags & FLAG_RETURN_INFINITY) !=0) 
				measurements[i] = Double.POSITIVE_INFINITY;
			else 
				measurements[i] = maxRange;
			hits[i] = false;
		}
		create3D();

	}
	private void create3D() {
		super.create3D(true);
		// construct sensor body - a line for each individual sensor ray.
		Point3d[] coords = new Point3d[nbSensors*2];
		for (int i=0;i< nbSensors;i++){
			Point3d start =  new Point3d(positions[i]); 
			coords[i*2]=start;
			Point3d end = new Point3d(start);
			Point3d direction  =  new Point3d(directions[i]);
			if ((flags & FLAG_SHOW_FULL_SENSOR_RAY) ==0)
				direction.scale(0.05f); // just a small ray
				end.add(direction);
				coords[i*2+1]=end;

		}
		LineArray line = new LineArray(
				coords.length,
				GeometryArray.COORDINATES );
		line.setCoordinates( 0, coords );

		Appearance appear = new Appearance();
		Material material = new Material();

		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(color);

		appear.setColoringAttributes(ca);
		appear.setMaterial(material);
		Shape3D shape = new Shape3D( line, appear );
		shape.setCollidable(false);
		shape.setPickable(false);
		addChild(shape);

	}  
	protected void update() {
		for (int s=0;s<nbSensors;s++) {
			hits[s]= false;
			// BUGFIX  Louis 30-dec-2006
			if ((flags & FLAG_RETURN_INFINITY) !=0) 
				measurements[s] = Double.POSITIVE_INFINITY;
			else 
				measurements[s] =maxRange;

		}
		//update the pickShape with current position
		// TODO factor this getLocalToVWorld 
		group.getLocalToVworld(t3d);
		cylinderStart.set(0.0f,0.0f,0.f);
		cylinderDirection.set(0.0,0.5,0.0);

		// set a pickCylinder around the belt 
		t3d.transform(cylinderStart);
		t3d.transform(cylinderDirection);
		pickCylinder.set(cylinderStart,cylinderDirection,cylinderRadius); 

		// pick possibly intersecting shapes
		// rem: pickAllSorted costs too much
		SceneGraphPath[] picked = pickableSceneBranch.pickAll(pickCylinder);
		boolean intersect= false;
		double minDist;
		double[] dist = new double[1];
		if (picked != null){

			// now check each sensor ray
			for (int s=0;s<nbSensors;s++)
			{
				start.set(positions[s]);
				end.set(start);
				end.add(directions[s]);

				t3d.transform(start);
				t3d.transform(end);

				pickSegment.set(start,end);
				// find the nearest
				minDist = Double.MAX_VALUE;

				intersect=false;
				// Pick again but on the segment

				picked = pickableSceneBranch.pickAll(pickSegment);
				if (picked != null) {
					// for all picked objects
					for (int i = 0; i < picked.length; i++) {
						Node obj = picked[i].getObject();
						if (obj instanceof Shape3D) {
							if (((Shape3D) obj).intersect(picked[i],
									pickSegment, dist)) {
								if  (dist[0] < minDist){
									minDist = dist[0];
									intersect = true;
								}
							}
						}
					}
				}
				hits[s] =intersect;
				if (intersect){

					measurements[s]= minDist;
					//System.out.println ("Sensor "+s+"="+(minDist));
				}
			}
		}

	}


	/**
	 * Returns the last measure collected for the individual sensor. Measurement is made from the circle perimeter.
	 * @param sensorNum num of the sensor.
	 * @return the range measurment.
	 */
	public double getMeasurement(int sensorNum){
		return measurements[sensorNum];
	}

	/**
	 * Returns the hit state of the sensor.
	 * @param sensorNum num of the sensor.
	 * @return true if the sensor ray has hit an obstacle 
	 */
	public boolean hasHit(int sensorNum){
		return hits[sensorNum];
	}

	/**
	 * Return the number of individual sensor in the belt.
	 * @return the number of sensors.
	 */
	public int getNumSensors(){ return nbSensors;}

	/**
	 * Returns the maximum sensing range in meters. 
	 * @return the maximum range.
	 */
	public float getMaxRange(){ return maxRange;}

	public JPanel createInspectorPanel(){
		return new RangeSensorBeltJPanel();
	}

	/**
	 * Return sensor flags.
	 */
	public int getFlags(){ return flags;}

	/**
	 * A JPanel Inner class for displaying the sensor belt rays in 2d. 
	 */
	private class RangeSensorBeltJPanel extends JPanel{

		private static final long serialVersionUID = 1L;
		Font font;
		int lineSize=8;
		DecimalFormat format; 
		final static int IMAGE_SIZEX = 200;
		final static int IMAGE_SIZEY = 40;
		public RangeSensorBeltJPanel(){
			Dimension d= new Dimension(50,IMAGE_SIZEY);
			setPreferredSize(d);
			setMinimumSize(d);
			font = new Font("Arial",Font.PLAIN,lineSize-1);
			// display format for numbers
			format = new DecimalFormat();
			format.setMaximumFractionDigits(3);
			format.setMinimumFractionDigits(2);
			format.setPositivePrefix("");
			format.setMinimumIntegerDigits(1);
		}
		/** Caution not synchronised */

		protected void paintComponent( Graphics g){
			super.paintComponent(g);
			g.setFont(font);
			//  Color c;
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0,0,IMAGE_SIZEX,IMAGE_SIZEY);
			g.setColor(Color.GRAY);
			int x =0; int y =lineSize;
			for (int i=0;i< nbSensors;i++){

				if (x+35 > IMAGE_SIZEX) {
					x = 0;
					y += lineSize;
				}
				g.drawString("["+i+"] "+hits[i],x,y);
				//g.drawString("["+i+"] "+format.format(measurements[i]),x,y);
				x += 35;
			}     	
		}       
	}
}
