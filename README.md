# whatsforlunch
A tool to help you answer the toughest question of the day -- What's for lunch?!

Provides a list of nearby restaurants for you to choose from (powered by Yelp)

"Liking" a restaurant moves it to the top of the list

"Don't like" removes it

"Just ate here" removes it for a couple days

Swipe cards away to help narrow down options

whatsforlunch will remember your preferences for next time

#### Developer Notes
This is my first Android app.  There are many areas that can be improved upon.  A lot of this is 'throw away code' that was written just to get things working that needs massive clean up. See development roadmap below.

The design similarity to Yelp is intentional.  Since this is basically a custom front-end for Yelp data, I wanted it to have a seemless UI feel. I also thought it would be a good exercise to try to replicate the Yelp UI from scratch.

##### Enhancement Backlog
###### UI/Functionality
- Need icon
- User definable settings
- Get more results
- Bug: if the user denies location permission and selects "never ask again", it will render the app useless (Marshmallow and above)

###### Code Quality
- Refactor: MainActivity is a "god object" and needs to be broken out into a better architecture
- Replace iterative List appraoch to UserRecords/BusinessListManager with hashmap
- Replace AsyncTask with something better (e.g. Retrofit)
- Refactor: RecyclerView.Adapter onClick handlers should be defined in ViewHolder


