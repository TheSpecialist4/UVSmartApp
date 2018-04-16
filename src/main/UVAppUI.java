package main;

import java.util.Scanner;

import com.zeroc.Ice.Current;
import UVApp.ContextManagerIcePrx;
import UVApp.UIIce;

public class UVAppUI {
	
	private static ContextManagerIcePrx contextManagerProxy;
	
	private static void startApp() {
		System.out.print("Context-aware UV Smart Application\nPlease enter your user name: ");
		Scanner scanner = new Scanner(System.in);
		getUsername(scanner);
		Thread th = new Thread() {
			String option = "";
			public void run() {
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
		};
		if (th.isAlive()) {
			th.interrupt();
		}
		th.start();
	}
	
	private static void getUsername(Scanner scanner) {
		String username = scanner.nextLine();
		contextManagerProxy.loginUser(username);
	}
	
	private static void getItemInfo(Scanner scanner) {
		System.out.print("Please enter name of item of interest: ");
		String item = scanner.nextLine();
		System.out.println(contextManagerProxy.getInterest(item));
	}
	
	private static void getLocationInfo() {
		System.out.println(contextManagerProxy.getInterestInLoc());
	}
	
	private static void exitApp() {
		return;
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
			
//			com.zeroc.Ice.ObjectAdapter adapter = communicator.
//					createObjectAdapterWithEndpoints("UIIce", "default -p 20300");			
//			
//			adapter.activate();
			
			startApp();
			
			communicator.waitForShutdown();
		}
	}
	
	class UIIceI implements UIIce {

		@Override
		public void printWarning(int value, String pref, boolean isTemp, Current current) {
			if (isTemp) {
				System.out.println("Warning, Temperature is now " + value 
						+ "\nSuggestion - please go to " + pref);
			}
			
		}
		
	}
}
