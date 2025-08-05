# weather-app
----------
### **Weather Forecast**

This microservice provides a 3-day weather forecast for any city, showing daily high and low temperatures. It also adds contextual alerts based on weather conditions:

1. [x] Carry umbrella if rain is predicted
2. [x] Use sunscreen lotion if temperature exceeds 40°C
3. [x] It’s too windy, watch out! if wind speed > 10 mph
4. [x] Don’t step out! A Storm is brewing! if thunderstorms are detected

### **Features**

1. [x] Accepts city and optional offlineMode as input parameters.
2. [x] Returns a clean summary of the next 3 days with necessary alerts.
3. [x] Supports offline fallback using cached weather data.
4. [x] Designed for extensibility, new alert conditions can be added with minimal code and without requiring major redeployments.

### **Sequence Diagram**

![img.png](img.png)

### **Design Patterns**

* **Strategy Pattern:** Used in the weather service to handle different processing strategies for online/offline modes
* **Facade Pattern:** WeatherApiClient acts as a facade to the external weather API
* **Decorator Pattern:** Used in processing weather data by adding alerts to the forecast
* **Singleton Pattern:** Spring components are singletons by default
* **Factory Pattern:** Used in creating different types of responses based on conditions

### **Production Readiness** 

* **Externalized Configuration:** API keys and URLs in properties files
* **Health Checks:** Spring Boot Actuator endpoints
* **Caching:** Offline mode support
* **Logging:** Structured logging with SLF4J
* **Error Handling:** Custom exceptions and global handler
* **API Documentation:** Swagger/OpenAPI
* **Containerization:** Docker support
* **CI/CD Pipeline:** Automated build, test, and deploy