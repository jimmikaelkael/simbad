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
 * $Author: jimmikaelkael $ 
 */
package simbad.sim;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import com.sun.j3d.utils.geometry.Primitive;
/**
 * A line object which can be put in the environment. 
 * @author Michaël Jimenez
 */
public class Line extends BlockWorldObject {
    
    /** Object dimension.s */
    float len;
     
    /** Constructs a line object.
     * 
     * @param pos position of the start of the object. 
     * @param len length of the object.
     * @param wd	EnvironmentDescription for global parameters.
     */
    public Line(Vector3d pos, float len, EnvironmentDescription wd) {        
        this(pos,len,wd,black);
    }
    
    /** Constructs a line object.
     * 
     * @param pos position of the start of the object. 
     * @param len length of the object.
     * @param wd	EnvironmentDescription for global parameters.
     * @param color  color of the object.
     */
    public Line(Vector3d pos, float len, EnvironmentDescription wd, Color3f color) {
    	super();
    	this.len = len;
        // put it on the floor
        pos.z += len/2;
    	setCanBeTraversed(true);
        create3D(wd,color);
        translateTo(pos);
    }
    /** Create the 3d object */
    protected void create3D(EnvironmentDescription wd,Color3f color){
        // create the line using a primitive
        super.create3D();
        Material mat = new Material();
        appearance.setMaterial(mat);

        int flags = Primitive.GEOMETRY_NOT_SHARED | Primitive.ENABLE_GEOMETRY_PICKING | Primitive.GENERATE_NORMALS;
        flags |= Primitive.ENABLE_APPEARANCE_MODIFY;
        // com.sun.j3d.utils.geometry.Box box =new com.sun.j3d.utils.geometry.Box(.08f,.001f,len,flags,appearance,0);
        // bug in j3d api doc . must give half values.
        com.sun.j3d.utils.geometry.Box box =new com.sun.j3d.utils.geometry.Box(.08f/2,.005f/2,len/2,flags,appearance,0);
         // Enable  sensor  detection 
        box.setPickable(true);

        //  define empty bounds for collision  detection
	    BoundingBox bounds = new BoundingBox();
	    setBounds(bounds);

	    setColor(color);
	    addChild(box);

    }

    public void rotate90(int ntimes) {
        super.rotate90(ntimes);
        if ((ntimes%2) != 0)
            translateTo(new Vector3d(len/2,0,-len/2));
    }

}
