# GRADE: 96/96 

## Demo - 6/6 points
* 4/4 Each new feature was justified and demonstrated.
* 1/1 An original composition was played.
* 1/1 All team members were included.

## Original composition - 6/6 points
* 3/3 The composition demonstrates the features of your software. - _Definitely._
* 2/3 The composition is artistic. - _It's creative. I like that you slipped in Happy Birthday!_

## User manual - 10/10 points
* 2/2 There is a file named manual.md in the top-level directory.
* 4/4 It describes each new feature added in Project 9.
* 4/4 It explains how to use each new feature.

## Feature selection - 16/16 points
* _There are several new features which are largely unconnected to each other. Most are fairly straightforward extensions, but you also learned some new things about MIDI sequences and JavaFX._
* _Drag-and-draw notes is a straightforward feature, but also a unique one._
* _Adding a default note duration is straightforward. (Several groups did this.) The separator takes up valuable space._
* _Changing the background color is easy. This should be in a **View** menu rather than taking up space in the left sidebar._
* _Displaying note properties in the left sidebar is fairly straightforward as well. It might fit better in another location. _
* _Beats are algorithmically straightforward and use existing facilities for programmatically adding notes to a composition. I'm not sure of the value of saving and inserting beats, versus copying and pasting gestures._
* _Adding right-click context menus required you to learn something new._
* _Pausing and scrubbing is a somewhat complex feature which also required you to learn new things. It is awesome that you can drag the read line. It would be nice if there were a standard play control panel with play, pause, stop, fast-forward, and rewind buttons. _

## Execution -  34/38 points

### Implementation of new features - 21/24 points
* All new features behave as described in the user manual.  If the description in the user manual is incomplete, then the behavior follows standard conventions and is otherwise unsurprising.
    * _The lack of markings on the note length slider makes it hard to use._
    * _It is surprising that the note properties do not change or clear when multiple notes are selected._
* There are no uncaught exceptions from new features.
* There is no debugging output from new features.

### No regressions - 11/12 points
* All prior requirements are met, unless they have been superseded by new requirements or are documented by the team as known bugs for this iteration.
    * _-1 **Open** should not clear the composition pane before the new composition is open. Opening might fail, or the user might click cancel!**

### Release tag - 2/2 points
* The release is tagged as project9-release.

## Reflection and elegance - 20/20 points

* 4/4 UML diagram is accurate and complete.
* 2/2 Design overview addresses changes from Project 8 in general.
* 4/4 Design overview addresses implementation of each new feature.
* 4/4 Assessment of what is elegant and what is not thoughtfully addresses object-oriented design principles.
* 2/2 Velocity is presented. 
* 1/1 Team retrospective is presented.
* 3/3 New classes/methods are reasonably self-explanatory.
