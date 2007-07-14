/*
 * Created on 18 nov. 2005
 * credits: largely inspired from : http://www.dickbaldwin.com/java/Java174.htm#pixelgrabber%20class%20class
 * 
 * important methods are : constructor, setPixel, getPixel, getImage
 * 
 */

package contribs.maploader;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;



class SimpleImage extends Frame {

	private static final long serialVersionUID = -1L;

	private Image _image;//ref to raw image file fetched from disk
	
	// File references
	private String _filename; // name of the image file
	private int _width; // width of the image file
	private int _height; // height of the image file
	
	// the pixel matrix representing the image rbg pixels
	private int[] _pixelMatrix;


	public SimpleImage( String __filename , boolean __display)
	{
		// step 1 : load the image (jpeg, gif or png)	
		_filename = __filename;
		_image = Toolkit.getDefaultToolkit().getImage(_filename);
	
	    MediaTracker mediaTracker = new MediaTracker(this);
	    mediaTracker.addImage(_image,1);
	
	    try{ // (1) wait for image to be loaded (2) timeout in 10s 
	    		if( !mediaTracker.waitForID(1,10000) )
	    		{
	    			// indicates where the file has been searched, better when 
	    			// you don't know where the file should be.
	    			System.out.println("[error] image could not be loaded.");
	    			System.out.println("  please verify that the file exists : ");
	    			System.err.println("  "+System.getProperty("user.dir")+System.getProperty("file.separator")+_filename);
	    			System.exit(-1);        
	    		}
	    }
	    catch(InterruptedException e) { e.printStackTrace(); }    

	    // step 2 : register the image 
	    
	    _width = _image.getWidth(this);
	    _height = _image.getHeight(this);
	    _pixelMatrix = new int [ _width * _height ];

	    try{
	    		PixelGrabber pixelGrabber = new PixelGrabber(_image,0,0,_width,_height,_pixelMatrix,0,_width);
	    		if( ! ( pixelGrabber.grabPixels() && ((pixelGrabber.getStatus() & ImageObserver.ALLBITS) != 0) ) )
	    			System.out.println("[error] grabbing failed.");
	    }
	    catch (InterruptedException e) { e.printStackTrace(); }
	    
	    // step 3 : if you want to display the image, display it
	    
	    if ( __display == true ) preparePaint();
	}
	
	/**
	 * get the raw pixel
	 * @param __x position of x
	 * @param __y position of y
	 * @return the raw pixel value
	 */
	public int getRawPixel ( int __x, int __y )
	{
		return ( _pixelMatrix[__y*_width+__x] );
	}
	
	/**
	 * set the raw pixel value
	 * @param __x position of x
	 * @param __y position of y
	 * @param __rawvalue the new raw value of the pixel to set.
	 */
	public void setRawPixel ( int __x, int __y , int __rawvalue )
	{
		_pixelMatrix[__y*_width+__x] = __rawvalue;
	}
	
	/** 
	 * return : alpha, red, green, blue (each 256 bits value)
	 */
	public int[] getPixel ( int __x, int __y )
	{
		int values[] = new int[4];
		int rawvalue = _pixelMatrix[__y*_width+__x];
		values[0] = ( rawvalue & 0xFF000000 ) / (int)Math.pow(256,3);
		values[1] = ( rawvalue & 0x00FF0000 ) / (int)Math.pow(256,2);
		values[2] = ( rawvalue & 0x0000FF00 ) / 256;
		values[3] = ( rawvalue & 0x000000FF );
		return ( values );
	}
	
	/**
	 * Set the value of a pixel (x,y)
	 * @param __x coord x
	 * @param __y coord y
	 * @param __alpha alpha value (0-255)
	 * @param __red red value (0-255)
	 * @param __green green value (0-255)
	 * @param __blue blue value (0-255)
	 */
	public void setPixel ( int __x, int __y , int __alpha, int __red, int __green, int __blue )
	{
		if ( __red > 255 || __green > 255 || __blue > 255 || __alpha > 255 )
		{
			System.out.println("[warning] setPixel values must be between 0 and 255.");
			return;
		}
		_pixelMatrix[__y*_width+__x] = __alpha * (int)Math.pow(256,3) + __red * (int)Math.pow(256,2) + __green * 256 + __blue ;
	}

	
	
	public Image getImage ()
	{
		return ( this.createImage( new MemoryImageSource( _width, _height, _pixelMatrix, 0, _width) ) );
	}
	
	/**
	 * returns the width of the image.
	 */
	public int getWidth()
	{
		return (_width);
	}
	
	/**
	 * returns the height of the image.
	 */
	public int getHeight()
	{
		return (_height);
	}

	/**
	 * Displays informations about the picture loaded.
	 */
	public void displayInformation ()
	{
		System.out.println("filename: " + _filename);
		System.out.println("width   : " + _width);
		System.out.println("height  : " + _height);
	}
  
	/**
	 * Display the image in a frame. 
	 */
	public void preparePaint()
	{
	    this.setVisible(true);
	    	    
	    //inTop = this.getInsets().top;
	    //inLeft = this.getInsets().left;

	    this.setSize(this.getInsets().left+_width,this.getInsets().top+_height);
	    this.setTitle("Image viewer");
	    this.setBackground(Color.black);
	    
	    this.addWindowListener(new WindowAdapter(){
	    	public void windowClosing(WindowEvent e){System.exit(0);}
	    	});
	}

	/**
	 * paint the graphics in the frame.
	 */
	public void paint(Graphics g)
	{
		Image image = getImage();
		g.drawImage(_image,this.getInsets().left,this.getInsets().top,this); // image at loading time
		g.drawImage(image,this.getInsets().left,this.getInsets().top+_height,this); // current image in memory
	}

	/* *** maintenant amusons nous un peu *** */
	
	public void exampleImageManipulation_1 ()
	{
		/*
		for( int i = 0; i < (_width*_height) ; i++ )
			_pixelMatrix[i] = _pixelMatrix[i] & 0xFFFFFF;  // alpha, R, G, B -- alpha=255=maxOpacity
		// ...SAME AS...
		**/
		for ( int x = 0 ; x != _width ; x++ )
			for ( int y = 0 ; y != _height ; y++ )
			{
				int[] values = getPixel(x,y);
				setPixel (x,y,values[0],values[1],values[2],values[3]); // alpha, R, G, B -- alpha=255=maxOpacity
			}
	}
	
	public void exampleImageManipulation_2 ()
	{
		for ( int x = 0 ; x != _width ; x++ )
			for ( int y = 0 ; y != _height ; y++ )
			{
				int[] values = getPixel(x,y);
				setPixel (x,y,255,(values[1]+1)%256,(values[2]+1)%256,(values[3]+1)%256); // alpha, R, G, B -- alpha=255=maxOpacity
			}
	}

	public void exampleImageManipulation_3 ()
	{
		for ( int x = 10 ; x != _width-10 ; x++ )
			for ( int y = 10 ; y != _height-10 ; y++ )
			{
				int[] values = getPixel((int)((double)x-8*Math.sin(x)),(int)((double)y-4*Math.cos(y)));
				setPixel (x,y,255,(values[1]+1)%256,(values[2]+1)%256,(values[3]+1)%256); // alpha, R, G, B -- alpha=255=maxOpacity
			}
	}

	
	
	/* Main method for test and demo purpose */
	
	public static void main(String[] args)
	{
		// Display Examples
		/*
		boolean display = false;
		SimpleImage simpleimage = new SimpleImage("nicolas/test.gif", display);
		simpleimage.displayInformation();
		while (true) 
		{ 
			simpleimage.exampleImageManipulation_3();
		}
		//if ( display ) obj.repaint();
		*/
		
		
		// Maze Display example
		boolean display = false;
		SimpleImage simpleimage = new SimpleImage("ressources/map1.png", display);
		simpleimage.displayInformation();
		
		for ( int y = 0 ; y != simpleimage.getHeight() ; y++ )
		{
			for ( int x = 0 ; x != simpleimage.getWidth() ; x++ )
			{
				int [] values = simpleimage.getPixel(x,y);
				if ( values[1] == 255 && values[2] == 0 && values[3] == 0 ) 
					System.out.print("#");
				else
					if ( values[1] == 0 && values[2] == 255 && values[3] == 0 )
						System.out.print("X");
					else
						if ( values[1] == 0 && values[2] == 0 && values[3] == 255 )
							System.out.print("!");
						else
							System.out.print(" ");
			}
			System.out.print("\n");
		}
			
	}
	
  }