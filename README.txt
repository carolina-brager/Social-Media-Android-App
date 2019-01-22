Final Project for CSC 216 - Mobile App Development in Android. 

The goal of the project was to design a social media app. The project uses the Model-View-Controller design pattern for organization and separation of concerns.

Model: includes information for the database of the app.
View: All of the activites for the app which are separated into non-logged in activties and logged in activities.
Controller: Classes for calculation of date of birth and for images used in the app.

The database contains three tables:
User: Contains information about each user
Post: Contains information about all of the posts, and has a secondary key of the user who made the post
Favorites: Contains information about the favorites of a user, has a secondary key of the primary user
