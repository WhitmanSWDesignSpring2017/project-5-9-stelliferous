Dear Professor Davis,

>Give a concise overview of your design. How did you divide the code into classes and methods? How does your design for Project 9 differ from your design to Project 8? How did you respond to feedback? (If I get it to you in time.)

For the project 9, we focused on adding features which did not result in big changes of the whole structure for our program. We added one new class to implement the popup menu feature and for other features, we just added new methods and fields because these are just an development for the current performance, which can still be counted as the responsibilities for the existed classes.
And all together, we implemented 6 new features.
a) We have a new class called PopUpMenu, which included two context menus, one for NoteRectangles and one for the CompositionPane so that the users can click on the pane to decide the position they want the noteRectangles to be pasted to. 
b) In the contextmenu for the rectangles, we have also added group, ungroup, copy and cut these menuitems and synchronized them with the menuitems in the menubar so that when the menuitems in the menubar are disabled, so will the ones in the context menu. We created these menuItems as fields rather than just a local variable in order to be able to access it from the menuBarController where all the disable and enable buttons actions are handled.
c) We added the pause, move forward and move backward actions for the TuneComposer so now we can manipulate the redline and start the composition in the middle rather than always have to start it from the beginning. We did so through resetting the translation animation for the redline when the endcomp and the above actions are called, and resetting the start tick and duration of the notes for the midiplayer.
d) We added several boolean values and new functions inside the NoteRectangle class, so now we can allow the user to stretch the noteRectangles from the 5 pixels within the left edge.
e) We added a properties pane on the left side of the pane, just below the duration slider, so when the user is creating a new noteRectangle or selecting a rectangle, the properties including xPosition, yPosition, width, instrument and the number of gestures it is in would be presented in that pane. This allows the user to get more specific knowledge regarding each NoteRectangles.
f) We opened the access of instrument to the user so that they are allowed to change the instrument for the exisited note. The user needs to select the instrument they want to change to first and then through the context menu for the rectangles, clicking the change instrument button and this actions would calls for the program to read from the radiobutton again and change the selected notes to that instrument from the NoteRectangle class.

In the feedback we got from the project 8, there's no comment on the structure of the class and the design flaws we might possess, so we basically refactored the way we saw to be appropriate.

>Explain why your way was the elegant way to do it. Address any improvements you made based on my feedback.

We still split JavaFX classes into different classes and decomposemethods to try to meet Single Responsibility Principle on both the class and method levels. We also keep including JavaDocs and internal comments, although we usually add them after writing the code. Since the structure of our program/code still has no need to use inheritance, our program is less coupled than it would be with inheritance. We do not use interfaces, and thus cannot really pursue the Interface Segregation Principle. Still, we try to keep the design ideas behind Interface Segregation by avoiding a "god" class and splitting up smaller responsibilities among multiple classes. In order to adhere to the Open/Closed Principle, we use encapsulation when applicable. For this final project, most of the additions we made did not call for the creation of whole new classes, but we did make a PopUpMenu class for a right-click menu rather than using fields within other classes. By making this class, we continue to meet the Single Responsibility Principle and the Open/Closed Principle by only including popup menu handlers and variables in the class. We also reduce duplication of code by calling MenuBarController handlers instead of rewriting the handlers for the popup menu.

Based on the project 8 feedback we received, we worked on giving our classes more intention-revealing names and including more internal documentation, striving for better self-explanatory code. 

>Explain what, if anything, in your solution is inelegant and why you didn't make it elegant (for example, maybe you didn't have time or the knowledge to fix it).

Perhaps because we did not follow the MVC pattern from the beginning of our project, we continue to violate the Law of Demeter, since many controllers go through the main controller to get something done. If we had used the MVC pattern to create our project, our classes would be less dependent on each other since they would each be "confined" to being either a model, view, or controller class. However, breaking this law still simplifies the structure of our code, and as such, the violation makes our code easier to read and more maintainable, other big factors of elegance. The accumulated coupling of our classes that this violation of the Law of Demeter causes is still concerning, although at this point it would take quite a long time to restructure our project to fix this problem.

We still don't use the Composite pattern to group and ungroup notes into gestures. This would make our program simpler and our code pattern more easily recognizable to other programmers reading it. However, we believe the way we created and handled gestures is more intuitive and slightly more simple than using the Composite pattern. In many ways, our PopUpMenu class follows the ideas of the Adaptor pattern, but it doesn't exactly match the components of the pattern.

If we had more time and "programming wisdom", we would refactor more and work on making our code more elegant, but as usual, these precious things are hard to come by.

>Include an estimate of your velocity. How many story points did you estimate you would complete during this project? How many did you actually complete, how many person-hours did the team spend, and what is the ratio of points/person-hour?

We estimated that we would complete 22 story points, and actually ended up completed about 23 (still working at getting better at estimation). We spent about 27.5+ hours on this project, so around 6.875 hours per person, and that means that the rate of points/person-hour was about 0.83636364. In this project our velocity speeded up, mainly because we assigned the features we want to implement individually ahead of time and every one understood what their jobs were. And these features do not interfere each other so there's no overlapping in code we need to refactor later. This clearly showed that depending on the nature of the work, the rate would certainly vary as well.

>Include a short summary of your team retrospective. What went well that your team will keep doing during the next project assignment? What will you improve? How?

As always, communication was the key to success (or what we hope is success!). We met early and (relatively) often, helping each other on our different features. We've certainly improved our use of GitHub, and learned a lot about the connection between GitHub and NetBeans over the course of this semester. Although this is the last project assignment of the semester, we'll take away a lot of experience in working with a team, and will keep in my the need to prioritize and 'play poker' with projects of all sorts and sizes. 

Sincerely yours,

Kaylin, Zach, Jing, and Tyler

