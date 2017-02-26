*Team Tweb Project 3 Reflection*

>Give a concise overview of your design. How did you divide the code into classes and methods? 
>How does your design for Project 3 differ from your design(s) for Project 2?

Our design is fairly simple, handling events and running the program in JavaFX, providing most of the GUI (except for rectangles and red lines) with FXML, and adding just a few lines of CSS. Our Project 3 design differs from Project 2 in that we handle some elements of the display/GUI through our JavaFX code, to allow greater user interaction. We did change the MidiPlayer.java file, so that the play() method plays the composition from the beginning.

Furthermore, we expanded the program in two ways: (1) the user can create a new note by dragging, as well as clicking, the mouse (2) the user can 'clear' a composition through the Action menu or keyboard shortcuts. 

>Explain why your way was the elegant way to do it. Address any improvements you made based on my feedback.

Because this is still a relatively small, simple program, we felt that a single JavaFX class represented the simplest way to design the program -- too much more modularity would've made it needlessly complex, and the current design allowed plenty of room for growth (in the form of extensibility, scalability... evidenced by the addition of the 'clear' button). We continued including detailed comments, javadocs, and self-explanatory code (where possible). The code is fairly robust, as the use of 'private' access modifiers and limited user inputs (clicks, menu items) limits what can go wrong. 

>Explain what, if anything, in your solution is inelegant and why you didn't make it elegant 
>(for example, maybe you didn't have time or the knowledge to fix it).

There's always more refactoring (renaming, javadoc additions, reorganizing) that can be done, but the existence of a due date limited how much we could increase the elegance in that regard. Changing the MidiPlayer.java wasn't recommended, but it was the most efficient and clear way to make our program function in the way we desired (with our limited knowledge of Midi, Sequencers, etc). We could have found another way to make the grey lines appear, but using a GridPane solely in FXML limited the amount of layout that TuneComposer.java had to handle; even though the GridPane required many lines of code, they're simple, maintainable, and readable, more so than would be creating another node in TuneComposer. Finally, we did think about breaking our JavaComposition.java class into a few more classes (i.e. create a separate class for rectangle creation), but decided that those changes would actual increase complexity and decrease maintainability -- again since our program is on the smaller side.

>Finally, describe how your team collaborated on the project. What did you do together? What did you do separately? 
>What did each team member contribute? Optionally, include a brief team retrospective: What is one thing you did well as a team? What is one thing you could have improved?

We met once to discuss the project as a whole and break up into smaller groups, and then mostly communicated online (via GitHub, social media). Will and Tyler met as a team, and Eric and Ben later met as a team (both in-person). Most major changes were made in-person, although individuals refactored and added to the code seperately as well. Will and Tyler worked alongside each other and exchanged ideas, but did not work on the same features at the same time. Ben and Eric ____________. Tyler contributed the basic GUI of the composition window (layout, scrollbars, menu, etc), creating and adding to the MidiComposition (connecting FXML to TuneComposer.java and MidiPlayer.java, adding notes, etc), and the "clear" menu option. Will solved the 'rectangle-placing' problem, creating the mechanism for rectangles to be added to the composition on-click. Eric ____________ . Ben _________________ . 

_ We worked well as individuals and had a healthy flow of ideas amongst the group, but next time we could collaborate even more. Given that much of these assignments involves research, pair programming is pretty difficult, but we could make an effort. 
