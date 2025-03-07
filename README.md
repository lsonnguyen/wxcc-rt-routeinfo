# WebexCC Realtime Route Info - wxcc-rt-routeinfo
Sample project to demonstrate making available WebexCC Queue and Team real-time & summary statistics via REST API. The solution primarily aims to enable on-premise CCX/CCE clients to make intelligent routing decisions based on the current state of WebexCC to facilitate a smooth and incremental transition to the cloud platform.

### Environment Setup
Ensure the project build environment is configured with the required libraries and tools listed below.

* [Java SDK17 or later](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Gradle Build Tool](https://gradle.org/releases/)

### Project Setup

* Create a new [WxCC Integration](https://developer.webex-cx.com/my-apps) to generate the Client ID & Secret with the following settings
  * Scopes: cjp:config, cjp:config_write, cjp:config_read, spark:people_read, cjp:user
  * Redirect URL: [Service URL]/login/oauth2/code/wxccrouter
* Clone or download and extract the project to a local directory
* Update the project /src/main/resources/application.yaml with the Client ID, Secret, and Redirect URL
* Follow Gradle documentation to build and run the service executable JAR
* Once the service has started, open the Service URL from a browser and log in using your WebexCC Admin account to activate the service.

IMPORTANT: The deployed Service URL must be SSL-enabled (HTTPS) to be accepted by WebexCC Subscription API for event notification webhook URL.

### API Endpoints and Stats Details
#### Queue Stats 
* Endpoint: /stats/queue/{name}
* Data (Current Date)
  * queuedCount: number of calls currently in queue
  * connectedCount: number of calls currently connected to agents
  * abandonedCount: number of calls that has been abandoned while in queue
  * agentCount: number of agents currently logged in and serving the queue
  * avgQueueTime: average queue time computed for the current interval
  * avgTalkTime: average talk/connected time computed for the current interval
  * estWaitTime: estimated wait time for the queue
  * maxWaitTime: current longest wait time in queue
  * teams: list of agent teams associated with the queue
  
#### Team Stats
* Endpoint: /stats/team/{name}
* Data (Current Date)
  * avgIdleTime: average idle time for agents in the team
  * avgAvailTime: average available time for agents in the team
  * avgTalkTime: average talk time for agent in the team
  * avgTotalTime: average total session duration for agents in the team
  * idleAgents: number of agents currently in idle state
  * availAgents: number of agents currently in available state
  * talkAgents: number of agents currently in connected state 


### Reference Documentation
* [WebexCC Subscriptions API](https://developer.webex-cx.com/documentation/subscriptions)
* [Event Notification Webhook Guide](https://developer.webex-cx.com/documentation/guides/using-webhooks)

### Additional References
* [OAuth2 Client](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web.security.oauth2.client)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web.reactive)
* [Official Gradle documentation](https://docs.gradle.org)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
* [Lombok Setup](https://projectlombok.org/setup/)