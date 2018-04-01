package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PreferenceRepository {
	
	private Map<String, PersonPreference> preferences;
	
	public PreferenceRepository(String filename) {
		System.out.println(filename);
		filename.replace(" ", "%20");
		preferences = new HashMap<>();
		if (!readFile(filename)) {
			System.err.println("error reading file..exiting");
			return;
		}
		printRepoContents();
	}
	
	private boolean readFile(String filename) {
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(new File(filename)));
			String line = "";
			PersonPreference person = new PersonPreference();
			while ((line = buffer.readLine()) != null) {
				if (line.trim().isEmpty()) {
					preferences.put(person.getName(), person);
					person = new PersonPreference();
					continue;
				}
				Scanner scanner = new Scanner(line);
				while (scanner.hasNext()) {
					String token = scanner.next();
					switch (token) {
					case "name:":
						String n = scanner.next();
						person.setName(n);
						break;
					case "Skin":
						if (!"Type:".equals(scanner.next())) { return false; }
						if (!scanner.hasNextInt()) { return false; }
						if (!setSkinType(person, scanner.nextInt())) { return false; }
						break;
					default:
						if (!token.contains("pref")) { return false; }
						if (!parsePreferenceLine(scanner, person)) { return false; }
						break;
					}
				}
			}
			if (!person.name.isEmpty()) {
				preferences.put(person.getName(), person);
			}
			buffer.close();
		} catch (IOException e) {
			System.err.println("Error reading the file \"" + filename + "\"exiting...");
			return false;
		}
		return true;
	}
	
	private boolean setSkinType(PersonPreference person, int skinType) {
		switch(skinType) {
		case 1:
			person.setSkinType(SkinType.ONE);
			break;
		case 2:
			person.setSkinType(SkinType.TWO);
			break;
		case 3:
			person.setSkinType(SkinType.THREE);
			break;
		default:
			return false;
		}
		return true;
	}
	
	private boolean parsePreferenceLine(Scanner scanner, PersonPreference person) {
		while (scanner.hasNext()) {
			if (!"when".equals(scanner.next())) { return false; }
			if (scanner.hasNextInt()) {
				int temp = scanner.nextInt();
				if (!"suggest".equals(scanner.next())) { return false; }
				if (!scanner.hasNext()) { return false; }
				return person.addTempPref(temp, scanner.next());
			} else {
				if (!"UVO".equals(scanner.next())) { return false; }
				if (!"suggest".equals(scanner.next())) { return false; }
				if (!scanner.hasNext()) { return false; }
				return person.addUVPref(scanner.next());
			}
		}
		return true;
	}
	
	private void printRepoContents() {
		System.out.println("____________REPO CONTENTS________________");
		System.out.println("count: " + preferences.size());
		for (String name : preferences.keySet()) {
			System.out.println(preferences.get(name));
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please enter the preference filename in Eclipse");
			return;
		}
		PreferenceRepository pr = new PreferenceRepository(args[0]);
	}
	
	private class PersonPreference {
	
		private String name;
		private Map<Integer, String> tempPrefs;
		private String uvPref;
		private SkinType skinType;
		
		public PersonPreference() {
			name = new String();
			this.tempPrefs = new HashMap<>();
			this.uvPref = new String();
		}
		
		public void setSkinType(SkinType skinType) {
			this.skinType = skinType;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public SkinType getSkinType() {
			return this.skinType;
		}
		
		public String getName() {
			return this.name;
		}
		
		public boolean addTempPref(int temp, String pref) {
			return tempPrefs.put(temp, pref) == null;
		}
		
		public boolean addUVPref(String pref) {
			this.uvPref = pref;
			return true;
		}
		
		@Override
		public String toString() {
			StringBuilder output = new StringBuilder();
			output.append(String.format("name: %s\nSkin Type: %s\n", name, getSkinType()));
			output.append(String.format("UVO: %s\n", uvPref));
			for (int temp : tempPrefs.keySet()) {
				output.append(String.format("temp %d: %s\n", temp, tempPrefs.get(temp)));
			}
			return output.toString();
		}
	}
}
