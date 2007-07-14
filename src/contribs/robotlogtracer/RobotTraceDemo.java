/**
 * update: 20060922
 * Robot Trace Demo - a robot shortly wanders through the environment - the trace is written to a file in the \"log\" directory.
 * Authors: Cedric and Nicolas
 */

package contribs.robotlogtracer;


import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import simbad.gui.Simbatch;
import simbad.sim.Agent;
import simbad.sim.EnvironmentDescription;
import simbad.sim.RangeSensorBelt;
import simbad.sim.RobotFactory;
import simbad.sim.Wall;


/**
 * Robot Trace Demo - a robot shortly wanders through the environment - the trace is written to a file in the \"log\" directory.
 */
public class RobotTraceDemo extends EnvironmentDescription 
{

	public final static int ARENA_WIDTH = 5; //lenght of the arena
	public final static int ARENA_HEIGHT = 5; // width of the arena
	
	// pixel matrix for the traces
	public int [][] _matrice = new int [ARENA_WIDTH*100][ARENA_HEIGHT*100]; 
	public LogTracer tg = new LogTracer();
	
	public static Random _rand = new Random(); //randomizer

	public WanderRobot _myRobot = new WanderRobot(new Vector3d(_rand.nextInt(ARENA_WIDTH*2-1)-(ARENA_WIDTH*2-1)/2, 0, _rand.nextInt(ARENA_HEIGHT*2-1)-(ARENA_HEIGHT*2-1)/2), "Simple network 0", this); 	//The evolutionnary bot used in the environment.
	
	/**
	 * Default constructor. It builds a simple environment with white walls and puts a single bot in it.
	 */
	public RobotTraceDemo ()
	{	
		System.out.println("Robot Trace Demo - a robot shortly wanders through the environment - the trace is written to a file in the \"log\" directory");

		this.wallColor = new Color3f(0.4f,0.4f,1);
		Wall w1 = new Wall(new Vector3d(ARENA_WIDTH, 0, 0), ARENA_WIDTH*2, 1, this); w1.rotate90(1); add(w1);
		Wall w3 = new Wall(new Vector3d(0, 0, ARENA_HEIGHT), ARENA_HEIGHT*2, 1, this); add(w3);
		Wall w2 = new Wall(new Vector3d(-ARENA_WIDTH, 0, 0), ARENA_WIDTH*2, 1, this); w2.rotate90(1); add(w2);
		Wall w4 = new Wall(new Vector3d(0, 0, -ARENA_HEIGHT), ARENA_HEIGHT*2, 1, this); add(w4);
		add(_myRobot);
	}
	
	
    /**
     * A wandering robot
     */
    public class WanderRobot extends Agent
	{  	
		RangeSensorBelt bumpers, sonars; //sensors for the bot
		
		public WanderRobot (Vector3d position, String nom, RobotTraceDemo __setup)
		{
			super(position,nom);
			sonars = RobotFactory.addSonarBeltSensor(this,8);
			bumpers = RobotFactory.addBumperBeltSensor(this,12);
		}

		public void initBehavior () 
		{
		}
		
		public void performBehavior ()
		{	
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
	} // end of robot class	
    
    
    /** running the robot and writing trace to "log" directory */
    private void evalueTrace ()
    {
	   // 	double globalFitness=0;
	    	String suffixe = ""+System.currentTimeMillis()/1000;
	    	
	    	Simbatch sim = new Simbatch(this,true);
	
	    	sim.reset(); // stability test?
		
	    	int x=0;
		int z=0;

    		int i = 0;

    		// 20000 steps
    		while ( i < 20000 ) //&& this._isRunnable ) 
    		{
    			//try {Thread.sleep(10); } catch(Exception e){} 
    			sim.step();
    			i++;
    			Point3d coords = new Point3d();
    			_myRobot.getCoords(coords);
			// update the pixel matrix
			int xm = (int)((coords.x+5)*50);
			int zm = (int)((coords.z+5)*50);
			this._matrice[xm][zm]+=5;
			if (i==1) { x=xm; z=zm; }
			System.out.flush();
    		}

    		System.out.println("end of the trace - see trace file in \"log\" directory");

    		// trace the graphic to a file
    		this.tg.setBeginTrace(x,z);
    		    		
		this.tg.commitTrace(this._matrice,"log/tracelog-"+suffixe+".png");
		
		//reset the pixel marix
		for(int ki =0;ki<_matrice.length;ki++)
		{
			for(int kj=0;kj<_matrice[ki].length;kj++)
			{
				_matrice[ki][kj]=0;
			}
		}
		// ready for a new trace...
	    	System.out.println("end of trace demo.");
	    	
    }
    
    // demo purpose : a robot shortly wanders through the environment - the trace is written to a file in the "log" directory 
    public static void main (String [] argv)
    {
		RobotTraceDemo demo = new RobotTraceDemo();
		demo._myRobot.initBehavior();
	    demo.evalueTrace();
    }
    
}


	
	


