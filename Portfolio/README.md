# Happy Weight

## Summary
In developing this mobile application named Happy Weight, the idea was simple. The idea was to provide an application that authenticated users can track their weight over time. Allow them to set a goal weight and alert that user that their goal weight was reached via SMS messaging. 

The application required a few UI screens and functionality. To start thgere was the need for a logina nd user registration screen. This provides the ability for new users to create an account and existing users to login. Then there was two main screens that were developed. The weight overview screen provided a simple graph of all of the user's entered weights as well as the ability to set a goal weight that was also projected onto the graph. The second main screen was the weight tracker screen. This is the screen that allows the users to add, modify, and delet weight data.

The coding of the application to acheiev all of the functionality set out was approached with NavigationComponents. This allowed for a single MainActivity and then utilizing fragments for each of the screens. Using the navigation graph capabiliites then I was able to link the screens together with actions and provide a simple navigation. I chose to use a bottom navigation bar as well for users to move between the overview and tracker sections. 

All testing was done with the emulator as well as two physical Google Pixel phones connected via USB with USB Debugging mode turned on. This allowed me to test my application accross multiple vendors of phones and esnure the aplpication was working well.