# ImageSearch App

A simple Google image search Android app.
The full requirements for this app can be found [here](http://courses.codepath.com/courses/intro_to_android/week/2#!assignment).

Time spent: 10 hours

### Completed user stories:

Except for image sharing and ability to zoom or pan images, all required and advanced user stories were completed. Following is the list for the same:

#### Required:

 * [x] User can enter a search query that will display a grid of image results from the Google Image API
 * [x] User can click on filter icon to set advanced search options like size, color, type, and site
 * [x] Search keyword and advanced search options are persisted in shared preferences and used wherever applicable
 * [x] User can tap on any image and see it in full screen image view
 * [x] User can scroll through the list infinitely and new images are loaded on scroll
   
#### Advaned:

 * [x] Robust error handling for failure scenarios and network errors
 * [x] Search text box is presented in the action bar
 * [x] Filters are presented in a modal overlay and are trigered by a button in the action bar
 * [x] Improved UI by 
 	- Custom theme - app icon, lighter fonts, backgrounds, etc.
 	- Animate hiding of system UIs in full screen mode

#### Bonus:

 * [x] Use a staggered grid view for presenting image results
 
### Notes:

- Used Android Studio for development
- Used Genymotion emulator 

### Walkthrough of all user stories:
<br />
![Video Walkthrough](AndroidInstagramViewerApp.gif)
<br />
<br />

GIF created with [LiceCap](http://www.cockos.com/licecap/).<br />

### Third party libraries, tools, and sites used:

- [Active Async](http://loopj.com/android-async-http/doc/com/loopj/android/http/AsyncHttpClient.html) for making network requests.
- [Picasso](http://square.github.io/picasso/) for downloading and caching images.
- [StaggeredGridView](https://github.com/f-barth/AndroidStaggeredGrid) for the grid view.
- [IconFinder](https://www.iconfinder.com) for app icons.
- [iconmonstr](http://iconmonstr.com) for images/assets in the app.
