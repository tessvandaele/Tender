# Tender

## Table of Contents
1. [Overview](#overview)
2. [Product Spec](#product-spec)
3. [Wireframes](#wireframes)
4. [Schema](#schema)

## Overview
### Description
The Food Tinder app is a spin on the classic Tinder app wherein users can swipe on food, rather than people, to find a place to eat. Users can either swipe right to "match" with a food location and have it added to their interest list or swipe left to dismiss the food service. The app will provide a detailed view of the chosen restaurant with information such as customer rating, location, hours, and pictures. The app will also provide navigation to restaurants and a reviewing service for users to leave reviews of visited restaurants.

### App Evaluation

- **Category:** Food
- **Mobile:** This app provides an easy and fun way to find food near you. Browsing restaurant websites online is often frustrating on a mobile device so this app makes it a clean, simple process.
- **Story:** A user will be provided with a set of food options which can be filtered based on preferences such as type of food, price level, and distance. The user can then swipe to indicate interest or disinterest in the food service. When the user is interested in the restaurant, the app will offer them extra details and navigation to the service.
- **Market:** This app is targeted mostly towards young adults who are likely familiar with the tinder platform and eat out frequently. Generally, however, the app can be used by anyone searching for a place to eat near them.
- **Habit:** This app is used when the user needs to eat a meal but cannot decide where to eat or wants to try something new.
- **Scope:** This app can have varying levels of technicality. It's basic form will simply be a swiping app in which restaurants can be added to an interested list. Then, additional features such as navigation and reviewing can be addded.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [ ] User can login to their profile
- [ ] User can view a set of restaurants (displayed like a deck of cards)
- [ ] User can swipe left to dismiss the card or swipe right to add the card to their interested list
- [ ] User can view a list of restaurants they are interested in
- [ ] User can remove restaurants from their interested list
- [ ] User can view a detailed screen of information on a specific restaurant

**Optional Nice-to-have Stories**

- [ ] User can change preferences on what restaurants to see
- [ ] User can get navigation to a restaurant
- [ ] User can leave a review of a restaurant
- [ ] User can personalize their profile (profile image, user name, address, etc.)
- [ ] The app has a logo and coherent theme throughout
- [ ] User can log out of their profile

### 2. Screen Archetypes

* Login view
   * User can login to their profile
   * The app has a logo and coherent theme
* Card view
   * User can view a set of restaurants
   * User can swipe left and right depending on their oppinion
* Interested view
    * User can view a list of restaurants they are interested in 
    * User can remove restaurants from their interested list
* Details view
    * User can view a screen with extra information on a specific restaurant (information includes 5-star rating, location, bussiness hours, menu, etc.)
    * User can get navigation to the restaurant
    * User can leave a review for a restaurant
* Account view
    * User can personalize their viewing preferences
    * User can personalize their profile
    * User can log out of their account

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home screen
* Interested screen
* Account settings screen

**Flow Navigation** (Screen to Screen)

* Login screen
   * Home screen
* Home screen
   * Account settings screen
   * Interested screen
* Interested screen
    * Home screen
    * Details screen
* Account settings screen
    * Home screen

## Wireframes

### Digital Wireframes

<img src='wireframe/Startup.png' title = 'Startup' width='200' alt='Startup Screen' /> <img src='wireframe/Login.png' title = 'Login' width='200' alt='Login Screen' /> <img src='wireframe/Swipe.png' title = 'wireframe/Swipe' width='200' alt='Swipe Screen' /> <img src='wireframe/RestaurantList.png' title = 'List' width='200' alt='List Screen' /> <img src='wireframe/Details.png' title = 'Details' width='200' alt='Details Screen' /> <img src='wireframe/Profile.png' title = 'Profile' width='200' alt='Profile Screen' />



### Demo Video

Here is a demo of the app wirframes:

<img src='wireframe/TenderWireframe.gif' title='Video Walkthrough' width='200' alt='Video Walkthrough' />

GIF created with [LiceCAP](https://www.cockos.com/licecap/).

## Schema

### Models

Restaurant

| Property | Type | Description |
| -------- | -------- | -------- |
| id     | string     | Unique Yelp ID of this business.|
| name     | string     | Name of this business.|
| categories     | object[]     | List of category title and alias pairs associated with this business.     |
| image_url     | string     | URL of photo for this business|
| website_url | string | URL for business page on Yelp. |
| rating     | number | Rating for this business (value ranges from 1, 1.5, ... 4.5, 5).|
| phone_num     | string     | 	Phone number of the business.|
| location     | object     | Location of this business, including address, city, state, zip code and country. |
| hours     | object[] | Opening hours for bussiness |
| distance | number | Distance in meters from the search location. This returns meters regardless of the locale. |
| price | string | Price level of the business.|
| reviews | review[] | List of reviews on the bussiness. |

Review

| Property | Type | Description |
| -------- | -------- | -------- |
| username     | string     | User screen name. |
| text     | string     | Text excerpt of this review.|
| rating     | number     | Rating for this review. |
| image_url     | string     | URL of the user's profile photo.|
| time_created | string | Time at which the review was posted |

User

| Property | Type | Description |
| -------- | -------- | -------- |
| username     | string     | User screen name |
| name     | string     | Name of user|
| location     | string     | Location of user |
| profile_url | string     | URL of profile picture |
| radius | number | Maximum radius in which user wants to view restaurants |
| price     | number | price range of restaurants user wants to view |
| categories | string[] | Categories of restaurants to filter search results | 
| open_now | boolean | If true, user only wants to be shown food that is currently open |

### Networking

#### List of network requests to Parse database by screen

* Swipe Screen
    * (Read/GET) Retrieve logged in user preferences to feed to Yelp API
 
* Profile Screen
    * (Read/GET) Query logged in user object
    * (Update/PUT) Update user profile info (profile image, name, and preferences)

#### Network requests for Yelp API

| HTTP Verb | Endpoint | Description |
| -------- | -------- | -------- |
| GET     | /bussinesses/search | Retrieves all restaurants with provided parameters |
| GET     | /businesses/{id}    | Retrieves mre specific information of restaurant given id |
| GET     | /businesses/{id}/reviews     | Retrieves a list of reviews provided a restaurant id|
