package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import UVApp.TemperatureSensorPrx;

public class AllSensors extends com.zeroc.Ice.Application {

	private String username;
	//Map<Integer, Integer> tempReadings;
	List<Integer> tempReadings;
	List<Integer> tempTimes;
	
	public AllSensors(String username) {
		this.username = username;
		tempReadings = new ArrayList<>();
		tempTimes = new ArrayList<>();
		readTempFile();
	}
	
	private void readTempFile() {
		//Map<Integer, Integer> tempReadings = new LinkedHashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("..\\Resources\\" + username + "Temperature.txt"));
			String line = "";
			boolean isFirst = true;
			while ((line = br.readLine()) != null) {
				Scanner scanner = new Scanner(line);
				scanner.useDelimiter(",");
				tempReadings.add(scanner.nextInt());
				int tempTime = scanner.nextInt();
				if (isFirst) {
					tempTimes.add(tempTime);
					isFirst = false;
				} else {
					tempTimes.add(tempTime + tempTimes.get(tempTimes.size() - 1));
				}
				scanner.close();
			}
			br.close();
			System.out.println("size: " + tempReadings.size());
			for (int i = 0; i < tempTimes.size(); i++) {
				System.out.println(tempReadings.get(i) + " -> " + tempTimes.get(i));
			}
		} catch (IOException e) {
			System.err.println("Error in reading temperature file. Exiting...");
			return;
		}
	}
	
	@Override
	public int run(String[] arg0) {
		boolean isShutdown = false;
		String tempTopicName = "temperature";
		com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(
				communicator().propertyToProxy("TopicManager.Proxy"));
		if (topicManager == null) {
			System.err.println("Invalid proxy");
			return 1;
		}
		// retrieve topic
		com.zeroc.IceStorm.TopicPrx tempTopic;
		try {
			tempTopic = topicManager.retrieve(tempTopicName);
		} catch (com.zeroc.IceStorm.NoSuchTopic e) {
			try {
				tempTopic = topicManager.create(tempTopicName);
			} catch (com.zeroc.IceStorm.TopicExists ex) {
				System.err.println(appName() + ": temporary failure");
				return 1;
			}
		}
		// temp publisher
		com.zeroc.Ice.ObjectPrx tempPublisher = tempTopic.getPublisher();
		tempPublisher = tempPublisher.ice_oneway();
		TemperatureSensorPrx tempPrx = TemperatureSensorPrx.uncheckedCast(tempPublisher);
		int counter = 0;
		while (!isShutdown) {
			sendTempReadings(tempPrx, counter);
			counter++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	private void sendTempReadings(TemperatureSensorPrx tempPrx, int counter) {
		int currentIndex = 0;
		if (counter > tempTimes.get(tempTimes.size() - 1)) {
			while (counter >= tempTimes.get(tempTimes.size() - 1)) {
				counter -= tempTimes.get(tempTimes.size() - 1);
			}
		}
		while (counter > tempTimes.get(currentIndex)) {
			currentIndex++;
		}
		try {
			tempPrx.getTemperature(username, tempReadings.get(currentIndex));
		} catch (com.zeroc.Ice.CommunicatorDestroyedException e) { }
	}
	
	public static void main(String[] args) {
		AllSensors app = new AllSensors(args[0]);
		int status = app.main("AllSensors", args, "config.pub");
		System.exit(status);
	}
}
