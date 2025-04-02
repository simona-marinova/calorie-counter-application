Calorie Counter Application

This application, written in Java using Spring MVC, helps users manage their daily calorie intake and track their activity. 
It provides features for managing foods, meals, recipes, activities, user profiles, and daily statistics.

Key Features

Food Management:

Add foods with their calories per 100 grams. 

View all foods, added by the user. 

Search for foods. 

Meal Management:

View meals for the current day.

Add food items and custom recipes to meals.

Delete meals.

Recipe Management:

Create, view, edit, and delete custom recipes.

Add food items to recipes.

View public recipes.

Search for recipes.

Daily Statistics:

Calculate calorie goals and calories burned at rest.

Record current weight.

View daily statistics history.

Activity Tracking:

Record activities and calculate burned calories.

View activity history (last 30 activities).

REST API Interaction: When the "Add Activity" button is clicked, the application interacts with a REST API to calculate the calories burned for the specified activity 
and duration (only if the REST API burned calories tracker is enabled). The result is then saved and displayed.

The "Activities History" page displays the user's activity history, but only if the REST API burned calories tracker is enabled.

Technical Details

Framework: Spring MVC

Language: Java

Security: Spring Security

Validation: Jakarta Validation API

Templating Engine: Thymeleaf

REST API: Interacts with a REST API burned calories tracker for burned calories from activities calculation. 

How to Use
Registration: New users can register through the registration page.

Login: Registered users can log in to access the application's features.

Home: The home page displays meals, foods, recipes, acitivities, daily statistics and other. 

Meals: Users can view their meals for the day and add food or recipes to them.

My Recipes: Users can create and manage their own custom recipes.

Daily Statistics: Users can view their daily calorie goals, weight, and statistics.

Activities: Users can log their activities and see the history.

Edit Profile: Users can edit their profile information.

REST API Usage
The application uses a REST API burned calories tracker to calculate calories burned during activities. This happens when a user adds a new activity.

The activity history is also fetched from the REST API.

Dependencies
Spring Boot

Spring MVC

Spring Security

Jakarta Validation
