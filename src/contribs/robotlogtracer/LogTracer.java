/*
 * Created on 13 mai 2005
 * 
 * Tobot log tracer - write the trajectory of a robot into an image file. 
 *
 */
package contribs.robotlogtracer;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;



/**
 * This class will take a structure proposing the trace to create a png file (or a jpg) will be created.
 * In the basic, i will use a pixel matrix, ie an array of array. This matrix defines a density of passages
 * of the robots on each cases of the matrix. The more it went on a place, the darker the trace will be.
 * 
 * How it works : 
 * 
 *  The trace is represented by some kind of pixel Matrix. It is an integer matrix (ie int[][]).
 * The value of the integers at the position [i][j] represent how much the robot went on the 
 * position [i][j] in the environment. So the more it went, the darker the trace will be ( because 
 * the value will be greater )
 * 
 * In supplement, it is possible to plot points on specific locations with specific
 * colors. For example i do put a red point on my starting position adding the position 
 * with the setBeginTrace(x,y) function
 * For the collisions, i can add a second matrix to plot the collisions with green points.
 * 
 * It is also now possible to trace manualy a point with the function : tracePoint(..) 
 * 
 * 
 * TODO : Tracing the walls should be hand-written
 * 
 * @author Cedric Hartland
 *
 */
public class LogTracer extends JFrame {

	private static final long serialVersionUID = 1L;
	private String _path = "log/trace-"+(System.currentTimeMillis()/1000);
	
	private String _type = "png";
	
	private final String [] _stringType = ImageIO.getWriterFormatNames();
	
	private int _startingX;
	private int _startingZ;
	
	/**
	 * Default constructor.
	 *
	 */
	public LogTracer  () {	}
	
	/**
	 * Second constructor, take the path and the name of the (PNG) file to create.  
	 * @param __pathFile the formated \"path/filename.png\" to create
	 */
	public LogTracer (String __pathFile)
	{
		_path=__pathFile;
	}
	
	/**
	 * Thrid constructor, takes a path and a file type (image type). The pah may be empty if the default is to be used.
	 * @param __pathFile
	 * @param __type
	 */
	public LogTracer (String __pathFile, String __type)
	{
		if (__pathFile!="") _path=__pathFile;
		
		boolean found=false;
		int i=0;
		while (i<_stringType.length && !found)
		{
			if (_stringType[i]==_type)
			{ 
			_type=__type;
			found=true;
			}
			i++;
		}
		if (!found)
		{
			_type="png";
			System.err.println("Warning : the given type of file is unsupported, please ensure the format is correct");
			System.err.println("Warning : the file type for the traces will be png instead");			
		}
	}
	
	/**
	 * decide the type from the path name, else, keep the default one.
	 *
	 */
	private void setType(){
		try 
		{
		// if the last four are to be the file type, then, we can change the file type
		// else we do nothing but just test for the last char to be a dot, else we add
		char dot = _path.charAt(_path.length()-4);
		if (dot=='.') 
		{
			String type = _path.substring(_path.length()-3,_path.length());
			boolean found=false;
			int i=0;
			while (i<_stringType.length && !found)
			{
				if (_stringType[i].compareTo(type)==0)
				{ 
				_type=type;
				found=true;
				}
				i++;
			}
			_path=_path.substring(0,_path.length()-4);
			if (!found)
			{
				_type="png";
				System.err.println("Warning : the given type of file is unsupported, please ensure the format is correct");
				System.err.println("Warning : the file type for the traces will be png instead");
				System.err.flush();
			}
		}
		}
		catch(Exception e) 
		{ 
			//do nothing 
		}
	}
	
	/**
	 * trace a point on the graphic
	 * @param bi
	 * @param colour
	 * @param width
	 * @param height
	 */
	private void tracePoint(BufferedImage bi, int colour, int width, int height)
	{
		// define the range of the coords
		int beginX;
		int endX;
		int beginZ;
		int endZ;
		
		if (this._startingX<10) beginX=0;
		else beginX=this._startingX-10;
		if (this._startingX>height-10) endX=height;
		else endX=this._startingX+10;
		
		if (this._startingZ<10) beginZ=0;
		else beginZ=this._startingZ-10;
		if (this._startingZ>width-10) endZ=width;
		else endZ=this._startingZ+10;
		
		for(int i=beginX; i<endX; i++) bi.setRGB(i,this._startingZ,colour);
		for(int i=beginZ; i<endZ; i++) bi.setRGB(this._startingX,i,colour);		
	}
	
	/**
	 * trace a point on the graphic
	 * @param bi
	 * @param colour
	 * @param width
	 * @param height
	 * @param x
	 * @param z
	 */
	public void tracePoint(BufferedImage bi, int colour, int width, int height, int x, int z)
	{
		// define the range of the coords
		int beginX;
		int endX;
		int beginZ;
		int endZ;
		
		if (x<10) beginX=0;
		else beginX=x-10;
		if (x>height-10) endX=height;
		else endX=x+10;
		
		if (z<10) beginZ=0;
		else beginZ=z-10;
		if (z>width-10) endZ=width;
		else endZ=z+10;
		
		for(int i=beginX; i<endX; i++) bi.setRGB(i,z,colour);
		for(int i=beginZ; i<endZ; i++) bi.setRGB(x,i,colour);		
	}
	
	public void setBeginTrace(int __x, int __z)
	{
		this._startingX=__x;
		this._startingZ=__z;
	}
	
	/**
	 * This function commits the trace of the pixel matrix to a precise filename and path. The default output generated will be a png.
	 * @param traceMatrix the trace matrix that will be used to generate a file.
	 * @param __path the path and the name of the file to generate
	 */
	public void commitTrace(int [] [] traceMatrix, String __path)
	{
		_path = __path;
		commitTrace(traceMatrix);
	}
	
	/**
	 * This function commits the trace of the pixel matrix to a precise filename and path. The default output generated will be a png.
	 * @param traceMatrix the trace matrix that will be used to generate a file.
	 * @param __path the path and the name of the file to generate
	 */
	public void commitTrace(int [] [] traceMatrix, String __path, boolean [][] collisionMatrix)
	{
		_path = __path;
		commitTrace(traceMatrix,collisionMatrix);
	}
	
	/**
	 * Get the traceMatrix and write the image corresponding to a file. The height/width length of the matrix will be the same as the height/width of the image.
	 * The trace matrix represents the arena and the number of times the robots
	 * have been on a quadrant of it. The more it went, the darker the trace 
	 * will be.
	 * @param traceMatrix the trace matrix generated by the robot
	 */
	public void commitTrace(int [][] traceMatrix)
	{
		setType();
		
		//define the height/width of the image
		int height=traceMatrix.length;
		int width=traceMatrix[0].length; 
		
		// integrity checks
		for(int i=0;i<traceMatrix.length;i++)
		{
			if (traceMatrix[i].length!=width)
			{
				System.err.println("The pixel matrix is not well formed, it should be X*Y but some of its columns are not the right size");
				return;
			}
		}
		
		//define the BufferedImage 
		BufferedImage bufferedImage = new BufferedImage(height,width,BufferedImage.TYPE_INT_RGB);	
		
		//the trace define an intensity of passage to a pixel point, the more it is visited, the darker it will be.
		int value,col;
		int alpha = 255;
		int red = 255;
		int green = 255;
		int blue = 255;
		// we fill the BufferedImage
		for(int i=0; i<height;i++)
		{
			for(int j=0;j<width;j++)
			{
				// to be visible, we define grey traits on white board
				value= Math.min(255,10*(traceMatrix[i][j]));
				col =  (alpha << 24) |  ((red-value) << 16) |  ((green-value) << 8 ) | (blue-value);
				bufferedImage.setRGB(i,j,col);
			}
		}
		
		// set the starting point
		if (this._startingX!=0 || this._startingZ!=0)
		{
			col =  (alpha << 24) |  ((red) << 16) |  ((green-255) << 8 ) | (blue-255);
			this.tracePoint(bufferedImage,col,width,height);
		}
		
		// finaly we write the BufferedImage to a file
		try {
			File f = new File(_path+"."+_type);
			ImageIO.write( bufferedImage , _type , f );
		}
		catch(Exception e){
			System.out.println("Error append during the creation of the image");
			System.out.println("The process will now try to write it in the root of the classpath");
			try {
				System.err.println("Warning : the file could not be created, another try with the default settings will be computed on the root path");
				_path = "trace-"+(System.currentTimeMillis()/1000)+"."+this._type;
				File f = new File(_path);
				ImageIO.write( bufferedImage , _type , f );
			}
			catch(Exception e2){ e.printStackTrace(); }
		}
	}
	
	/**
	 * 	 * Get the traceMatrix and write the image corresponding to a file. The height/width length of the matrix will be the same as the height/width of the image.
	 * The trace matrix represents the arena and the number of times the robots
	 * have been on a quadrant of it. The more it went, the darker the trace 
	 * will be.
	 * @param traceMatrix the trace matrix generated by the robot
	 * @param collisionMatrix define the points where the robot did hit the wall
	 */
	public void commitTrace(int [][] traceMatrix, boolean [][] collisionMatrix)
	{
		setType();
		
		//define the height/width of the image
		int height=traceMatrix.length;
		int width=traceMatrix[0].length; 
		
		// integrity checks
		for(int i=0;i<traceMatrix.length;i++)
		{
			if (traceMatrix[i].length!=width)
			{
				System.err.println("The pixel matrix is not well formed, it should be X*Y but some of its columns are not the right size");
				return;
			}
		}
		for(int i=0;i<collisionMatrix.length;i++)
		{
			if (collisionMatrix[i].length!=width)
			{
				System.err.println("The pixel matrix is not well formed, it should be X*Y but some of its columns are not the right size");
				return;
			}
		}
		
		//define the BufferedImage 
		BufferedImage bufferedImage = new BufferedImage(height,width,BufferedImage.TYPE_INT_RGB);	
		
		//the trace define an intensity of passage to a pixel point, the more it is visited, the darker it will be.
		int value,col;
		int alpha = 255;
		int red = 255;
		int green = 255;
		int blue = 255;
		// we fill the BufferedImage
		for(int i=0; i<height;i++)
		{
			for(int j=0;j<width;j++)
			{
				// to be visible, we define grey traits on white board
				value= Math.min(255,10*(traceMatrix[i][j]));
				col =  (alpha << 24) |  ((red-value) << 16) |  ((green-value) << 8 ) | (blue-value);
				bufferedImage.setRGB(i,j,col);
			}
		}
		
		// put points on the colision positions
		for(int i=0; i<height;i++)
		{
			for(int j=0;j<width;j++)
			{
				if (collisionMatrix[i][j])
				{
					col =  (alpha << 24) |  ((red-255) << 16) |  ((green) << 8 ) | (blue-255);
					this.tracePoint(bufferedImage,col,width,height,i,j);
				}
			}
		}
		
		// set the starting point
		if (this._startingX!=0 || this._startingZ!=0)
		{
			col =  (alpha << 24) |  ((red) << 16) |  ((green-255) << 8 ) | (blue-255);
			this.tracePoint(bufferedImage,col,width,height);
		}
		
		
		// finaly we write the BufferedImage to a file
		try {
			File f = new File(_path+"."+_type);
			ImageIO.write( bufferedImage , _type , f );
		}
		catch(Exception e){
			System.out.println("Error append during the creation of the image");
			System.out.println("The process will now try to write it in the root of the classpath");
			try {
				System.err.println("Warning : the file could not be created, another try with the default settings will be computed on the root path");
				_path = "trace-"+(System.currentTimeMillis()/1000)+"."+this._type;
				File f = new File(_path);
				ImageIO.write( bufferedImage , _type , f );
			}
			catch(Exception e2){ e.printStackTrace(); }
		}
	}
	

	
	/**
	 * TEST: used to test the class.
	 * @param args the arguments ...
	 */
	public static void main(String[] args) 
	{
		int [][] trace = new int [256][256];
		int k=0;
		for(int i=0;i<trace.length;i++)
		{
			for(int j=0;j<trace[i].length;j++)
			{
				if (i==100 & j<100) trace[i][j]=(k++)/10;
				if (j==100 & i>100 & i<180) trace[i][j]=(k++)/10;
				if (i==180 & j>100) trace[i][j]=(k++)/10;
				
				//trace[i][j]=rand.nextInt((k++/1000)+1);
			}
		}
		
		LogTracer tr = new LogTracer("trace.png");
		tr.commitTrace(trace);
		//tr.setType();
	}
}
