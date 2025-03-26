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