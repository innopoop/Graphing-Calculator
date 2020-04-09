import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import java.awt.*;
import java.util.regex.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Graphing {
	public static void main(String[] args) {
		//creates JFrame containing all the components
		guiFrame bkgrd = new guiFrame();
		//default close function
		bkgrd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//set JFrame to visible
		bkgrd.setVisible(true);
	}
}
//extends JFrame (window function)
class guiFrame extends JFrame{
	//JComponents declared in class so we can access them all thoughout
	private static JPanel promptBox;
	private static JPanel equationPrompt;
	private static JPanel key;
	private static JPanel extraInfo;
	private static JPanel extraPoints;
	private static JPanel ftcPanel;
	private static JTextField input;
	private static JButton enterBT;
	private static JLabel legend;
	//all of the checkboxes to toggle graphs
	private static JCheckBox original;
	private static JCheckBox der_1;
	private static JCheckBox der_2;
	private static JCheckBox ftc;
	private static JCheckBox integrate;
	
	//scaling
	private static JPanel scaleBox;
	private static JLabel scalePrompt;
	private static JTextField scale;
	private static int xScale;
	private static int yScale;
	private static int xOrigin;
	private static int yOrigin;
	private static int yMax;
	private static int xMax;
	
	//min/max/inflections
	private static JPanel min;
	private static JPanel max;
	private static JPanel inflection;
	private static JPanel inflection2;
	private static ArrayList<Double> min_x;
	private static ArrayList<Double> min_y;
	private static ArrayList<Double> max_x;
	private static ArrayList<Double> max_y;
	private static ArrayList<Double> inf_x;
	private static ArrayList<Double> inf_y;
	
	//hole
	private static ArrayList<Double> hole_x;
	private static ArrayList<Double> hole_y;
	
	//integration variables
	private static JTextField lowerBound;
	private static JTextField upperBound;
	//tells us where integral is shown
	private static JLabel integral;
	//displays integral
	private static JLabel defIntegralString;
	//integral value
	private static Double defIntegral;
	private static int a_val;
	private static int b_val;
	
	//graphing necessities: x/y value array + discontinuities (derivatives are only y values bc x values are always the same)
	private static double[] x;
	private static Double[] y;
	private static Double[] derivative_1;
	private static Double[] derivative_2;
	private static String[] discontinuities;
	private static String[] der_1_dc;
	private static String[] der_2_dc;
	
	//assumes that a discontinuity is simply out of range until a hole/asymptote is found
	private static boolean outOfDomain = true;
	
	//nice colors
	private static Color marineBlue = new Color(35,156,196);
	private static Color grassGreen = new Color(20,135,68);
	private static Color darkPurple = new Color(124,13,130);
	private static Color lightPink = new Color(252,151,252);
	private static Color brown = new Color(84,60,67);
	
	//class constructor that inherits from JFrame and in effect creates a JFrame upon construction
	public guiFrame() {
		
		//sets title of window to "Graphing Calculator" and size initiation; reminds users to put fractions in parentheses cause finding discontinuities/MathEvaluator requires it
		super("Graphing Calculator: ***PUT FRACTIONS IN PARENTHESES!***");
		
		//set size pixels
		setSize(1080,1080);

		//handler class used to manage button interactions (defined below)
		HandlerClass handler = new HandlerClass();
		
		//create new panel and textfield for equation
		promptBox = new JPanel();
		promptBox.setLayout(new BoxLayout(promptBox, BoxLayout.Y_AXIS));
		
		//prompts equation and gets input
		equationPrompt = new JPanel();
		input = new JTextField(50);
		
		//creates scaling panel and adds input field and text
		scaleBox = new JPanel();
		scalePrompt = new JLabel("Enter a positive integer to scale the graph by: ");
		//content used to scale graph
		scale = new JTextField(10);
		scaleBox.add(scalePrompt);
		scaleBox.add(scale);
		//create button
		enterBT = new JButton("Go!");
		
		//action listener looks for the pressing of the button using handler class object
		enterBT.addActionListener(handler);
		
		//create text for prompt & add all components
		equationPrompt.add(new JLabel("Enter equation here:"));
		
		//add components
		equationPrompt.add(input);
		scaleBox.add(enterBT, Component.RIGHT_ALIGNMENT);
		
		//add panels
		promptBox.add(equationPrompt);
		promptBox.add(scaleBox);
		
		//a panel that will be used to hold legend, min, max, & inflection panels
		extraInfo = new JPanel();
		extraInfo.setPreferredSize(new Dimension(300,1000));
		
		//holds the legend and all checkboxes (used to repaint graph)
		key = new JPanel();
		
		//makes all components inside that are added go underneath the previous component
		key.setLayout(new BoxLayout(key, BoxLayout.Y_AXIS));
		
		//legend tells user which colors are which
		legend = new JLabel("<html>KEY:</font><br/><font color=rgb(35,156,196)>Original Function\tBLUE</font><br/><font color=rgb(20,135,68)>First Derivative\tGREEN</font><br/><font color=rgb(124,13,130)>Second Derivative\tMAGENTA</font><br/><font color=rgb(252,151,252)>Min/Max\tLIGHT PINK</font><br/><font color=rgb(84,60,67)>Inflection Pts\tBROWN</font></html>");
		
		//creates checkboxes that will be added to the key panel
		original = new JCheckBox("See Original Function");
		der_1 = new JCheckBox("See First Derivative");
		der_2 = new JCheckBox("See Second Derivative");
		ftc = new JCheckBox("<html>See FTC on f'(x) </br>[Enter value for a and b!]</html>");
		integrate = new JCheckBox("Integrate with Trapezoid Rule");
		
		//BoxHandler class is used to look for when the boxes are checked or not and do something if they are/aren't
		BoxHandler check = new BoxHandler();
		
		//add ItemListener (check for selection) class to each checkbox
		original.addItemListener(check);
		der_1.addItemListener(check);
		der_2.addItemListener(check);
		ftc.addItemListener(check);
		integrate.addItemListener(check);
		
		//creates text fields for user input on 'a' and 'b' for integration
		upperBound = new JTextField(10);
		lowerBound = new JTextField(10);
		
		//add legend and all checkboxes to the key panel
		key.add(legend);
		key.add(original);
		key.add(der_1);
		key.add(der_2);
		key.add(ftc);
		key.add(integrate);
		
		//new Panel that will hold all of the integration stuff (a, b, and the value of the integral)
		ftcPanel = new JPanel();
		
		//vertical layout
		ftcPanel.setLayout(new BoxLayout(ftcPanel, BoxLayout.Y_AXIS));
		
		//define JLabel variable that will tell user the integral value is: 
		integral = new JLabel("<html><body>The integral of f'(x)<br> from a to b is:</body></html>");
		
		
		//JLabel that will showcase the String version of the definite Integral (it's on its own bc I can change it using setText())
		defIntegralString = new JLabel("");
		//make the text red to stand out
		defIntegralString.setForeground(Color.RED);
		
		//add new a and b labels along with their textfields
		ftcPanel.add(new JLabel("a"));
		ftcPanel.add(lowerBound);
		ftcPanel.add(new JLabel("b"));
		ftcPanel.add(upperBound);
		//add integral & definite integral string
		ftcPanel.add(integral);
		ftcPanel.add(defIntegralString);
		
		//new JPanel that will hold min, max, and inflection points
		extraPoints = new JPanel();
		//set the layout to be a grid with 2 rows and 2 column with no space in between
		extraPoints.setLayout(new GridLayout(2,2,0,0));
		
		//**all point panels have a vertical layout**
		//Panel to hold all minimum values
		min = new JPanel();
		min.setLayout(new BoxLayout(min, BoxLayout.Y_AXIS));
		
		//Panel to hold all maximum values
		max = new JPanel();
		max.setLayout(new BoxLayout(max, BoxLayout.Y_AXIS));
		
		//two panels to hold all inflection points because sin/cos have a lot of inflection points...
		inflection = new JPanel();
		inflection.setLayout(new BoxLayout(inflection, BoxLayout.Y_AXIS));
		inflection2 = new JPanel();
		inflection2.setLayout(new BoxLayout(inflection2, BoxLayout.Y_AXIS));
		
		//add all four panels to extraPoints panel (in the order they are added is how they are formatted)
		extraPoints.add(min);
		extraPoints.add(max);
		extraPoints.add(inflection);
		extraPoints.add(inflection2);
		extraPoints.setPreferredSize(new Dimension(300,600));
		
		//this makes all of the components inside of the extraInfo panel (The panel that has ALL OTHER PANELS) align to the left
		extraInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		//add all three panels in the order it should be seen
		extraInfo.add(key);
		extraInfo.add(ftcPanel);
		extraInfo.add(extraPoints);
		
		//add prompt panel and extra information panels onto jframe window using layout below
		add(promptBox, BorderLayout.NORTH);
		add(extraInfo, BorderLayout.EAST);
		
	}
	
	//Handler class to watch for checkbox interaction
	private class BoxHandler implements ItemListener{
		 //override method in class called itemStateChanged (when state changed do this...)
		 public void itemStateChanged(ItemEvent event) {
			 
			 //if FTC checkbox is selected && the 'a' and 'b' textfields are NOT empty
			 if(ftc.isSelected() && !lowerBound.getText().equals("") && !upperBound.getText().equals("")) {
				 
				 //try parsing the input of the boxes to double values
				 try{
					 double a = Double.parseDouble(lowerBound.getText());
					 double b = Double.parseDouble(upperBound.getText());
					 //if the values parse, check to see if they are within the bounds of the graph
					 if(a <= getScale() && a >= -getScale() && b <= getScale() && b >= -getScale() ) {
						 
						 //call the FTC function defined later
						 funTheoryCalc(a,b);
						 
						 //convert the defIntegral double value found in the function to a BigDecimal object so that we can format it
						 BigDecimal defIntegralRound = new BigDecimal(defIntegral);
						 
						 //rounds the value to 5 decimals
						 defIntegralRound = defIntegralRound.setScale(5, RoundingMode.HALF_UP);
						 
						 //change the defIntegralString JLabel that's on the ftcPanel to the newly calculated value
						 defIntegralString.setText(defIntegralRound.toString());
					 }
				 }
				 //catch the error w/ random error because i'm too lazy to find the right error
				 catch(NumberFormatException e){
					 System.out.println("Error: input values for a and b");
				 }
			 }
			 
			 //if integrateByTrapezoid checkbox is selected && the 'a' and 'b' textfields are NOT empty
			 if(integrate.isSelected()&& !lowerBound.getText().equals("") && !upperBound.getText().equals("")) {
				 
				 //try to parse & same as the FTC checkbox
				 try{
					 double a = Double.parseDouble(lowerBound.getText());
					 double b = Double.parseDouble(upperBound.getText());
					 if(a <= getScale() && a >= -getScale() && b <= getScale() && b >= -getScale() ) {
						 //defined below
						 trap(a,b);
						 BigDecimal defIntegralRound = new BigDecimal(defIntegral);
						 defIntegralRound = defIntegralRound.setScale(5, RoundingMode.HALF_UP);
						 defIntegralString.setText(defIntegralRound.toString());
					 }
				 }
				 catch(NumberFormatException e){
					 System.out.println("Error: input values for a and b");
				 }
			 }
			 
			 //if FTC button & integrateByTrapezoid is NOT selected
			 if(!ftc.isSelected() && !integrate.isSelected()) {
				 //set the defIntegralString to an empty string
				 defIntegralString.setText("");
			 }
			 //repaints the graph using paintComponent defined later
			 revalidate();
			 repaint();
		 }
	}
	
	//HandlerClass to manage BUTTON interaction
	private class HandlerClass implements ActionListener{
		//override the parent functions actionPerformed method to specify our own commands
		@Override
		public void actionPerformed(ActionEvent event) {
			
			//if no scale input, stop function
			if(scale.getText().equals("")) {
				return;
			}
			
			//calculates and stores values of x & y to double arrays (defined below)
			findPoints();
			
			//calculate derivative values using method defined below
			derive(y,derivative_1,der_1_dc);
			derive(derivative_1,derivative_2,der_2_dc);
			
			//clear the panels at every press of the button so that the window is clean
			min.removeAll();
			max.removeAll();
			inflection.removeAll();
			inflection2.removeAll();
			
			//set all boxes except showOriginalFunction checkbox to unchecked
			integrate.setSelected(false);
			der_1.setSelected(false);
			der_2.setSelected(false);
			ftc.setSelected(false);
			
			//redefine min & max arrays at every press of the button b/c different equations are being put in
			min_x = new ArrayList<Double>();
			min_y = new ArrayList<Double>();
			max_x = new ArrayList<Double>();
			max_y = new ArrayList<Double>();
			
			//checkExtrema function defined below
			checkExtrema(min_x,min_y,max_x,max_y,derivative_1);
			
			//add to the min panel: Title formatted by inline html tags
			min.add(new JLabel("<html><font color= 'orange'>Minimum values:</font><br/></html>"));
			
			//for i=0 to i<min_x arraylist size
			for(int i = 0; i < min_x.size(); i++) {
				//if the y values are NOT infinite and NOT not a number
				if(!min_y.get(i).isInfinite() && !min_y.get(i).isNaN()) {
					//format the x & y values held within the two x & y arraylists (found from checkExtrema function)
					BigDecimal x_val = new BigDecimal(min_x.get(i));
					x_val = x_val.setScale(2, RoundingMode.HALF_UP);
					
					BigDecimal y_val = new BigDecimal(min_y.get(i));
					y_val = y_val.setScale(2, RoundingMode.HALF_UP);
					
					//add the newly formatted minimum values in ( x, y ) form to the minimum panel
					min.add(new JLabel("( " + x_val + ", " + y_val + " )"));
				}
			}
			
			//same with minimum panel except maximum
			max.add(new JLabel("<html><font color= 'orange'>Maximum values:</font><br/></html>"));
			for(int i = 0; i < max_x.size(); i++) {
				if(!max_y.get(i).isInfinite() && !max_y.get(i).isNaN()) {
					
					BigDecimal x_val = new BigDecimal(max_x.get(i));
					x_val = x_val.setScale(2, RoundingMode.HALF_UP);
					
					BigDecimal y_val = new BigDecimal(max_y.get(i));
					y_val = y_val.setScale(2, RoundingMode.HALF_UP);
					
					max.add(new JLabel("( " + x_val + ", " + y_val + " )"));
				}
			}
			
			//define inflection x & y arraylists
			inf_x = new ArrayList<Double>();
			inf_y = new ArrayList<Double>();
			
			//find inflection points using function defined below
			findInflections(inf_x, inf_y, derivative_2);
			
			//add titles of both inflection panels
			inflection.add(new JLabel("<html><font color= 'orange'>Inflection Points:</font></html>"));
			inflection2.add(new JLabel("<html><font color= 'orange'>****************</font></html>"));
			//for the length of the x arraylist
			for(int i = 0; i < inf_x.size(); i++) {
				
				//format x & y values
				BigDecimal x_val = new BigDecimal(inf_x.get(i));
				x_val = x_val.setScale(2, RoundingMode.HALF_UP);
				
				BigDecimal y_val = new BigDecimal(inf_y.get(i));
				y_val = y_val.setScale(2, RoundingMode.HALF_UP);
				
				//if there are less than 18 inflections points, add them to first inflection panel
				if(i < 18) {
					inflection.add(new JLabel("( " + x_val + ", " + y_val + " )"));	
				}
				//otherwise add them to the second inflection panel
				else {
					inflection2.add(new JLabel("( " + x_val + ", " + y_val + " )"));
				}
			}
			
			//set the "See Original Function" checkbox to checked
			original.setSelected(true);
			
			//creates a GraphPanel object that holds the paintComponent that will be used to draw the graph
			GraphPanel graph = new GraphPanel();
			
			//add it to THIS object and put it west of all other objects
			add(graph, BorderLayout.WEST);
			
			
			//everytime the button is clicked, if layout changes, repaint 
			revalidate();
			repaint();
		}
	}
	
	//unnecessary getEquation function that returns the input textfield's text content
	public static String getEquation() {
		return input.getText();
	}
	
	public static Integer getScale() {
		try {
			return Integer.parseInt(scale.getText());
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
	
	//function to find the y values of the function inputed
	public static void findPoints() {
		//defines how the graph is scaled
		
		//distance between x ticks
		xScale = 700/(getScale()*2);
		//distance between y ticks
		yScale = 800/((getScale() + 3)*2);
		//multiply distance by how many ticks to get x origin
		xOrigin = getScale() * xScale;
		//same as x except  you need to add 100 because graph starts at 100
		yOrigin = 100+(getScale() + 3) * yScale;
		//maximum x pixel is 700 - leftover pixels after dividing by ticks
		xMax = 700-(700%(getScale()*2));
		//same with y except graph ends at 900
		yMax = 900-(800%((getScale() + 3)*2));
		
		
		//sets array length to xOrigin * 2 because that's how long the graph is
		x = new double[xOrigin * 2];
		y = new Double[x.length];
		
		//will carry information about discontinuities
		discontinuities = new String[x.length];
		
		//uses the java parser provided by mr duran to evaluate values for equation getEquation() 
		MathEvaluator m = new MathEvaluator(getEquation());
		
		//at every value of .1 between 0 and xMax, find 
		for(int i = 0; i < x.length; i++) {
			
			//checks for y values to the THOUSANDTHS place
			double[] xPoint = new double[(int)((1.0/xScale)*1000+1)];
			Double[] yPoint = new Double[xPoint.length];
			
			//sets value to -bound + i/xScale
			x[i] = -xOrigin/(double)xScale + i/(double)xScale;
			
			//for 0 to value between each pixel
			for(int j = 0; j < (1.0/xScale)*1000; j++) {
				
				//xPoint = -boundary + i/xScale (adds 1/xScale every time) - remainder when dividing this sum by 0.005 for solid multiple of 5 + 1/1000 every time
				xPoint[j] = -xOrigin/(double)xScale +  i/(double)xScale - (-xOrigin/(double)xScale +  i/(double)xScale)%0.005 + j/1000.0;
	
				//add variable function of the mathevaluator at point xPoint[i] just found
				m.addVariable("x", xPoint[j]);
			
				//get the value of the function
				yPoint[j] = m.getValue();
				//if y value is not infinity, null, or NaN
				if( yPoint[j] != null&& !Double.isInfinite(yPoint[j]) && !Double.isNaN(yPoint[j])) {
					discontinuities[i] = "none";
				}
				//otherwise there is a discontinuity
				else{
					//create double variables numerator & denominator for fractions
					double numerator;
					double denominator;
					
					//create regex pattern for separating fractions into numerator and denominator (REQUIRES PARENTHESES AROUND ENTIRE FRACTION)
					Pattern checkRegex = Pattern.compile("\\(([()0-9x\\s+\\-*\\^\\.]+)/([()0-9x\\s+\\-*\\^\\.]+)\\)");
					
					//create matcher that checks whether the equation matches the regex pattern
					Matcher regexMatcher = checkRegex.matcher(getEquation());
					
					//assume that the discontinuity is simply that x is out of the domain until proven otherwise
					outOfDomain = true;
				
					//while there is a match 
					while(regexMatcher.find()) {
						
						//if the first two groups found in the matcher are not null (groups defined in the regex pattern)
						if(regexMatcher.group(1) != null && regexMatcher.group(2) != null) {
							
							//create new parser for numerator expression & find value
							MathEvaluator num = new MathEvaluator(regexMatcher.group(1));
							num.addVariable("x", xPoint[j]);
							numerator = num.getValue();
							
							//create new parse for denominator expression & find value
							MathEvaluator den = new MathEvaluator(regexMatcher.group(2));
							den.addVariable("x", xPoint[j]);
							denominator = den.getValue();
						
							//if the numerator and denominator are both equal to 0, discontinuity = hole
							if(numerator == denominator && numerator == 0) {
								discontinuities[i] = "hole";
								//for later when we need to position hole
								y[i] = (double)Math.round(y[i-1]);
								System.out.println("Hole: " + x[i] + ", " + y[i]);
								//makes sure discontinuities count when out of domain as well
								outOfDomain = false;
								
								//break out of while loop
								break;
							}
						
							//else if only the denominator is 0, the discontinuity is a v. asymptote
							else if(denominator == 0) {
								discontinuities[i] = "vertical asymptote";
								//makes sure discontinuities count when out of domain as well
								outOfDomain = false;
								break;
							}
						}
					}
					//out of domain
					if(outOfDomain) {
						discontinuities[i] = "out of domain";
					}
					//if none of the values to the thousandths place between this x[i] value and the next are discontinuities, break out of 1000 for loop
					if(!discontinuities[i].equals("none")) {
						break;
					}
				}
			}
			//if no discontinuity, set y[i] = the value at x[i]
			if(discontinuities[i].equals("none")) {
				m.addVariable("x", x[i]);
				y[i] = m.getValue();

			}
		}
		
		//define derivative arrays
		derivative_1 = new Double[x.length];
		derivative_2 = new Double[x.length];
		der_1_dc = new String[x.length];
		der_2_dc = new String[x.length];
	}
	
	//derive using original y-value function, to be derived y-values, and a discontinuity array that will be used when we graph
		public static void derive(Double []original, Double[] toBeDerived, String[] discontinuity) {
			//for the length of 1 max
			for(int i = 1; i < original.length-2; i++) {
					//if the original function is NOT null, infinity, or not a number
					if(original[i-1] != null && original[i+1]!= null && original[i] != null && !Double.isInfinite(original[i]) && !Double.isNaN(original[i])) {
						//derivative = (y2-y1)/(x2-x1) by limit definition 
						
						toBeDerived[i] = (original[i+1]-original[i-1]) / (x[i+1]-x[i-1]);
						//no discontinuity at this point
						discontinuity[i] = "none";
					}
					//otherwise
					else {
						//there is a discontinuity
						discontinuity[i] = "null";
					}
			}
			
			//just set the ending values to the values before it since we can't really calculate slope when they don't have slope
			discontinuity[0] = "none";
			toBeDerived[0] = toBeDerived[1];
			discontinuity[original.length-1] = "none";
			toBeDerived[original.length-1] = toBeDerived[original.length-2];
			
		}
	
	//function to calculate the area of the trapezoid between integer a and b
	public static double trapArea(int a, int b) {
		return 0.5 * (derivative_1[a] + derivative_1[b]);
	}
	
	//trap function to calculate TOTAL value of definite integral approximation
	public static void trap(double a, double b) {
		
		//set defIntegral back to 0.0
		defIntegral = 0.0;
		
		//multiply the value by xScale defined later + xOrigin (the center of the graph)
		a_val = (int)Math.round(a*xScale) + xOrigin;
		b_val = (int)Math.round(b*xScale) + xOrigin;
		
		//if b > a
		if(b_val > a_val) {
			
			//for the pixels between a and b in increments of xScale
			for(int i = a_val; i < b_val; i+=xScale) {
				
				//if the max value - the current i value > xScale (meaning that you can increment by 1)
				if(b_val - i >= xScale) {
					
					//defIntegral = defIntegral + calculate trapezoidArea between i & i +xScale
					defIntegral += trapArea(i, i+xScale);
				
				//if max value - current i value < xScale, defIntegral = defIntegral + trapArea(current value to maximum value)
				}else {
					defIntegral += trapArea(i, b_val);
				}
			}
		}
		
		//if a > b do the same except start from the b value instead
		if(a_val > b_val) {
			for(int i = b_val; i < a_val; i+=xScale) {
				if(a_val - i > xScale) {
					defIntegral += trapArea(i, i+xScale);
				}else {
					defIntegral += trapArea(i, a_val);
				}
			}
			
			//multiply by -1 to signify the backwards evaluation of integral
			defIntegral *= -1;
		}
	}
	
	//fun(damental) Theory (of) Calc(ulus) takes in upper and lower bounds
	public static void funTheoryCalc(double a, double b) {
		
		//scale the a & b values to fit the graph
		a_val = (int)Math.round(a*xScale) + xOrigin;
		b_val = (int)Math.round(b*xScale) + xOrigin;
		
		//defIntegral = max - min;
		MathEvaluator m = new MathEvaluator(getEquation());
		m.addVariable("x", a);
		double A = m.getValue();
		m.addVariable("x", b);
		double B = m.getValue();
		if(!Double.isInfinite(A) && !Double.isInfinite(B) && !Double.isNaN(A) && !Double.isNaN(B)) {
			defIntegral = B - A;
		}else {
			defIntegral = null;
		}
	}
	
	//find inflection points using variables sent in but it could actually just directly access the variables because they are global
	public static void findInflections( ArrayList<Double> infx, ArrayList<Double> infy, Double[] scnd_der) {
		
		//for the length of 1 to graph max - 1
		for(int i = 1; i < scnd_der.length-1; i++) {
			//if second derivative at pixel i is NOT null, infinity, not a number && absVal(2nd_der@i) < 0.4 for rounding error && the signs of the first derivative before and after i are not the same
			if(scnd_der[i-1] != null && scnd_der[i+1] != null && scnd_der[i] != null && !Double.isInfinite(scnd_der[i]) && !Double.isNaN(scnd_der[i]) && (Math.abs(scnd_der[i]) < 0.08 && ((scnd_der[i-1] < 0 && scnd_der[i+1] > 0) || (scnd_der[i-1] > 0 && scnd_der[i+1] < 0)))){
				//add x & y values to inflection arraylists
				infx.add(x[i]);
				infy.add(y[i]);
			}
		}
	}
	//checkExtrema with variables sent in that again don't need to be sent in
	public static void checkExtrema(ArrayList<Double> minX,ArrayList<Double> minY, ArrayList<Double> maxX,ArrayList<Double> maxY, Double[] values) {
		//for the pixels between 1 and xMax
		for(int i = 1; i < values.length-1;i++) {
			
			//if the derivative of original function is NOT null, infinity, or not a number && the absVal(derivative @ i) < 0.108 to account for rounding error/subtraction estimate
			if(values[i-1]!=null && values[i+1] != null && values[i] != null && !Double.isInfinite(values[i]) && !Double.isNaN(values[i]) && ( Math.abs(values[i]) < 0.15)) {
				//if derivative before i < 0 && derivative after i > 0
				if(values[i-1] < 0 && values[i+1] > 0) {
					//add minimum
					minX.add(x[i]);
					minY.add(y[i]);
				}
				//if derivative before i > 0 && derivative after i < 0
				if(values[i-1] > 0 && values[i+1] < 0) {
					//add maximum
					maxX.add(x[i]);
					maxY.add(y[i]);
				}
				
			}
		}
	}
	
	//graphpanel class that extends jpanel and contains paintcomponent
	public class GraphPanel extends JPanel{
		
		//classwide integer output (what the y pixel will be on the graph)
		private int output;
		
		//boolean outOfRange to check whether the y value is out of the range of the graph (default false)
		private boolean outOfRange = false;
		
		//constructor to set the size of the graph panel
		public GraphPanel() {
			this.setPreferredSize(new Dimension(701,801));
		}
		
		//override the paintcomponent method to do what we want
		@Override
		protected void paintComponent(Graphics g) {
			
			//convert graphics to graphics2D (for line drawing)
			Graphics2D g2 = (Graphics2D) g;
			
			//set color red
			g2.setColor(Color.RED);
			
			//if FTC checkbox is selected
			if(ftc.isSelected()) {
				
				//check derivative 1 (bc we use derivative 1 to show FTC) & deselect integrateByTrapezoid box
				der_1.setSelected(true);
				integrate.setSelected(false);
				
				//if a value < b value
				if(a_val < b_val) {
					
					//draw a line from x-axis to y-value as long as the value is not out of range
					for(int i = a_val; i <= b_val; i++) {
						scaleY(derivative_1[i]);
						
						if(!outOfRange) {
							g2.drawLine(i, scaleY(derivative_1[i]), i, yOrigin);
						}
					}
				}
				
				//if b < a, do the same except start from b
				if(b_val < a_val) {
					for(int i = b_val; i <= a_val; i++) {
						scaleY(derivative_1[i]);
						
						if(!outOfRange) {
							g2.drawLine(i, scaleY(derivative_1[i]), i, yOrigin);
						}
					}
				}
			}
			
			//if integrateByTrapezoid is checked
			if(integrate.isSelected()) {
				
				//check derivative 1 & deselect FTC
				der_1.setSelected(true);
				ftc.setSelected(false);
				
				//if a < b
				if(a_val < b_val) {
					//for the pixels in between, increment by 10
					for(int i = a_val; i <= b_val; i+=xScale) {
						//check whether derivative at i is out of range
						scaleY(derivative_1[i]);
						if(!outOfRange) {
							
							//if distance between max an i is greater than 10
							if(b_val - i > xScale) {
								//check if value at i+10 is outofrange (defined below)
								scaleY(derivative_1[i+xScale]);
								//if not outOfRange
								if(!outOfRange) {
									//new array for trapezoid points
									int[] trapX = {i,i, i+xScale,i+xScale,i};
									int[] trapY = {yOrigin,scaleY(derivative_1[i]),scaleY(derivative_1[i+10]),yOrigin,yOrigin};
									
									//create polygon using these arrays & specify number of points
									Polygon trapezoid = new Polygon(trapX, trapY, 5);
									
									//fill trapezoid
									g2.fillPolygon(trapezoid);
								}
							}
							//otherwise b-i < xScale
							else{
								//scaleY contains a check out of range function
								scaleY(derivative_1[b_val]);
								
								//if not out of range
								if(!outOfRange) {
									//draw trapezoid
									int[] trapX = {i,i, b_val,b_val,i};
									int[] trapY = {yOrigin,scaleY(derivative_1[i]),scaleY(derivative_1[b_val]),yOrigin,yOrigin};
									Polygon trapezoid = new Polygon(trapX, trapY, 5);
									g2.fillPolygon(trapezoid);
								}
							}
						}
						}
					}
				}
				//for when b < a and does the same thing as a > b (i could create a function for this but i'm too lazy
				if(b_val < a_val) {
					for(int i = b_val; i <= a_val; i+=xScale) {
						//check whether derivative at i is out of range
						scaleY(derivative_1[i]);
						if(!outOfRange) {
							
							//if distance between max and i is greater than xScale
							if(a_val - i > xScale) {
								//check if value at i+xScale is outOfRange (defined below)
								scaleY(derivative_1[i+xScale]);
								//if not outOfRange
								if(!outOfRange) {
									//new array for trapezoid points
									int[] trapX = {i,i, i+xScale,i+xScale,i};
									int[] trapY = {yOrigin,scaleY(derivative_1[i]),scaleY(derivative_1[i+xScale]),yOrigin,yOrigin};
									
									//create polygon using these arrays & specify number of points
									Polygon trapezoid = new Polygon(trapX, trapY, 5);
									
									//fill trapezoid
									g2.fillPolygon(trapezoid);
								}
							}
							//otherwise b-i < xScale
							else{
								//scaleY contains a check out of range function
								scaleY(derivative_1[a_val]);
								
								//if not out of range
								if(!outOfRange) {
									//draw trapezoid
									int[] trapX = {i,i, a_val,a_val,i};
									int[] trapY = {yOrigin,scaleY(derivative_1[i]),scaleY(derivative_1[a_val]),yOrigin,yOrigin};
									Polygon trapezoid = new Polygon(trapX, trapY, 5);
									g2.fillPolygon(trapezoid);
								}
							}
							
						}
				}
			}
			//set color of paint to black
			g2.setColor(Color.BLACK);
			
			//this block draws the constant x and y axis of the graph (every tick is 1)
			if(getScale() != null) {
				g2.drawLine(0, yOrigin, xMax, yOrigin);
				for(int i = 0; i <= xMax; i+=xScale) {
					g2.drawLine(i, yOrigin - 10, i, yOrigin + 10);
				}
				g2.drawLine(xOrigin, 100, xOrigin, yMax);
				for(int i = 100; i<=yMax; i+=(yScale)) {
					g2.drawLine(xOrigin - 10, i, xOrigin + 10, i);
				}
			}
			
			//if "See original function" is checked
			if(original.isSelected()) {
				
				//set color to blue for graph drawing
				g2.setColor(marineBlue);
			
				//for every pixel in the width of the graph up to xMax-1 for outOfBound errors :(
				for(int i = 0; i < xMax-1; i++) {
				
					//if y value is not null, Infinite, or Not a Number
					if(y[i]!=null && !Double.isInfinite(y[i]) && !Double.isNaN(y[i])) {
						
						//method that checks whether the yvalue at the current x pixel is outOfRange
						scaleY(y[i]);
					}
					
					//if less than xMax-2 bc outOfBound errors && a hole is coming up
					if(i < xMax-2 && discontinuities[i+1].equals("hole")) {
					
						//check whether the y value at i+1 is outOfRange
						scaleY(y[i+1]);
					
						//if NOT outOfRange
						if(!outOfRange) {
						
							//set color to red
							g2.setColor(Color.RED);
							
							//draw an open circle and skip a few x pixels to make the graph look nice
							g2.drawOval(i-2, scaleY(y[i+1])-15, 15, 15);
			
						}
					
						//set color back to blue
						g2.setColor(marineBlue);
						
						//increment by value based on how large scale is
						i+=xScale/10;
						
						//continue for loop
						continue;
					
					}
				
					//if discontinuity at i is v. asymptote
					else if(discontinuities[i].equals("vertical asymptote")) {
					
						//set color to red
						g2.setColor(Color.RED);
					
						//dotted line across the x pixel value
						for(int j = 100; j< yMax; j+= yScale) {
							g2.drawLine(i, j, i, j+yScale-yScale/5);
						}
					
						//set color to blue
						g2.setColor(marineBlue);
					}
				
					//if discontinuity is out of domain, don't draw anything & skip to next pixel
					else if(discontinuities[i].equals("out of domain")) {
						continue;
					}
					
					//if the minimum or maximum x arraylist contains x[i] 
					else if(min_x.contains(x[i]) || max_x.contains(x[i])) {
						
						//out of range?
						if(!outOfRange) {
							
							//set color to lightpink
							g2.setColor(lightPink);
							
							//create circle
							Ellipse2D.Double circle = new Ellipse2D.Double(i-3,scaleY(y[i])-4,8,8);
							
							//fill circle
							g2.fill(circle);
							
							//reset color to blue
							g2.setColor(marineBlue);
						}
					}
					
					//if inflection x array contains x[i]
					else if(inf_x.contains(x[i])) {
						
						//out of range?
						if(!outOfRange) {
							
							//color to brown
							g2.setColor(brown);
							
							//create circle
							Ellipse2D.Double circle = new Ellipse2D.Double(i-3,scaleY(y[i])-4,8,8);
							
							//fill circle
							g2.fill(circle);
							
							//reset color
							g2.setColor(marineBlue);
						}
					}
				
					//else if there is no discontinuity
					else if(discontinuities[i].equals("none")){
					
						//if y value is not out of range
						if(!outOfRange) {
							
							//if there is a big gap (like in tan(x)), between pixel values, draw an asymptote
							if(y[i]!=null&&y[i+1]!=null && Math.abs(y[i]-y[i+1]) > 35) {
								g2.setColor(Color.RED);
								
								//dotted line across the x pixel value
								for(int j = 100; j< yMax; j+= yScale) {
									g2.drawLine(i, j, i, j+yScale - yScale/5);
								}
							
								//set color to blue
								g2.setColor(marineBlue);
							}
							//otherwise
							else {
								if(y[i]!=null && y[i+1]!=null)
								//draw line from x value i to i+1
								g2.drawLine(i, scaleY(y[i]), i+1, scaleY(y[i+1]));
							}
						}
					}
				}
			}
			
			//if der_1 checkbox selected
			if(der_1.isSelected()) {
				
				//color = green
				g2.setColor(grassGreen);
				
				//for graph width
				for(int i = 0; i<xMax-2;i++) {
					
					//as long as derivative is a value within range
					if(derivative_1[i] != null && derivative_1[i+1] != null) {
						
						//out of range?
						scaleY(derivative_1[i]);
						if(!outOfRange) {
							
							//draw line
							g2.drawLine(i, scaleY(derivative_1[i]), i+1, scaleY(derivative_1[i+1]));
						}
					}
				}
			}
			
			//same as derivative 1 but for 2
			if(der_2.isSelected()) {
				g2.setColor(darkPurple);
				for(int i = 0; i<xMax-2;i++) {
					if(der_2_dc[i].equals("none") && derivative_2[i] != null && derivative_2[i+1] != null) {
						scaleY(derivative_2[i]);
						if(!outOfRange) {
							g2.drawLine(i, scaleY(derivative_2[i]), i+1, scaleY(derivative_2[i+1]));
						}
					}
				}
			}
		}
		//checks whether y value is out of range and converts double value to
		//closest integer equivalent scaled to the graph
		//double input should be the double y value in the last part of paintComponent method
		private int scaleY(double input) {
			
			//multiply double value by 10 to get a scaled by 10 value of the double
			double doubleOutput = input*yScale;
			//if the doubleOutput is greater than -410 or less than 410
			if(doubleOutput <= yOrigin +10 && doubleOutput >= -yOrigin - 10) {
				
				//y value is not outOfRange
				outOfRange = false;
				
				
				output = yOrigin - (int) Math.round(doubleOutput);
			
			//else out of range
			}else{
				outOfRange = true;
			}
			
			//return integer output
			return output;
		}
		
	}
}