package module6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PFont;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	private String typedCityName = ""; 
	// The map
	private UnfoldingMap map;
	
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	private PFont bold = createFont("Arial Bold",13);
	private PFont regular = createFont("Arial",12);
	/*private int countNearByQuakes=0;
	private double avgMag=0.0;
	private double sumMag = 0.0;
	private int countRecentQuakes=0;
	*/
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		/*
				map1 = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			    map2 = new UnfoldingMap(this, 200, 50, 650, 600, new Microsoft.AerialProvider());
			    map3 = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleTerrainProvider());
			    MapUtils.createDefaultEventDispatcher(this, map1, map2, map3);
			    
			    map = map1;*/
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
		earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    //printQuakes();
	    
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	    //new implemented method for sorting magnitude of quakes
	    sortAndPrint(quakeMarkers.size());
	   
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		//addKey();
		//Added a parameter to addKey in order to draw the Typed City to the Key
		addKey(typedCityName);
		/*additionalKey();
		if( lastClicked != null && lastClicked instanceof CityMarker ) {
			popUpCityClicked(countNearByQuakes, avgMag, countRecentQuakes);
		}

		//Extension
		//To display lat and long at mouse pointer location
		Location location = map.getLocation(mouseX, mouseY);
	    fill(0);
	    if( map == map2 ) {
	    	fill(255);
	    }
	    //mouseX is the x-coord and mouseY is the y-coord where
	    //the lat and long will be displayed
	    text(Math.round(location.getLat()*100.0)/100.0 + ", " + Math.round(location.getLon()*100.0)/100.0, mouseX, mouseY);
		*/
	}
	
	
	
	// TODO: Add the method:
	//   private void sortAndPrint(int numToPrint)
	// and then call that method from setUp
	private void sortAndPrint( int numToPrint ) {
		EarthquakeMarker[] array = new EarthquakeMarker[ quakeMarkers.size() ];
		array = quakeMarkers.toArray( array );
		Arrays.sort(array);
		
		//to print all quake magnitudes
		for( int i=0; i<numToPrint; i++) {
			System.out.println("magnitude : " + array[i].getMagnitude());
		}
		
		//method to get greatest magnitude occuring three or more times.
		for( int i=0; i<numToPrint; i++) {
			
			if( array[i].getMagnitude() == array[i+1].getMagnitude()
					&& array[i+1].getMagnitude() == array[i+2].getMagnitude() ) {
				System.out.println("greatest magnitude occuring three or more times" + array[i].getMagnitude());
				break;
			}
		}

		}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
			checkEarthquakesForClick();
			if (lastClicked == null) {
				checkCitiesForClick();
			}
		}
	}
	
	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : quakeMarkers) {
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					if (quakeMarker.getDistanceTo(marker.getLocation()) 
							> quakeMarker.threatCircle()) {
						quakeMarker.setHidden(true);
					}
				}
				return;
			}
		}		
	}
	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}
	@Override
	public void keyReleased()
	{
		// if (key == CODED) {
		if (keyCode == BACKSPACE || keyCode == DELETE) {
			if (typedCityName.length() > 0) {
				typedCityName = typedCityName.substring(0, typedCityName.length()-1);
				compareCityName("");
			}

		} else if (keyCode == RETURN || keyCode == ENTER) {
			compareCityName(typedCityName);
		}
		
		else {
			typedCityName += key;
			typedCityName = typedCityName.replaceAll("[^a-zA-Z]", "");
		}
	}
	// and respond appropriately. Partial queries are welcome for strings greater than 3 on length.
		private void compareCityName(String typedCityName) {
			
			if (typedCityName == "" || typedCityName.isEmpty()) {  // If user press enter with an empty city string, reset citymarkers and show all of them
				System.out.println("Resetting City Markers");
				for (Marker mhide : cityMarkers) {
					mhide.setHidden(false);
				}
			} else if (typedCityName.length() > 3 ) { // Partial searchs are welcome if the string is greater than 3, as is less likely to have more than one match
				System.out.println("Searched City: " + typedCityName);
				for (Marker marker : cityMarkers) {
					
					
					//Use the lowest length of the string to avoid index exception when comparing
					int maxLenght = 0;
					if (typedCityName.length() >  marker.getStringProperty("name").length()) {
						 maxLenght = marker.getStringProperty("name").length();
					} else {
						 maxLenght = typedCityName.length();
					}
					
					//Compare the strings 		
					if (typedCityName.substring(0, maxLenght).equalsIgnoreCase( marker.getStringProperty("name").substring(0, maxLenght) )) { // If its a match!
						System.out.println("Your Searched city " + typedCityName + "matches the city " + marker.getStringProperty("name"));
						
						// I Will reuse the code from CheckCitiesForClick()
						lastClicked = (CommonMarker)marker;
						// Hide all the other earthquakes and hide
						for (Marker mhide : cityMarkers) {
							if (mhide != lastClicked) {
								mhide.setHidden(true);
							}
						}
					} else {
						System.out.println("Your City " + typedCityName + " doesnt match any of the cityquakes on the map");
					}
				} 
			} else { //Get console feedback that the query string needs to be longer
				System.out.println("Not enough chars to query");
			}
		}
		private void addKey(String typedCityName) {	
			// Remember you can use Processing's graphics methods here
			fill(255, 250, 240);
			
			int xbase = 25;
			int ybase = 50;
			
			rect(xbase, ybase, 150, 300);
			
			fill(0);
			textAlign(LEFT, CENTER);
			textSize(12);
			text("Earthquake Key", xbase+25, ybase+25);
			
			fill(150, 30, 30);
			int tri_xbase = xbase + 35;
			int tri_ybase = ybase + 50;
			triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
					tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
					tri_ybase+CityMarker.TRI_SIZE);

			fill(0, 0, 0);
			textAlign(LEFT, CENTER);
			text("City Marker", tri_xbase + 15, tri_ybase);
			
			text("Land Quake", xbase+50, ybase+70);
			text("Ocean Quake", xbase+50, ybase+90);
			text("Size ~ Magnitude", xbase+25, ybase+110);
			
			fill(255, 255, 255);
			ellipse(xbase+35, 
					ybase+70, 
					10, 
					10);
			rect(xbase+35-5, ybase+90-5, 10, 10);
			
			fill(color(255, 255, 0));
			ellipse(xbase+35, ybase+140, 12, 12);
			fill(color(0, 0, 255));
			ellipse(xbase+35, ybase+160, 12, 12);
			fill(color(255, 0, 0));
			ellipse(xbase+35, ybase+180, 12, 12);
			
			textAlign(LEFT, CENTER);
			fill(0, 0, 0);
			text("Shallow", xbase+50, ybase+140);
			text("Intermediate", xbase+50, ybase+160);
			text("Deep", xbase+50, ybase+180);

			text("Past hour", xbase+50, ybase+200);
			
			fill(255, 255, 255);
			int centerx = xbase+35;
			int centery = ybase+200;
			ellipse(centerx, centery, 12, 12);

			strokeWeight(2);
			line(centerx-8, centery-8, centerx+8, centery+8);
			line(centerx-8, centery+8, centerx+8, centery-8);
			
			//TODO For my extra functionality
			//DRAW the query of the city that the user is typing
			fill(0);
			text("Search City: ", xbase+40, ybase+250);
			
			//Get cursor inviting to write
			int startTime = millis();
			String CityName = typedCityName;
			if ((((startTime/60)/10)%2) == 1) {
				CityName = typedCityName += "|";
			} else {
				CityName = typedCityName;
			}
			
			//Print a white box for the query
			fill(255, 255, 255);
			rect(xbase+25, ybase+260, xbase+80, ybase-30);
			
			//Print the query string
			fill(0);
			text(CityName, xbase+40, ybase+268);
			
		}

}
