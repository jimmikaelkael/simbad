/**
 * Created on 26 avr. 2005
 * @author cedric and nicolas
 * 
 * ## Simbad speed Benchmark ##
 * 
 * The robot will run during 1.000.000 steps randomly wandering in the environment (sometimes it even gets stuck, 
 * eventhough this does not impact the benchmark). This benchmark provides the number of steps per seconds for this 
 * very specific setup (i.e. it *will* vary from one environment or controller to another). based upon the wander 
 * demo.
 * 
 * Please add your own statistics at the end of this file. Please follow the provided layout for clarity purpose.
 * You may want to benchmark simbad with and without display mode (note however that displayMode=false sometimes
 * causes a memory error due to a known memory leak bug in Java3D).
 * 
**/

package contribs.bench;


import java.util.Random;

import javax.vecmath.Vector3d;

import simbad.gui.Simbatch;
import simbad.sim.Agent;
import simbad.sim.EnvironmentDescription;
import simbad.sim.RangeSensorBelt;
import simbad.sim.RobotFactory;
import simbad.sim.Wall;

public class Benchmark {

	static private boolean _displayMode = true; // true: display simulator window, false : quiet mode (faster)
	
	public static void main(String[] args) {
		
		long _chrono = System.currentTimeMillis();
		long totalSteps = 1000000; 
		
		EnvBench eb = new EnvBench();
		Simbatch sim = new Simbatch(eb,_displayMode);
		sim.reset();
		
		System.out.println("Starting Benchmark (display mode = " + _displayMode + ")");
		for(int i=0;i<totalSteps;i++) 
		{
			if ( i%(totalSteps/10) == 0 ) System.out.print(".");
			sim.step();
		}
		
		System.out.println("\nEnd of Benchmark (please provide several results for relevant average figures)");

		// at the end, simply have to compute the time taken
		_chrono = System.currentTimeMillis()-_chrono;
		System.out.println("/* username : (fill in) */");
		System.out.println("/* date : (fill in) */");
		System.out.println("/* hardware (speed,ram,video) and OS description : (fill in) */");
		System.out.println("/* notes : (anything special?) */");
		System.out.println("/* benchmark took "+_chrono+" ms, i.e. "+ totalSteps/(_chrono/1000) +" steps/sec (display mode is " + _displayMode + ") */");
		
	}
}


class EnvBench  extends EnvironmentDescription {
	
	static Random _rand=new Random();
	
	
    public EnvBench()
    {
    	Wall w1 = new Wall(new Vector3d(9, 0, 0), 18, 1, this); w1.rotate90(1); add(w1);
    	Wall w2 = new Wall(new Vector3d(-9, 0, 0), 18, 1, this); w2.rotate90(1); add(w2);
    	Wall w3 = new Wall(new Vector3d(0, 0, 9), 18, 1, this); add(w3);
    	Wall w4 = new Wall(new Vector3d(0, 0, -9), 18, 1, this); add(w4);
    	add(new Robot(new Vector3d(0,0,0),"Astro le petit robot"));
    }
	
    public class Robot extends Agent {
    	
        RangeSensorBelt sonars, bumpers;

        public Robot (Vector3d position, String name) 
        {
            super(position, name);
            // Add sensors
            bumpers = RobotFactory.addBumperBeltSensor(this,12);
            sonars = RobotFactory.addSonarBeltSensor(this,12);
        }

        public void initBehavior () {}

        public void updateLog (){}
        
        public void performBehavior () 
        {
            if (bumpers.oneHasHit()) 
            {
                setTranslationalVelocity(-0.1);
                setRotationalVelocity(0.5-(0.1 * Math.random()));
            } 
            else 
            	if (collisionDetected()) 
            	{
	                setTranslationalVelocity(0.0);
	                setRotationalVelocity(0);
	                moveToStartPosition();
	                System.out.println("Collision!");
            	} 
            	else   
            		if (sonars.oneHasHit()) 
            		{
		                // reads the three front quadrants
		                double left = sonars.getFrontLeftQuadrantMeasurement();
		                double right = sonars.getFrontRightQuadrantMeasurement();
		                double front = sonars.getFrontQuadrantMeasurement();
		                // if obstacle near
		                if ((front  < 0.7)||(left  < 0.7)||(right  < 0.7)) 
		                {
		                    if (left < right)
		                        setRotationalVelocity(-1);
		                    else
		                        setRotationalVelocity(1);
		                    setTranslationalVelocity(0);
	                    } 
		                else
		                {
		                    setRotationalVelocity(0);
		                    setTranslationalVelocity(0.6);
		                }
            		} 
            else 
            {
                setTranslationalVelocity(0.8);;
                setRotationalVelocity(0);
            }
        }
    }  
}


/* ********************************************************************************************************************************** */

/* username : nicolas br. */
/* date : 2005/04/27 */
/* hardware (speed,ram,video) and OS description : pc 2.8ghz 512mo nvidiaQuadro4_200/400 under linux (knoppix)*/
/* notes : under linux+eclipse, simbad processes do not terminate (!!), moreover speed is quite unstable */
/* display mode is true */
/* benchmark took 175969 ms, i.e. 5714 steps/sec (display mode is true) */
/* benchmark took 155511 ms, i.e. 6451 steps/sec (display mode is true) */
/* benchmark took 111907 ms, i.e. 9009 steps/sec (display mode is true) */
/* benchmark took 113035 ms, i.e. 8849 steps/sec (display mode is true) */
/* average benchmark : ~138 sec => ~7200 steps/sec (but high variability btw runs (+/- 30 sec)) */
/* display mode is false */
/* benchmark took 66579 ms, i.e. 15151 steps/sec (display mode is false) */
/* benchmark took 61572 ms, i.e. 16393 steps/sec (display mode is false) */
/* benchmark took 77088 ms, i.e. 12987 steps/sec (display mode is false) */
/* benchmark took 63374 ms, i.e. 15873 steps/sec (display mode is false) */
/* average benchmark : ~67 sec => ~15000 steps/sec */

/* username : nicolas br. */
/* date : 2005/04/27 */
/* hardware (speed,ram,video) and OS description : pc 2.8ghz 512mo nvidiaQuadro4_200/400 under windows XP*/
/*          java3D/directX */
/* notes : ( displayMode = false ) cannot be done due to java3D bug. */
/* benchmark took 75219 ms, i.e. 13333 steps/sec (display mode is true) */
/* benchmark took 75687 ms, i.e. 13333 steps/sec (display mode is true) */
/* benchmark took 72906 ms, i.e. 13888 steps/sec (display mode is true) */
/* benchmark took 73157 ms, i.e. 13698 steps/sec (display mode is true) */
/* benchmark took 79156 ms, i.e. 12658 steps/sec (display mode is true) */
/* benchmark took 70672 ms, i.e. 14285 steps/sec (display mode is true) */
/* average benchmark : ~75 sec => ~13500 steps/sec */
/* other runs: */
/* benchmark took 74797 ms, i.e. 13513 steps/sec (display mode is false) -- only full run in 4 tries, not that quicker... */
/* benchmark took 86093 ms, i.e. 11627 steps/sec (display mode is true, but window hidden) */
/* benchmark took 92922 ms, i.e. 10869 steps/sec (display mode is true, but window hidden) */
/* benchmark took 101141 ms, i.e. 9900 steps/sec (display mode is true, but window hidden) --> so, dont hide the window :-) */
/* other run: */
/* hardware (speed,ram,video) and OS description : pc 2.8ghz 512mo nvidiaQuadro4_200/400 under windows XP*/
/*          java3D/openGL */
/* notes : ( displayMode = false ) cannot be done due to java3D bug. */
/* benchmark took 60703 ms, i.e. 16666 steps/sec (display mode is true) */
/* benchmark took 66453 ms, i.e. 15151 steps/sec (display mode is true) */
/* benchmark took 80109 ms, i.e. 12500 steps/sec (display mode is true) */
/* benchmark took 59735 ms, i.e. 16949 steps/sec (display mode is true) */
/* benchmark took 59984 ms, i.e. 16949 steps/sec (display mode is true) */
/* benchmark took 64750 ms, i.e. 15625 steps/sec (display mode is true) */
/* benchmark took 60000 ms, i.e. 16666 steps/sec (display mode is true) */
/* benchmark took 61422 ms, i.e. 16393 steps/sec (display mode is true) */
/* average benchmark : ~64 sec => ~15862 steps/sec */
/* conclusion : better use java3d/opengl under windows. Moreover the directX version is more bugged.  */


/* ********************************************************************************************************************************** */

/* username : Vincent Besson */
/* date : 2005/05/10 */
/* hardware (speed,ram,video) and OS description : P4 3.4 GHz, ram 512 Mo, NVidia Quadro NVS 64 Mo, Linux Mandrake 10.1 */
/* notes : - often the robot stay blocked in a corner, but I let end the benchmark;
/* 		   - when I run Benchmark with totalSteps = 1000000 i get memory heaps. Anyway, I got these results: */
/* display mode is true*/
/* benchmark took 102147 ms, i.e. 9803 steps/sec (display mode is true) */
/* benchmark took 115492 ms, i.e. 8695 steps/sec (display mode is true) */
/* benchmark took 106357 ms, i.e. 9433 steps/sec (display mode is true) */
/* benchmark took 111622 ms, i.e. 9009 steps/sec (display mode is true) */
/* average benchmark took ~ 109 s, i.e. ~ 9200 steps/s                  */
/* display mode is false*/
/* benchmark took 65314 ms, i.e. 15384 steps/sec (display mode is false) */
/* benchmark took 47375 ms, i.e. 21276 steps/sec (display mode is false) */
/* benchmark took 63029 ms, i.e. 15873 steps/sec (display mode is false) */
/* benchmark took 50172 ms, i.e. 20000 steps/sec (display mode is false) */
/* average benchmark took ~ 56 s,  i.e. ~ 18000 steps/s */

/* username : Vincent Besson */
/* date : 2005/05/10 */
/* hardware (speed,ram,video) and OS description : P4 3.4 GHz, ram 512 Mo, NVidia Quadro NVS 64 Mo, Windows XP Pro (2002 SP1) */
/* notes : - often the robot stay blocked in a corner, but I let end the benchmark;
/* 		   - only in windows, I get sometimes "java.lang.OutOfMemoryError". I reboot Eclipse and it works;
/* 		   - when I run Benchmark with totalSteps = 1000000 i get memory heaps. Anyway, I got these results: */
/* display mode is true*/
/* benchmark took 65719 ms, i.e. 15384 steps/sec (display mode is true) */
/* benchmark took 57375 ms, i.e. 17543 steps/sec (display mode is true) */
/* benchmark took 56250 ms, i.e. 17857 steps/sec (display mode is true) */
/* benchmark took 42328 ms, i.e. 23809 steps/sec (display mode is true) */
/* average benchmark took ~ 55 s,  i.e. ~ 18600 steps/s */
/* display mode is false*/
/* benchmark took 61891 ms, i.e. 16393 steps/sec (display mode is false) */
/* benchmark took 56844 ms, i.e. 17857 steps/sec (display mode is false) */
/* benchmark took 78968 ms, i.e. 12820 steps/sec (display mode is false) */
/* benchmark took 62469 ms, i.e. 16129 steps/sec (display mode is false) */
/* average benchmark took ~ 65 s,  i.e. ~ 15800 steps/s */

/* ********************************************************************************************************************************** */
