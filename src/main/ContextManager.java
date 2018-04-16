package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.zeroc.Ice.Current;
import com.zeroc.IceStorm.BadQoS;
import com.zeroc.IceStorm.InvalidSubscriber;

import UVApp.ContextManagerIce;
import UVApp.LocationSensor;
import UVApp.LocationServerIcePrx;
import UVApp.PreferenceRepositoryIcePrx;
import UVApp.TemperatureSensor;
import UVApp.UIIcePrx;
import UVApp.UserDetails;

public class ContextManager extends com.zeroc.Ice.Application {
	
	private static Map<String, UserInfo> users = new HashMap<>();
	private static PreferenceRepositoryIcePrx prefProxy;
	private static Map<String, CityInfo> cities = new HashMap<>();
	private static String currentUser = "";
	
	public ContextManager(String filename) {
		users = new HashMap<>();
		readFile(filename);
	}
	
	private void readFile(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line = "";
			CityInfo city = new CityInfo();
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) {
					cities.put(city.getName(), city);
					city = new CityInfo();
					continue;
				}
				Scanner scanner = new Scanner(line);
				while (scanner.hasNext()) {
					String token = scanner.next();
					switch (token) {
					case "name:":
						StringBuilder name = new StringBuilder();
						while (scanner.hasNext()) {
							name.append(scanner.next() + " ");
						}
						city.setName(name.toString());
						break;
					case "location:":
						city.setLocation(scanner.next());
						break;
					case "information:":
						StringBuilder info = new StringBuilder();
						while (scanner.hasNext()) {
							info.append(scanner.next() + " ");
						}
						city.setInfo(info.toString());
						break;
					case "services:":
						scanner.useDelimiter(", ");
						while(scanner.hasNext()) {
							city.addService(scanner.next());
						}
						scanner.useDelimiter(" ");
						break;
					}
				}
				scanner.close();
			}
			if (!city.getName().isEmpty()) { cities.put(city.getName(), city); }
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int run(String[] arg0) {
		String id = null;
		String tempTopicName = "temperature";
		//String locTopicName = "location";
		//String warningTopicName = "tempWarning";
		com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(
				communicator().propertyToProxy("TopicManager.Proxy"));
		if (topicManager == null) {
			System.err.println("Invalid proxy");
			return 1;
		}
		
		com.zeroc.IceStorm.TopicPrx tempTopic;
		//com.zeroc.IceStorm.TopicPrx locTopic;
		try {
			tempTopic = topicManager.retrieve(tempTopicName);
			//locTopic = topicManager.retrieve(locTopicName);
		} catch (com.zeroc.IceStorm.NoSuchTopic e) {
			try {
				tempTopic = topicManager.create(tempTopicName);
				//locTopic = topicManager.create(locTopicName);
			} catch (com.zeroc.IceStorm.TopicExists ex) {
				System.err.println(appName() + ": temporary failure");
				return 1;
			}
		}
		
		com.zeroc.Ice.ObjectAdapter tempAdapter = communicator()
				.createObjectAdapterWithEndpoints("TemperatureSensor.Subscriber", "tcp");
		
		com.zeroc.Ice.Identity tempSubId = new com.zeroc.Ice.Identity(id, "");
		if (tempSubId.name == null) {
			tempSubId.name = java.util.UUID.randomUUID().toString();
		}
		
		com.zeroc.Ice.ObjectPrx tempSubscriber = tempAdapter.add(new TemperatureSensorI(), tempSubId);
		Map<String, String> qos = new HashMap<>();
		tempSubscriber = tempSubscriber.ice_oneway();
		
		tempAdapter.activate();
		
//		com.zeroc.Ice.Identity locSubId = new com.zeroc.Ice.Identity(id, "");
//		if (locSubId.name == null) {
//			locSubId.name = java.util.UUID.randomUUID().toString();
//		}
		
		//locAdapter.activate();
		
		//com.zeroc.Ice.ObjectPrx locSubscriber = locAdapter.add(new LocationSensorI(), locSubId);
		//locSubscriber = locSubscriber.ice_oneway();
		
		try {
			tempTopic.subscribeAndGetPublisher(qos, tempSubscriber);
		} catch (com.zeroc.IceStorm.AlreadySubscribed e) {
			e.printStackTrace();
			return 1;
		} catch (BadQoS e) {
			e.printStackTrace();
			return 1;
		} catch (InvalidSubscriber e) {
			e.printStackTrace();
			return 1;
		}
		
//		try {
//			// client
//			com.zeroc.Ice.ObjectPrx base = communicator().stringToProxy("PreferenceRepositoryIce:default -p 20100");
//			prefProxy = PreferenceRepositoryIcePrx.checkedCast(base);
//			
//			// server
//			com.zeroc.Ice.ObjectAdapter adapter = communicator().
//					createObjectAdapterWithEndpoints("ContextManagerIce", "default -p 20200");
//			adapter.add(new ContextManager.ContextManagerIceI(), 
//					com.zeroc.Ice.Util.stringToIdentity("ContextManagerIce"));
//			adapter.activate();
//		} catch (Exception e) {}
		
		communicator().waitForShutdown();
		tempTopic.unsubscribe(tempSubscriber);
		return 0;
	}
	
	public static void main(String[] args) {
		ContextManager cm = new ContextManager(args[0]);
		
		try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
			// client
			com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("PreferenceRepositoryIce:default -p 20100");
			prefProxy = PreferenceRepositoryIcePrx.checkedCast(base);
			
			//com.zeroc.Ice.ObjectPrx base2 = communicator.stringToProxy("LocationServerIce:default -p 20500");
			//locationServerProxy = LocationServerIcePrx.checkedCast(base2);
			
			//com.zeroc.Ice.ObjectPrx base3 = communicator.stringToProxy("UIIce:default -p 20300");
			//uiProxy = UIIcePrx.checkedCast(base3);
			
			// server
			com.zeroc.Ice.ObjectAdapter adapter = communicator.
					createObjectAdapterWithEndpoints("ContextManagerIce", "default -p 20200");
			adapter.add(new ContextManager.ContextManagerIceI(), 
					com.zeroc.Ice.Util.stringToIdentity("ContextManagerIce"));
			adapter.activate();
			communicator.waitForShutdown();
			
			int status = cm.main("ContextManager", args, "config.sub");
		}
	}

	public class TemperatureSensorI implements TemperatureSensor {
		
		@Override
		public void getTemperature(String userName, String type, int temperature, Current current) {
			System.out.println("temperature: " + temperature);
			if (users.get(userName) != null) {
				if (users.get(userName).currentTemp != temperature) {
					users.get(userName).currentTemp = temperature;
					if (temperature >= users.get(userName).tempThreshold) {
						System.out.println("Warning, Temperature is now " + temperature + 
								"\nSuggestion - please go to " + users.get(userName).tempPref);
						//uiProxy.printWarning(temperature, users.get(userName).tempPref, true);
					}
				}
			}
		}
	}
	
	public class LocationSensorI implements LocationSensor {

		@Override
		public void getLocation(String userName, String sensorType, String value, Current current) {
			if (users.get(userName) != null) {
				users.get(userName).currentLoc = value;
			}
			
		}
		
	}
	
	static class UserInfo {
		private int tempThreshold;
		private int skinType;
		private int currentTemp;
		private String currentLoc = "";
		private String tempPref = "";
	}
	
	public static class ContextManagerIceI implements ContextManagerIce {

		@Override
		public void loginUser(String userName, Current current) {
			if (prefProxy == null) {
				String args[] = new String[30];
				try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
					com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("PreferenceRepositoryIce:default -p 20100");
					prefProxy = PreferenceRepositoryIcePrx.checkedCast(base);
				}
			}
			UserDetails u = prefProxy.getUserDetails(userName);
			UserInfo user = new UserInfo();
			user.tempThreshold = u.tempThreshold;
			user.skinType = u.skinType;
			user.currentTemp = u.currentTemp;
			user.tempPref = u.tempPref;
			users.put(userName, user);
			currentUser = userName;
			System.out.println("logged in");
		}

		@Override
		public String getInterest(String item, Current current) {
			for (String c : cities.keySet()) {
				if (c.trim().equals(item)) {
					return "Information about " + item + ":\n" + cities.get(c).getInfo();
				}
			}
			return "No match found for item of interest";
		}

		@Override
		public String getInterestInLoc(Current current) {
			if (users.get(currentUser) == null) {
				return "Something went wrong..Please try again\n";
			}
			String loc = users.get(currentUser).currentLoc;
			for (String c : cities.keySet()) {
				if (cities.get(c).getLocation().equals(loc)) {
					StringBuilder output = new StringBuilder();
					output.append("The following items of interest are in your location:\n");
					output.append(c);
				}
			}
			return "There are no items of interest in your current location";
		}
		
	}
}
