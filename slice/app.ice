module UVApp {
    interface TemperatureSensor {
        void getTemperature(string userName,string sensorType, int temperature);
    };
    
    interface LocationSensor {
        void getLocation(string userName, string sensorType, string value);
    }
    
    interface LocationServerIce {
        bool isLocationIndoor(string location);
    }
    
    struct UserDetails {
        int tempThreshold;
        int skinType;
        int currentTemp;
        string tempPref;
        string uvPref;
    };
    
    interface PreferenceRepositoryIce {
        UserDetails getUserDetails(string userName);
    }
    
    interface ContextManagerIce {
        void loginUser(string userName);
        string getInterest(string item);
        string getInterestInLoc();
    }
    
    interface UIIce {
        void printWarning(int value, string pref, bool isTemp);
    }
}