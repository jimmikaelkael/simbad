#
#  Simbad - Robot Simulator
#  Copyright (C) 2004 Louis Hugues
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful, 
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
# -----------------------------------------------------------------------------
# $Author$ 
# $Date$
# $Revision$
# $Source$
#

from simbad.gui import *
from simbad.sim import *
from  javax.vecmath import *

# This example shows a controller usiing a small plan 
# containing turle like instructions.
#
class MyRobot(Agent):
	""" a robot controller """

	def initBehavior(self):
		# The plan we want to execute
		self.plan = [['turn',90],['move', 2],
				     ['turn',90],['move', 2],
                     ['turn',90],['move', 2],
                     ['turn',90],['move', 2]]
		# a counter representing time left for current action 
		self.count = 0 
	
	def performBehavior(self):
		# when counter exhausted, fetch next action
		if self.count<1: self.nextAction()
		self.count -=1 
	
	def nextAction(self):
		""" set up the next action """
		fps = self.getFramesPerSecond()
		if len(self.plan)==0 : 
			self.setvel(0,0)
			return
		action = self.plan.pop(0)
		print action
		# a turn action ?
		if action[0] == 'turn':
			rv = 0.5
			self.count= fps*(degToRad(action[1]))/rv
			self.setvel(0,rv)
		# a move action
		elif action[0] =='move':
			tv = 0.5
			self.count = fps*action[1]/tv
			self.setvel(tv,0)

	def setvel(self , tv,rv):
		""" shortcut for setting velocities"""		
		self.setTranslationalVelocity(tv)
		self.setRotationalVelocity(rv)


def degToRad(deg):
	""" convert degrees to radians """
	return  deg * (3.1416/180.0)

# description of the environment 
class MyEnv(EnvironmentDescription):
	def __init__(self):
		# place a  robot in the environment
		self.add(MyRobot(Vector3d(0,0,0),"robot with python"))
		# place two boxes 
		self.add(Box(Vector3d(-3, 0, -3), Vector3f(1, 1, 1),self))
		self.add(Box(Vector3d(2, 0, 3), Vector3f(1, 1, 1),self))

# launch simbad 
simbad = Simbad(MyEnv(),0)
	
	
