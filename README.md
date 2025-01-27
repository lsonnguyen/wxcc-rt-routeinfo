# WebexCC Realtime Route Info - wxcc-rt-routeinfo
Sample project to demonstrate sharing WebexCC Queue and Team real-time & summary stats via REST API. The solution primarily aims to enable on-premise CCX/CCE clients to make intelligent routing decisions based on the current state of WebexCC to facilitate a smooth and incremental transition to the cloud platform.

### Environment Setup
Ensure the project build environment is configured with the required libraries and tools listed below.

* [Java SDK17 or later](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Gradle Build Tool](https://gradle.org/releases/)

### Project Setup

* Create a new [WxCC App](https://developer.webex-cx.com/my-apps) to generate the Client ID & Secret with the foolwing settings
  * Scopes: cjp:config, cjp:config_write, cjp:config_read, spark:people_read, cjp:user
  * Redirect URL: [Service Base URL]/login/oauth2/code/wxccrouter
* Clone or download and extract the project to a local directory
* Update the project /src/main/resources/application.yaml with the Client ID, Secret, and Redirect URL
* Follow Gradle documentation to build and run the service executable JAR

IMPORTANT: The service URL must be SSL-enabled (HTTPS) to be accepted for event notification webhook URL.

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
  * teams: list of team associated with the queue
  
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
For further reference, please consider the following sections:

* [OAuth2 Client](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web.security.oauth2.client)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web.reactive)
* [Official Gradle documentation](https://docs.gradle.org)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

