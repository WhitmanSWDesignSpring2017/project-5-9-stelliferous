>Give a concise overview of your design. How did you divide the code into classes and methods? How does your design for Project >5 differ from the design you inherited for Project 4? How did you respond to feedback?

Our design includes one large package, tunecomposer, with three smaller packages (Model, Control, View) that each contain their own files. Model simply contains the main tunecomposer java files, which initializes the program on the whole, bringing up the stage and setting up other files. The Control package contains files that depend on user interaction, and supplementary files. The MainController file coordinates the bulk of user interactivity, connecting all the other controllers to form one cohesive program and coordinate user actions and visual display. Interaction with notes has now been moved to the Composition file, which allows their creation, movement, and editing. That java file relies on the Constants files, which includes a list of constants, the Instrument file, which describes the information related to each type of instrument, and the NoteRectangle class, which defines the duration, location, and visual of created note rectangles. The Gesture class allows for the user's creation and deletion of gestures, along with the accompanying border around said gestures. The CompositionState class allows the user a concise way to store the state of the program and notes. The UndoRedoAction class does as its name implies, making Composition States available for undoing and redoing actions.Finally, the view package contains CSS for styling in the user interface, and two different FXML files to lay out the user interface and area for user interaction.

We also created some "stretch goals." They included (1) letting the user add "beats," or groupings of notes that provide a beat to support their composition (2) letting the user save and insert their own beats, essentially copying selected notes. Notes can then be pasted back as one gesture. (3) We now let the user "mark" a specific composition state that they can return to, rather than using "undo" many times.

>Explain why your way was the elegant way to do it. Address any improvements you made based on my feedback.

We split up different fxml and javaFX classes into different classes and controllers to meet the guideline of Single Responsibility Principle. We meet the Liskov Substitution Principle in that we don't really use subclassing, and favor composition over inheritance. Whenever possible, we made methods and fields private or protected to work towards the Open/Closed principle; greater encapsulation closes the program to modification. Simply breaking classes into smaller controllers allows for more extensibility and "openess." Because we don't use inheritance, our program cannot violate the Dependency Inversion principle or the Interface Segregation Principle. We included documentation for clarity. As always, we continue to refactor and decompose on a smaller scale. 

>Explain what, if anything, in your solution is inelegant and why you didn't make it elegant (for example, maybe you didn't >have time or the knowledge to fix it).

Our program does violate the Law of Demeter, in that many controllers go through the main controller to get something done. We wish we didn't have to write our own deep-clone information for undoing and redoing, but with the time we had couldn't find a simpler way to do it built into Java. (Since there wasn't much information to store in each Composition State, only three arrays, we chose to implement undo/redo by saving each state rather than storing the actions themselves). We hadn't received feedback by the time that we submitted the project. As always, with more time and knowledge, more refactoring could be done.

>Include an estimate of your velocity. How many story points did you estimate you would complete during this project? How many >did you actually complete, how many person-hours did the team spend, and what is the ratio of points/person-hour?

We estimated that we would complete 26 story points, and actually ended up completed about 23 (although we're not very experienced in gauging story points or story point estimation). We spent about 37 hours on this project, so around 12 hours per person, and that means that the rate of points/person-hour was about 0.62162162162.

>Include a short summary of your team retrospective. What went well that your team will keep doing during the next project >assignment? What will you improve? How?

We met on the earlier side and in-person, which really helped with communication and planning. We will keep doing that, as time allows. In the future, we may *also* work additional hours on our own. We would like to talk to you about the project and the choices we are making. Thank you,

Tyler, Jing, and Kaylin


