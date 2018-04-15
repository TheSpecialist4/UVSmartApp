module UVApp {
    interface TemperatureSensor {
        void getTemperature(string userName, int temperature);
    };
    
    struct UserDetails {
        int tempThreshold;
        int skinType;
    };
    
    interface PreferenceRepositoryIce {
        UserDetails getUserDetails(string userName);
    }
    
    interface ContextManagerIce {
        void loginUser(string userName);
    }
    
}