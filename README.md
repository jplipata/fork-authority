# whatsforlunch
A tool to help you answer the toughest question of the day -- What's for lunch?!

#### Background
No matter how many stars a restaurant has on Yelp, you'll get sick of it if you eat there everyday.  #whatsforlunch let's you specify things like "just ate here", "like", or "don't like".  The app will provide you with a list of customized Yelp responses based on your feedback.

#### How It Works
Provides a list of nearby restaurants for you to choose from (powered by Yelp)

<img src="https://cloud.githubusercontent.com/assets/11450465/14062934/505f6230-f388-11e5-8cba-6428e8ff6b1d.gif" width="300">

<P>"Liking" a restaurant moves it to the top of the list

<img src="https://cloud.githubusercontent.com/assets/11450465/14063068/0980f214-f38f-11e5-830a-9b7c83fefe61.gif" width="300">  
<P>"Don't like" removes it

<img src="https://cloud.githubusercontent.com/assets/11450465/14063069/148cb490-f38f-11e5-9018-5560d72a85d9.gif" width="300">  
<P>"Just ate here" removes it for a couple days

<img src="https://cloud.githubusercontent.com/assets/11450465/14063070/1c92cbc0-f38f-11e5-80d0-80e35ae3c3a6.gif" width="300"> 
<P>Swipe cards away to help narrow down options

<img src="https://cloud.githubusercontent.com/assets/11450465/14063072/23027b72-f38f-11e5-8b01-af4bbf7de382.gif" width="300"> 
<P>Click the Refresh button to start over

<img src="https://cloud.githubusercontent.com/assets/11450465/14063074/28212a04-f38f-11e5-9d22-9fa426829fd5.gif" width="300"> 
<P>whatsforlunch will remember your preferences for next time

#### Developer Notes
This is my first Android app.  There are many areas that can be improved upon.  A lot of this is 'throw away code' that was written just to get things working that needs massive clean up. See development roadmap below.

The design similarity to Yelp is intentional.  Since this is basically a custom front-end for Yelp data, I wanted it to have a seemless UI feel. I also thought it would be a good exercise to try to replicate the Yelp UI from scratch ("great artists steal").

##### Enhancement Backlog
###### UI/Functionality
- Need icon
- User definable settings
- Get more results
- "Just ate here" active state icon
- Bug: if the user denies location permission and selects "never ask again", it will render the app useless (Marshmallow and above)

###### Code Quality
- Refactor: Overall architecture needs to be improved.  E.g. MainActivity is a "god object" and needs to be broken out into smaller classes.  
- Replace iterative List appraoch to UserRecords/BusinessListManager with hashmap
- Replace AsyncTask with something better (e.g. Retrofit)
- Replace findViewById with Butterknife
- Refactor: RecyclerView.Adapter onClick handlers should be defined in ViewHolder
