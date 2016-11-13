#XP core value : Respect#
>Out of the XP core values, I have selected “Respect” and I will make sure this value is thoroughly followed during the entire project lifecycle.

This week we worked on making our game multiplayer.
During our sprint planning meeting discussed few important topics like how to handle multiplayer, how to decide winner, on what basis score will be generated and so on.

Each team member contributed to the discussion and we made some important decisions regarding multiplayer mode within game.
During our sprint planning we broke down required work into achievable tasks/stories and assigned them to team.

We created a Node.JS backend application that would handle creation of multiplayer room/lobby, deciding winner among players, getting rank of player after game is over and also displaying leader board.

Every players score is stored in MySql database for generating leader board and player rank.
We hosted our backend application on amazon EC2 server and access the RESTful api's in our greenfoot game.
We tested our application rigorously to find bugs and solved them.
