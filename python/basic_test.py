from simbad.gui import *
from simbad.sim import *
from  javax.vecmath import *

# a very simple robot controller
class MyRobot(Agent):
	def initBehavior(self):
		# nothing to do
		pass
	
	def performBehavior(self):
		if self.collisionDetected():
			self.setTranslationalVelocity(0)
		else:
			self.setTranslationalVelocity(0.2)

# description of the environment 
class MyEnv(EnvironmentDescription):
	def __init__(self):
		# put a  robot 
		self.add(MyRobot(Vector3d(0,0,0),"robot with python"))
		# put a box 
		self.add(Box(Vector3d(3, 0, 0), Vector3f(1, 1, 1),self))

# launch simbad 
simbad = Simbad(MyEnv(),0)
	
	
