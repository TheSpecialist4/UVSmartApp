package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.zeroc.Ice.Current;

import UVApp.LocationServerIce;

public class LocationServer {
	
	private static Set<String> indoors;
	private static Set<String> outdoors;
	
	public LocationServer(String filename) {
		indoors = new HashSet<>();
		outdoors = new HashSet<>();
		readFile(filename);
		//printLocs();
	}
	
	public String getLocationInfo(String location) {
		if (indoors.contains(location)) {
			return "Indoor";
		} else if (outdoors.contains(location)) {
			return "Outdoor";
		} else {
			return "";
		}
	}
	
	public List<String> getIndoorLocations() {
		return new ArrayList<String>(indoors);
	}
	
	public List<String> getOutdoorLocations() {
		return new ArrayList<String>(outdoors);
	}
	
	private void readFile(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line = "";
			while ((line = br.readLine()) != null) {
				Scanner scanner = new Scanner(line);
				while (scanner.hasNext()) {
					String token = scanner.next();
					switch (token) {
					case "Indoor":
						scanner.next();
						addLocations(scanner.next(), true);
						break;
					case "Outdoor":
						scanner.next();
						addLocations(scanner.next(), false);
						break;
					default:
						scanner.next();
						break;
					}
				}
				scanner.close();
			}
			br.close();
		} catch (IOException e) {
			System.err.println("error in reading file '" + filename + "'."
					+ "Please make sure the full path is specified. Exiting...");
			return;
		}
	}
	
	private void addLocations(String token, boolean isIndoors) {
		Scanner scanner = new Scanner(token);
		scanner.useDelimiter(",");
		while (scanner.hasNext()) {
			if (isIndoors) {
				indoors.add(scanner.next());
			} else {
				outdoors.add(scanner.next());
			}
		}
		scanner.close();
	}
	
	private void printLocs() {
		System.out.print("Indoors: ");
		for (String l : indoors) {
			System.out.print(l + " ");
		}
		System.out.print("\nOutdoors: ");
		for (String l : outdoors) {
			System.out.print(l + " ");
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please enter the preference filename in Eclipse");
			return;
		}
		LocationServer ls = new LocationServer(args[0]);
		
		try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
			// server
			com.zeroc.Ice.ObjectAdapter adapter = communicator.
					createObjectAdapterWithEndpoints("LocationServerIce", "default -p 20500");
			adapter.add(new ContextManager.ContextManagerIceI(), 
					com.zeroc.Ice.Util.stringToIdentity("LocationServerIce"));
			adapter.activate();
			communicator.waitForShutdown();
		}
	}
	
	class LocationServerIceI implements LocationServerIce {

		@Override
		public boolean isLocationIndoor(String location, Current current) {
			return LocationServer.indoors.contains(location);
		}
		
	}
}
