package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PreferanceRepository {
	
	private Map<String, Preference> preferences;
	
	public PreferanceRepository(String fileName) {
		preferences = new HashMap<>();
	}
	
	private boolean readFile(String filename) {
		try {
			Scanner scanner = new Scanner(new File(filename));
			while (scanner.hasNextLine()) {
				
			}
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't read file \"" + filename + "\"exiting...");
			return false;
		}
		return true;
	}
	
	private class Preference {
	
		private String tempPref;
		private String uvPref;
		private SkinType skinType;
		
		public Preference(String tempPref, String uvPref, SkinType skinType) {
			this.tempPref = tempPref;
			this.uvPref = uvPref;
			this.skinType = skinType;
		}
		
		public String getTempPref() {
			return this.tempPref;
		}
		
		public String getUVPref() {
			return this.uvPref;
		}
		
		public SkinType getSkinType() {
			return this.skinType;
		}
	}
}
