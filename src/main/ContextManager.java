package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zeroc.Ice.Current;
import com.zeroc.IceStorm.BadQoS;
import com.zeroc.IceStorm.InvalidSubscriber;

import UVApp.TemperatureSensor;

public class ContextManager extends com.zeroc.Ice.Application {

	public ContextManager(String filename) {
		
	}
	
	@Override
	public int run(String[] arg0) {
		String id = null;
		String tempTopicName = "temperature";
		com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(
				communicator().propertyToProxy("TopicManager.Proxy"));
		if (topicManager == null) {
			System.err.println("Invalid proxy");
			return 1;
		}
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
		com.zeroc.Ice.ObjectAdapter tempAdapter = communicator()
				.createObjectAdapterWithEndpoints("TemperatureSensor.Subscriber", "tcp");
		com.zeroc.Ice.Identity tempSubId = new com.zeroc.Ice.Identity(id, "");
		if (tempSubId.name == null) {
			tempSubId.name = java.util.UUID.randomUUID().toString();
		}
		
		tempAdapter.activate();
		
		com.zeroc.Ice.ObjectPrx tempSubscriber = tempAdapter.add(new TemperatureSensorI(), tempSubId);
		Map<String, String> qos = new HashMap<>();
		tempSubscriber = tempSubscriber.ice_oneway();
		
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
		
		communicator().waitForShutdown();
		tempTopic.unsubscribe(tempSubscriber);
		return 0;
	}
	
	public static void main(String[] args) {
		ContextManager cm = new ContextManager("hello");
		ArrayList<String> extraArgs = new ArrayList<>();
		int status = cm.main("ContextManager", args, "config.sub");
	}

	public class TemperatureSensorI implements TemperatureSensor {
		
		@Override
		public void getTemperature(String userName, int temperature, Current current) {
			System.out.println("temperature: " + temperature);
		}
	}
}
