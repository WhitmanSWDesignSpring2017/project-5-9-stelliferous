# GRADE: 61.5/64 

_Well done!_

## Functional and implementation requirements: 43/44 points 

### Menu: 6/6 points
* 2/2 Add to the **File** menu: **About...**, **New**, **Open...**, **Save**, and **Save as...**. Use standard shortcuts: Shortcut-N, Shortcut-O, Shortcut-S, and Shortcut-Shift-S.  **About** need not have a shortcut.
* 2/2 There should be a separator between the **About...** menu item and the rest of the **File** menu items and there should be a separator between the **Exit** menu item (placed at the bottom of the menu) and the rest of the menu items.
* 2/2 Each of the five new menu items should be disabled if their action is not appropriate to the current application state. In particular, the **Save** menu item should be disabled if the composition has not been edited since the previous save.

### Dialogs: 19/18 points
* 3/3 When the **About...** menu item is selected, a dialog box appears whose text includes the authors of the software.  The dialog should have only a **Close** or **OK** button.
* 1/2 When the **Save as...** menu item is chosen, a dialog appears in which the user is asked for the name of the file into which the sounds in the composition panel are to be saved. If the user enters a legal name and presses the **OK** button in the dialog, then the file should be saved.  If the user presses the **Cancel** button in the dialog, then the dialog closes and no saving occurs.
    * _Use file chooser dialog for improved usability._
* 2/2 When the **Save** menu item is chosen for a composition that was not loaded from a file nor ever saved to a file, it behaves like the **Save as...** menu item. If the current composition was loaded from a file or previously saved to a file, then the composition is saved to that file. No dialog appears in that case.
* 4/4 If the **New** menu item is chosen and the current composition has already been saved to a file, then the composition panel is cleared as if the application had just started up. If the current composition has been changed since it was last saved to a file, a dialog appears asking whether you want to save the composition before closing it. There should be 3 options: **Yes**, **No**, **Cancel**. If the user chooses **No** from the dialog, the current composition is disposed of and the composition panel is cleared.  If the user chooses **Cancel**, the dialog just disappears. If the user chooses **Yes**, the behavior is the same as if the **Save** menu item was chosen.
* 3/3 If the **Open...** menu item is chosen, it behaves like the **New** menu item with regard to saving the current composition. Then, if the user hasn`t canceled, an dialog appears in which the user can select a file to open. If the user chooses a valid file, it is loaded into the composition pane. If the user cancels, the dialog disappears. If the user chooses an invalid file, an error message is displayed and the open dialog stays visible.
* 4/4 If the **Exit** menu item is chosen and there have been changes made to the composition since it was last saved, then a dialog appears asking the user whether she wants to save the composition before exiting. If the user chooses **No** from the dialog, then the program exits.  If the user chooses **Cancel**, then the exiting is canceled and the application stays running. If the user chooses **Yes**, then the application behaves as if the **Save** menu item was chosen, after which the application exits.
* 2/0 If the user clicks the window close button, the behavior should be the same as for the **Exit** menu item.

### Save/Open Implementation: 8/10 points
* 4/4 Saved files include complete information about all notes and gestures in plain text.
    * _You may want to choose another file extension more specific than .txt._
* 2/4 Opening a saved file restores all notes and gestures from the saved composition.
    * _BUG: When opening a saved composition, the composition pane is not cleared before adding notes from the saved composition, so that what appears is not an accurate representation of the saved file._
* 2/2 Starting a new composition or opening a file should cause all undo/redo state to be cleared. You cannot undo the saving or loading of a file or a new composition.

### No regressions - 8/8 points
* 8/8 All prior requirements are met, unless they have been superseded by new requirements or are documented by the team as known bugs for this iteration.

### Release tag - 2/2 points
* 2/2 The release is tagged as project8-release.

## Reflection and elegance - 18.5/20 points

* 4/4 UML diagram is accurate and complete.
* 2/2 Design overview addresses changes from Project 7 in general.
    * _For project 9, please focus on telling me what's new! Delete stuff I've already read (or save it in another file)._
* 1/1 Design overview explains which classes are responsible for saving and loading files, and how they interact with other classes. _- SaveActions, CompositionToFile_
* 0/1 Design overview addresses which classes are responsible for composition state management (Does the composition have a filename? Has it changed since it was created, opened, or last saved?), and how they interact with other classes.
* 6/6 Assessment of what is elegant and what is not thoughtfully addresses object-oriented design principles.
    * _I like your discussion of appropriate responsibilities and minimizing duplication._
    * _I also appreciate your discussion of the Law of Demeter. I think you are noting a tradeoff between following that law and reducing direct coupling. To reduce indirect coupling, you may also think about whether it is appropriate to add some delegation methods to your main controller, to expose only the necessary methods of its composed objects._
    * _I also appreciate your reflecitons on design pattern applications - very timely._
* 2/2 Velocity is presented. _:-)_
* 1/1 Team retrospective is presented.
* 2.5/3 New classes/methods are reasonably self-explanatory. _I looked at SaveActions and CompositionFileInteractions._
    * _CompositionFileInteractions is an awkward name. Could it be more intention-revealing?_
    * _Remember to always include internal documentation for the purpose/role of private fields._
    * _Methods are looking better. Although some methods are longer, I can't see a way around having a three-way conditional to handle Yes/No/Cancel._
    * _Appropriate use of catching an exception!_
