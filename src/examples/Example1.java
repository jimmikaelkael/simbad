package examples;


import simbad.gui.Simbad;
import simbad.sim.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;


/**
  Derivate your own code from this example.
 */


public class Example1 {

    /** Describe the robot */
    static public class Robot extends Agent {

        RangeSensorBelt sonars;
        CameraSensor camera;

        public Robot(Vector3d position, String name) {
            super(position, name);
            // Add camera
            camera = RobotFactory.addCameraSensor(this);
            // Add sonars
            sonars = RobotFactory.addSonarBeltSensor(this);
        }

        /** This method is called by the simulator engine on reset. */
        public void initBehavior() {
            // nothing particular in this case
        }

        /** This method is call cyclically (20 times per second)  by the simulator engine. */
        public void performBehavior() {

            // progress at 0.5 m/s
            setTranslationalVelocity(0.5);
            // frequently change orientation
            if ((getCounter() % 100) == 0)
                setRotationalVelocity(Math.PI / 2 * (0.5 - Math.random()));

            // print front sonar every 100 frames
            if (getCounter() % 100 == 0)
                System.out
                        .println("Sonar num 0  = " + sonars.getMeasurement(0));

        }
    }

    /** Describe the environement */
    static public class MyEnv extends EnvironmentDescription {
        public MyEnv() {
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
            Box b1 = new Box(new Vector3d(-3, 0, -3), new Vector3f(1, 1, 1),
                    this);
            add(b1);
            add(new Arch(new Vector3d(3, 0, -3), this));
            add(new Robot(new Vector3d(0, 0, 0), "robot 1"));

        }
    }

    public static void main(String[] args) {
        // request antialising
        System.setProperty("j3d.implicitAntialiasing", "true");
        // create Simbad instance with given environment
        Simbad frame = new Simbad(new MyEnv(), false);
    }

} 