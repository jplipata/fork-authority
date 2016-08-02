# whatsforlunch
A tool to help you answer the toughest question of the day: What's for lunch?!

#### Background
<img src="https://cloud.githubusercontent.com/assets/11450465/14229888/806b9cd6-f910-11e5-9f5f-982ea09fe3a1.gif" width="325" align="right">

No matter how many stars a restaurant has on Yelp, you'll get sick of it if you eat there everyday.  #whatsforlunch let's you specify things like "just ate here", "like", or "don't like".  The app will store your preferences and provide you with a personalized list of search results.  You can also "swipe to dismiss" to help narrow down your options.

#### How It Works

Provides a list of nearby restaurants for you to choose from (powered by Yelp)

"Liking" a restaurant moves it to the top of the list

"Don't like" removes it
 
"Just ate here" removes it for a couple days

Swipe cards away to help narrow down options

Click the Refresh button to start over

whatsforlunch will remember your preferences for next time

#### Developer Notes
The design similarity to Yelp is intentional.  Since this is basically a custom front-end for Yelp data, I wanted it to have a seemless UI feel. I also thought it would be a good exercise to try to replicate the Yelp UI from scratch ("great artists steal").

##### Enhancement Backlog
###### UI/Functionality
- Need app icon
- More results, better way to manage filtered categories
- User definable settings/search query
- "Just ate here" active state icon and dynamic text based on state
- More sophisticated suggestion logic, such as looking for patterns in categories for recommendations (e.g. if a user tends to like Japanese, businesses with category = Japanese will get sorted to top of list

###### Code Quality
- Refactor: Overall architecture needs to be improved, implement e.g. MVP pattern.
- Replace iterative List approach to UserRecords/BusinessListManager with hashmap
- Replace findViewById with something better, e.g. Butterknife or Data Binding Library
- Refactor: RecyclerView.Adapter onClick handlers should be defined in ViewHolder

License
--------

    Copyright 2016 tenprint

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
