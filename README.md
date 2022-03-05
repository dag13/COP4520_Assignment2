# COP4520_Assignment2
Problem 1: Minotaur’s Birthday Party (50 points)

To solve this problem I used similar logic to the N prisoners problem. When a guest finds a way out of the labyrinth, he or she has the following options:
If there is a cupcake:
(1) Eat it if they have not had one already
(2) If its the leader increase the count after eating
No cupcake:
(1) Leader increases count and requests new cupcake
(2) Others just leave it

To run the program:

javac MinotaurBirthdayParty.java

java MinotaurBirthdayParty

The program will ask for user input and will then run the scenario. It will print to the console after all guests have existed the labyrinth.
  
 
 
Problem 2: Minotaur’s Crystal Vase (50 points)

Out of the three scenarios, I believe the best choice is scenario three. This is scenario prevents the race for the door that could happen with options one and two. However, unlike the previous two choices guests in the third scenario are unable to do other things while not in the vase room. The reason for this is because they have to wait in a queue.

To implement the third scenario I use an array-based queue lock is from Chapter 7 of the textbook The Art of Multiprocessor Programming. Fig 7.7
Instead of implementing Lock the class extends an AbstractLock which I credit to github user javaf https://github.com/javaf/mcs-lock/blob/master/AbstractLock.java

To run the program:

javac MinotaurCrystalVase.java

java  MinotaurCrystalVase

The program will ask for user input and will then run the scenario. It will print to the console after the scenario is finished.
