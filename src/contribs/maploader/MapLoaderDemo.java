/*
 * Created on 18 novembre 2005
 * bredeche(at)lri.fr
 *
 * This is demo for loading a map from an image file in Simbad . 
 * 
 * The important parts are:
 * - the main class, which describes the environment and setup the robot
 * - the robot class, which is embedded in the latter
 *  
 */

package contribs.maploader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import simbad.gui.Simbad;
import simbad.sim.Agent;
import simbad.sim.BallAgent;
import simbad.sim.EnvironmentDescription;
import simbad.sim.RangeSensorBelt;
import simbad.sim.RobotFactory;
import simbad.sim.Wall;

/**
 * This class represents an Environment useable in Simbad but with the 
 * specification of being defined by a PNG image. For the moment, the image
 * is defined as follows
 *  - 20x20 pixels (have to fit)
 *  - a red (rgb 255,0,0) pixel means a box
 *  - a green (rgb 0,255,0) pixel represent a goal to reach (if any)
 *  - a blue (rgb 0,0,255) pixel represents the starting position
 *  - dark gray (rgb 60,60,60) pixel representing a simulated ball
 *  - the image have to be in a PNG, JPG or GIF format.
 * 
 * TODO : 
 *  - define more flexible constraints (20x20 -> ~ [10:50]x[10;50]) or something like that
 *     (for now : ([0:20],[0:20]) values but it should be more likely ([10:20],[10:20]))
 *  - add a niveler due to the rgb values for more visual effects
 *  - add the specific (table) walls defined by some intermediary color. 
 * 
 * 
 * ADDED : 
 *  - multi floor : example : if you load image.png, the app will search for image-2.png
 *  for the second floor, image-3.png for the third, etc.
 *  - Multi-robot instances
 * 
 * @author nicolas (+ cedric)
 *
 */

public class MapLoaderDemo extends EnvironmentDescription {
	
	
	//static private Simbatch sim;
	//private Robot robot;
	
	// a goal to reach
	public Point3d _goal = new Point3d();
	// the initial starting points for robots (contains Point3d)
	public ArrayList<Point3d> _startingPoints = new ArrayList<Point3d>();
	
	
	public MapLoaderDemo() 
	{
		// build the environment
		// here a simple square-closed environment.
		Wall w1 = new Wall(new Vector3d(9, 0, 0), 19, 1, this); w1.rotate90(1); add(w1);
		Wall w2 = new Wall(new Vector3d(-9, 0, 0), 19, 2, this); w2.rotate90(1); add(w2);
		Wall w3 = new Wall(new Vector3d(0, 0, 9), 19, 1, this); add(w3);
		Wall w4 = new Wall(new Vector3d(0, 0, -9), 19, 2, this); add(w4);
		
		// create the robot
		
		add(new Robot(new Vector3d(0, 0, 0), "MapRobot"));
		
	}
	
	/**
	 * initialize the environment due to the file __filename.
	 * TODO : 
	 *  - create multi-objectives points
	 *  - create balls or non static items. 
	 * @param __filename
	 */
	public MapLoaderDemo( String __filename ) 
	{
		int [] values;
		
		// initialise the image
		SimpleImage simpleImage = new SimpleImage (__filename, false);
		simpleImage.displayInformation();
		
		// step 2 : for each data in the image, initialize the environment
		//          + starting position (for one or more robots)
		//          + goal position
		for ( int y = 0 ; y != simpleImage.getHeight() ; y++ )
		{
			System.out.print(" ");
			for ( int x = 0 ; x != simpleImage.getWidth() ; x++ )
			{
				values = simpleImage.getPixel(x,y); 			
				if ( values[1] > 200 && values[2] < 50 && values[3] < 50 ) 
				{
					// red value, we will display a Wall in here : 
					add(new Wall(new Vector3d(x-(simpleImage.getWidth()/2),0,y-(simpleImage.getHeight()/2)),1,1,1,this));
					System.out.print("#");
				}
				else
					if ( values[1] < 50 && values[2] > 200 && values[3] < 50 )
					{
						// green value : define the goal point
						_goal.x = x; _goal.z = y; _goal.y = 0;
						System.out.print("X");
					}
					else
						if ( values[1] < 50 && values[2] < 50 && values[3] > 200 )
						{
							_startingPoints.add(new Point3d(x,0,y));
							// starting position
							System.out.print("!");
						}
						else
							if (values[1]<100 && values[1]==values[2] && values[2]==values[3]){
								// add a ball
								// take care because the setUsePhysics remove the agentInspector
					            showAxis(false);
					            setUsePhysics(true);				            
		                        add(new BallAgent(new Vector3d(x-(simpleImage.getWidth()/2), 0, y-(simpleImage.getHeight()/2)), "ball", new Color3f(200,0,0),0.25f,0.25f));

							}
							else
							System.out.print(" ");
			}
			System.out.print("\n");
		}
		
		String secondFloor=__filename;
		boolean hasNextFloor = true;
		int cpt = 2;
		// add other floors to the environment
		// the other files should be called : for example if the initial file is maze.png
		//  - maze-2.png, maze-3.png, ....
		while(hasNextFloor){
			try {
				// step 3 : define a second floor if exists
				if (__filename.endsWith(".png")) secondFloor = __filename.replaceAll(".png","-"+cpt+".png");
				if (__filename.endsWith(".gif")) secondFloor = __filename.replaceAll(".gif","-"+cpt+".gif");
				if (__filename.endsWith(".jpg")) secondFloor = __filename.replaceAll(".jpg","-"+cpt+".jpg");
				// only way found to check if the file exist
				// if it does not exists an exception is raised and we can poursue without adding a second floor. 
				new FileReader(secondFloor); 
				
				// step 4 : initialise the image
				simpleImage = new SimpleImage (secondFloor, false);
				simpleImage.displayInformation();
				
				// step 5 : for each data in the image, update the environment
				for ( int y = 0 ; y != simpleImage.getHeight() ; y++ )
				{
					System.out.print(" ");
					for ( int x = 0 ; x != simpleImage.getWidth() ; x++ )
					{
						values = simpleImage.getPixel(x,y);
						if ( values[1] > 200 && values[2] < 50 && values[3] < 50 ) 
						{
							// red value, we will display a Wall in here : 
							add(new Wall(new Vector3d(x-(simpleImage.getWidth()/2),cpt-1,y-(simpleImage.getHeight()/2)),1,1,1,this));
							System.out.print("#");
						}
						else	System.out.print(" ");
					}
					System.out.print("\n");
				}
				cpt++;
			}
			catch(FileNotFoundException fnfe){ 
				// do nothing : do not add a second floor
				if (cpt==2){
					System.out.println("no second floor.");
					System.out.println(" - to define a second floor, create a file called : "+secondFloor);
					System.out.println("");
				}
				hasNextFloor=false;
			}
		}
		
		// add the robots of Robot instances
		for(int i=0;i<_startingPoints.size();i++){
			add(new Robot(new Vector3d(((Point3d)_startingPoints.get(i)).x-(simpleImage.getWidth()/2), 0f, ((Point3d)_startingPoints.get(i)).z-(simpleImage.getHeight()/2)), "openDProbot"));
		}
	}
	
	
	
	
	
	// the robot is defined as an embedded class
	public class Robot extends Agent {
		
		RangeSensorBelt sonars, bumpers;
		
		public Robot(Vector3d position, String name) {
			super(position, name);
			// Add sensors
			sonars = RobotFactory.addSonarBeltSensor(this,8);
			bumpers = RobotFactory.addBumperBeltSensor(this,12);
		}
		
		/** Initialize Agent's Behavior */
		public void initBehavior() {
			// nothing particular in this case
		}
		
		/** Perform one step of Agent's Behavior */
		public void performBehavior() 
		{
			
			/*
			// *** clue-less robot ***
			if (collisionDetected()) {
				// stop the robot
				setTranslationalVelocity(0.0);
				setRotationalVelocity(0);
			}
			else
			{
				setTranslationalVelocity(1.5-2*Math.random());
				setRotationalVelocity(0.5-Math.random());
			}   
			*/
			
			// *** avoider robot ***
            if (bumpers.oneHasHit()) {
                setTranslationalVelocity(-0.1);
                setRotationalVelocity(0.5-(0.1 * Math.random()));
            } else if (collisionDetected()) {
                // stop the robot
                setTranslationalVelocity(0.0);
                setRotationalVelocity(0);
            } else   if (sonars.oneHasHit()) {
                // reads the three front quadrants
                double left = sonars.getFrontLeftQuadrantMeasurement();
                double right = sonars.getFrontRightQuadrantMeasurement();
                double front = sonars.getFrontQuadrantMeasurement();
                // if obstacle near
                if ((front  < 0.7)||(left  < 0.7)||(right  < 0.7)) {
                    if (left < right)
                        setRotationalVelocity(-1);
                    else
                        setRotationalVelocity(1);
                    setTranslationalVelocity(0);
                    
                } else{
                    setRotationalVelocity(0);
                    setTranslationalVelocity(0.6);
                }
            } else {
                setTranslationalVelocity(0.8);;
                setRotationalVelocity(0);
            }
            	
		}
	}
	
	
	/*   	  
	 * for test purposes
	 */
	public static void main(String[] args) 
	{	
		MapLoaderDemo env = new MapLoaderDemo("ressources/map1.png");
		new Simbad(env,false);	
	}
}
