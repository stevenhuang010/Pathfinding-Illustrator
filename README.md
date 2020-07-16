# Pathfinding-Project
This is a tool that helps users understand and picture various pathfinding and maze generation algorithms by illustrating these algorithms step-by-step. 

Implemented algorithms include:
- Dijkstra's Algorithm
- A* Search
- Breadth-First Search 
- Depth-First Search
- Bidirectional BFS
- Prim's Maze Generation Algorithm
- Recursive Division Maze Generation Algorithm.

## Basic Controls
- The light pink circle represents the start node and the light blue circle represents the end node. These nodes can be dragged and dropped at different locations on the grid. Wall nodes are the squares shaded in a dark-purple color, and these nodes are impassable for pathfinding algorithms.

<p align = "center">
  <img src = "https://user-images.githubusercontent.com/63945057/87722510-12dc1f80-c76d-11ea-9d89-3d1c06905fca.png">
</p>       
                                                                                                                   
- When a pathfinding algorithm is executed (located at the top of the application), the square-shaped grid nodes that the algorithm has visited will change to a light-purple color to showcase how the algorithm works, while the unvisited nodes remain white. Since wall nodes are impassable, they will remain dark-purple during the algorithm. If a path from the start node to the end node exists, a light blue path will be drawn at the conclusion of the algorithm. Otherwise, no path will be drawn.

- The user is also able to add walls or erase walls by checking the Draw Wall checkbox or Erase Wall checkbox, respectively, and dragging their mouse across the grid.

<p align = "center">
  <img src = "https://user-images.githubusercontent.com/63945057/87724448-58e6b280-c770-11ea-93b9-3d0485f45656.gif">
</p>

- After a pathfinding algorithm concludes, the user can drag the start or end node to any place on the grid and the pathfinding algorithm will update in real time, continuing to show the shortest path as the user drags the circular node.

<p align="center">
  <img src="https://user-images.githubusercontent.com/63945057/87723357-90ecf600-c76e-11ea-83fb-fa8fb96d596a.gif">
</p>

- When a maze generation algorithm is executed, dark-purple walls will be added, so that pathfinding algorithms must go around them when looking for a valid path.

<p align="center">
  <img src="https://user-images.githubusercontent.com/63945057/87723092-1fad4300-c76e-11ea-8dca-6d277a7240e7.gif">
</p>


## Other Features
The folowing extra features are included at the bottom of the application.

- <p> About PathFinder - Opens a new stage with information about this application </p>
- <p> Clear Board - Clears all animations and walls </p>
- <p> Speed Slider - Changes the animation speed </p>
- <p> Draw Wall - If this box is checked, the user can draw walls on the board by dragging the mouse across the grid </p>
- <p> Erase Wall - If this box is checked, the user can erase walls on the board by dragging the mouse across the grid </p>
- <p> Show Steps - If this box is checked, the animation for the algorithm will be showed; otherwise, only the final product will be visible </p>
- <p> Finish Algorithm - If an algorithm is occurring, this button can be pressed to immediately finish it and show the final product </p>
