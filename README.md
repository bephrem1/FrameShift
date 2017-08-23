# Frame Shift
Android Game Utilizing Dynamic Shape Redraw and Frequent Conditional Checks<br />
By: Benyam Ephrem<br />
Date Created: Around 2015<br />

History: This is the 2nd game I ever created when I was in the 10th grade in high school. This was a similar project to "Tilt The Gaps" that I first made and in this game I really honed in my UI shaping skills and ensuring that the game adapted well to different device sizes and dpi's

Explanation: This game starts with the screen half green and half red (rectangles) and you tap the greem rectangle to begin playing. The game is sort of like Pong...the green and red sides alternate all the while black rectangles are opening and closing the zone where a player can tap. If a player does not hit the green fast enough and the line in the middle collides with a black rectangle (the red rectangle takes up all of the space) then the player loses. The black rectangles move very fast as the score goes up making the tap area shift rapidly causing a player to have to be on their toes and have fast reactions because the black rectangles go up and down...side to side...obscuring the tap zone possible causing a missed tap (black rectangle taps do nothing) and a game over.

It features single player mode, double player, and an unstarted multiplayer mode
_________________________________________________________________

Reflection: The UI on this game is not finished as this was when I fell off on programming and lost motivation for a while because I forgot the reason I started...to make cool things. But the UI is very clean and used nice fonts and the game over screen is very well formatted. This app/game also uses data persistence to record overall tap accuracy (a black rectangle is a "missed" tap and a green tap is a "made" tap) and accuracy percentages are derived from this historical data.

_________________________________________________________________

Use of Code and Testing: The code in this repository is simply the essential source code needed to replicate the game in another Android Studio Project. Simply port the code into Android Studio and you can run it within' the provided Android Studio framework (after editing naming issues with packages)

This was the second program I ever made primarily myself (despite consulting Stack Overflow often for small issues and bugs) and I actually like this more than Tilt The Gaps. Given a good platform and a real development team I think these would be excellent games with a "KetchApp type" feel, sleek and simple. I also didn't have a designer to help me make visual assets to make the game more visually appealing, but the game logic and the way the rectangles behave is beyond cool when seen in real time. It is like the game has a life of its own!


