# CalendarAPI
Struts based Calendar API 


## 19/03/2025

- **CalendarAction:**

- Created methods for CRUD operations in the Calendar table
- Designed and created schema for the CalendarAPI project


## 24/03/2025

- Redesigned calendars table by adding constraint for unique calendar name 
- Changed PUT and DELETE operations to work upon calendar id 
- Created Event Action for create events from request using postman
- Overloaded event class for accepting events with repeat and without one 

- Used a simple UI calendar.html for frontend
- Added a sidebar that shows all calendars of current user after login 
- Added edit option to each calendar name in the sidebar

- Created a popup form for event create operation 
- Linked the event popup form to every date in the calendar
- Used lazy data filler in date based on repeat field
- Made the event create form dynamic for custom repeat events 


## 25/03/2025

- Moved the request part to frontend for event create operation

## 27/03/2025

- Worked on the event operations 
- Populated recurrent events in the calendar till end of year 
- Completed full create event operation and delete operation
- Managed to store small database for the case of recurrent events


# 28/03/2025 

- Restruturing the database to suit recurring events in scheduling appointments in FSM
- Created class for schedule 
- Started with post operation for schedule action code



# 31/03/2025

- Completed schedule post operations for all custom types 
- Populated for daily, monthly, weekly, yearly recurring schedules
- Fine tuned variable in schedule class for easy convention
- Verified backend for the population criteria and post operations


# 01/04/2025

- Completed schedule put and delete methods 
- Overloaded schedule class constructors
- Secured the API calls by using JWT token with username as the subject
- Created dynamic UI for repeat events 

# 02/04/2025

- Made changes to the UI for CRUD operations on schedule
- Working on schedule edit page 

# 03/04/2025 

- Replaced authentication filters in web.xml with struts interceptors 
- Removed url parameters that are unused
- Endpoint management for login action page and lazy login implementation
- Fetching user data from token to avoid tampering endpoints
- Managed endpoint flow within compact struts.xml resolving redundancy
- Restructuring struts xml to have custom mapper class 
- Compressing endpoints CRUD to work on single endpoint 
- Created a custom rest mapper class that sets action method dynamically for each endpoint and it's request method 
- Addition of jar files for rest => xwork-core, adm, struts-rest-plugin, xstream
- Refactored com.model package to com.utils for naming convention
- Dynamic method mapping works on login post and options method

Note : To solve the inputName error thrown by struts xml file use non static getter and setter for inputstream variable in action 