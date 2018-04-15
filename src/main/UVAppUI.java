package main;

import java.util.Scanner;

import UVApp.ContextManagerIcePrx;

public class UVAppUI {
	
	private static ContextManagerIcePrx contextManagerProxy;
	
	private static void startApp() {
		System.out.print("Context-aware UV Smart Application\nPlease enter your user name: ");
		Scanner scanner = new Scanner(System.in);
		getUsername(scanner);
		String option = "";
		do {
			printMainMenu();
			option = scanner.nextLine();
			switch (option) {
			case "1":
				getItemInfo(scanner);
				break;
			case "2":
				getLocationInfo();
				break;
			case "E":
				exitApp();
				break;
			}
		} while (!option.equals("E"));
		scanner.close();
	}
	
	private static void getUsername(Scanner scanner) {
		String username = scanner.nextLine();
		contextManagerProxy.loginUser(username);
	}
	
	private static void getItemInfo(Scanner scanner) {
		System.out.print("Please enter name of item of interest: ");
		String item = scanner.nextLine();
		System.out.println("Information about " + item);
	}
	
	private static void getLocationInfo() {
		System.out.println("The following items of interest are in your location:");
	}
	
	private static void exitApp() {
		
	}
	
	private static void printMainMenu() {
		System.out.println("Context-aware UV Smart Application Main Menu\nPlease select an option:\n" + 
				"1. Search for information on a specific item of interest\n" + 
				"2. Search for items of interest in current location\n" + 
				"E. Exit");
	}
	
	public static void main(String[] args) {
		
		try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
			com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("ContextManagerIce:default -p 20200");
			contextManagerProxy = ContextManagerIcePrx.checkedCast(base);
			
			startApp();
		}
	}
}
