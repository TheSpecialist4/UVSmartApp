package main;

import java.util.ArrayList;
import java.util.List;

public class CityInfo {
	
	private String name = "";
	private String location = "";
	private String information = "";
	private List<String> services;
	
	public CityInfo() {
		services = new ArrayList<>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getInfo() {
		return information;
	}
	
	public List<String> getServices() {
		ArrayList<String> services = new ArrayList<>();
		for (String s : this.services) {
			services.add(s);
		}
		return services;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addService(String service) {
		this.services.add(service);
	}
	
	public void setLocation(String loc) {
		location = loc;
	}
	
	public void setInfo(String info) {
		information = info;
	}
	
	public void setServices(List<String> services) {
		for (String service : services) {
			this.services.add(service);
		}
	}
}
