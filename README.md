# Checkpoint
Recommend running on the Pixel 4 API 32 for testing! 
---

### Design Document

Travis Newberry

Josh Anness

Christopher Negron

Brendan Payne

## Introduction

Driving in this fast-paced world, anything can happen. Do you want to adapt quickly to events that occur? Checkpoint can make it happen.

-	Weather events/changes
-	Events like traffic, accidents, and hazards
-	Police activity/speed traps

Use your Android device to navigate life with Checkpoint. Use GPS for real-time directions. Receive data and events about your surroundings and other user data. 

## Storyboard

[Checkpoint Storyboard](https://projects.invisionapp.com/prototype/Main-ckyx7vr3a0000l201fgsuoptd/play/e178daa4)

(![image](https://user-images.githubusercontent.com/46828931/151495872-8a9a229c-a250-41be-8400-e992c33c6eef.png)
 
## Functional Requirements

### Requirement 100.0: Weather Forecast

#### Scenario

As a user who wants to know what is going on during the day, I want to be able to see the weather projections and updates so I know what is going to happen

#### Dependencies

GPS and location will be available and viewable. Weather data in the location is also accessible.  

#### Assumptions

Data is stated in English. 

Radar is able to be interpreted. 

An internet connection at some point in the day. 

#### Examples
1.1

**Given** data on the weather is available

**When** I give my location

**Then** I should have a weather forecast

1.2

**Given** I am in an area with a weather warning

**Then** I should receive an alert about the warning

1.3

**Given** a weather event is going to happen at a certain time

**Then** I should get a warning ahead of time

1.4

**Given** internet connection is not available   

**Then** I should get the last saved weather report

### Requirement 101.0: Driving Events

#### Scenario

As a user that is driving, I want to know about events around my route, so that I can plan accordingly. 

#### Dependencies
GPS data is available, and the user has granted location access. 
The device knows it’s in a car.

#### Examples

1.1

**Given** the speed of the car

**Given** the speed limit in the area 

**When**

- “Alert when over speed limit” is selected

**Then** warn the user that they’re going over the speed limit

1.2

**Given** the information of a slowdown ahead

**Then** inform the user of the slowdown ahead

1.3

**Given** a crash, or blockade ahead

**Then** warn the user of the event

1.4

**Given** police reported in the route

**Then** warn the user about police ahead

## Class Diagram

![ClassDiagram](https://github.com/JoshAnness/Checkpoint/blob/UML/Checkpoint.drawio.png)

### Class Diagram Description

**MainActivity**:  The first screen the user sees. This will show the map with the driver and various inherited objects.

**Driver**:  Noun class that represents the driver. 

**Map**: Noun class that represents the map. 

**Route**: Noun class that represents the route. 

**Event**: Noun class that represents an event. 

**WeatherCondition**: Noun class that represents a weather condition. 

**WeatherStatus**: Noun class that represents the weather status. 

**IDriverDAO**: Interface to Driver.

**IMapDAO**: Interface to interact with Map data.

**IRouteDAO**: Interface for Route, will parse JSON.

**IEventDAO**: Interface for Event, will parse JSON.

**IWeatherDAO**: Interface for weather, will parse JSON. 

## Scrum Roles

[Scrum Board](https://github.com/JoshAnness/Checkpoint/projects)
- Product Owner: Josh Anness
- Scrum Master: Brendan Payne
- DevOps: Travis Newberry
- Developer: Christopher Negron

## Standup
Sundays at 8:00 on Teams
