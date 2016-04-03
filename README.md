# whatsforlunch
A tool to help you answer the toughest question of the day: What's for lunch?!?

#### Background
<img src="https://cloud.githubusercontent.com/assets/11450465/14229888/806b9cd6-f910-11e5-9f5f-982ea09fe3a1.gif" width="325" align="right">

No matter how many stars a restaurant has on Yelp, you'll get sick of it if you eat there everyday.  #whatsforlunch let's you specify things like "just ate here", "like", or "don't like".  The app will provide you with a list of customized Yelp search results based on your feedback.  You can also "swipe to dismiss" to help narrow down your options.

#### How It Works

Provides a list of nearby restaurants for you to choose from (powered by Yelp)

"Liking" a restaurant moves it to the top of the list

"Don't like" removes it
 
"Just ate here" removes it for a couple days

Swipe cards away to help narrow down options

Click the Refresh button to start over

whatsforlunch will remember your preferences for next time

#### Developer Notes
This is my first Android app.  There are many areas that can be improved upon.  A lot of this is 'throw away code' that was written just to get things working that needs massive clean up. See development roadmap below.

The design similarity to Yelp is intentional.  Since this is basically a custom front-end for Yelp data, I wanted it to have a seemless UI feel. I also thought it would be a good exercise to try to replicate the Yelp UI from scratch ("great artists steal").

##### Enhancement Backlog
###### UI/Functionality
- Need app icon
- Provide feedback to user when device Location is not enabled
- User definable settings
- Replace text location coordinates with map view
- Get more results
- "Just ate here" active state icon and dynamic text based on state
- Bug: if the user denies location permission and selects "never ask again", it will render the app useless (Marshmallow and above)

###### Code Quality
- Refactor: Overall architecture needs to be improved, implement e.g. MVP pattern. (Currently, MainActivity is a "god object" and needs to be broken out into smaller classes.)
- Replace iterative List approach to UserRecords/BusinessListManager with hashmap
- Replace AsyncTask with something better (e.g. RxJava/Retrofit)
- Replace findViewById with Butterknife
- Refactor: RecyclerView.Adapter onClick handlers should be defined in ViewHolder
