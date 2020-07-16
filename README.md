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

- When a pathfinding algorithm is executed (located at the top of the application), the square-shaped grid nodes that the algorithm has visited will change to a light-purple color to showcase how the algorithm works, while the unvisited nodes remain white. Since wall nodes are impassable, they will remain dark-purple during the algorithm. If a path from the start node to the end node exists, a light blue path will be drawn at the conclusion of the algorithm. Otherwise, no path will be drawn.

- After a pathfinding algorithm concludes, the user can drag the start or end node to any place on the grid and the pathfinding algorithm will update in real time, continuing to show the shortest path as the user drags the circular node.

- When a maze generation algorithm is executed, dark-purple walls will be added, so that pathfinding algorithms must go around them when looking for a valid path.

- The user is also able to add walls or erase walls by checking the Draw Wall checkbox or Erase Wall checkbox, respectively, and dragging their mouse across the grid.

<p align="center">
  <img src="https://user-images.githubusercontent.com/63945057/87717691-a90c4780-c765-11ea-8306-046310cedadc.png">
</p>

## Other Features
The folowing extra features are included at the bottom of the application.

- <p> Clear Board - Clears all animations and walls </p>
- <p> Speed Slider - Changes the animation speed </p>
- <p> Draw Wall - If this box is checked, the user can draw walls on the board by dragging the mouse across the grid </p>
- <p> Erase Wall - If this box is checked, the user can erase walls on the board by dragging the mouse across the grid </p>
- <p> Show Steps - If this box is checked, the animation for the algorithm will be showed; otherwise, only the final product will be visible </p>
- <p> Finish Algorithm - If an algorithm is occurring, this button can be pressed to immediately finish it and show the final product </p>
