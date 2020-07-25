import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.PriorityQueue;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class AStar extends TimerClass {

	final int EDGE_WEIGHT = 1;
	
	ArrayList<Rectangle> visitedFill;
	PriorityQueue<Rectangle> priorityQueue;
	PathfindingModel fScoreModel;
	
	public AStar(PathfindingModel m, PathfindingGUI g) {
		super(m, g);
	}
	
	//G Score - Like Dijkstra's
	//H Score - Heuristic Distance
	//F Score - G Score + H Score
	//Priority Queue operates based on lowest F Score

	public void aStar() {
		//Normal initialization
		algoStart();
		showSteps = gui.showSteps.isSelected();
		if (!gui.getHasBeenRefreshed()) {
			showSteps = false;
		}
		visitedFill = new ArrayList<Rectangle>();
		priorityQueue = new PriorityQueue<Rectangle>(new FScoreRectangleComparator());
		previousNode = new Hashtable<Rectangle, Rectangle>();
		
		
		int currRow = model.getStartNodeRow();
		int currCol = model.getStartNodeCol();
		Rectangle startNode = gui.getRectangle(currRow, currCol);
		startNode.setFill(visitedColor);
		Rectangle curr = startNode;
		
		//Hashtable initialization
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				if (!model.isStartNode(row, col)) {
					previousNode.put(gui.getRectangle(row, col), curr);
				}
			}
		}
		
		fScoreModel = new PathfindingModel();
		//Initializes all fScore to infinity (doesn't need to account for walls because the only fScore values that will be accessed are the ones 
		//where the gScore values have no walls
		fScoreModel.initializeGrid(model.getNumRows(), model.getNumCols());
		//Updates endNode of fScoreModel
		fScoreModel.setEndNode(model.getEndNodeRow(), model.getEndNodeCol());
		//Sets fScore startNode equal to its heuristic
		fScoreModel.setDistanceAt(model.getStartNodeRow(), model.getStartNodeCol(), calculateManhattanDistanceHeuristic(currRow, currCol));
		while (curr != null && !model.isEndNode(currRow, currCol)) {
			ArrayList<Integer> neighborCoordinates = model.getNeighborCoordinates(currRow, currCol);
			int currGScoreDistance = model.getDistanceAt(currRow, currCol);
			for (int i = 0; i < neighborCoordinates.size(); i += 2) {
				int x = neighborCoordinates.get(i);
				int y = neighborCoordinates.get(i + 1);
				if (!model.hasBeenTraveled(x, y) && !model.isWall(x, y)) {
					Rectangle neighbor = gui.getRectangle(x, y);
					int tentativeGScore = currGScoreDistance + EDGE_WEIGHT;
					if (tentativeGScore < model.getDistanceAt(x, y)) {
						//hashtable updates
						previousNode.put(neighbor, curr);
						//models updates
						model.setDistanceAt(x, y, tentativeGScore);
						int fScore = tentativeGScore + calculateManhattanDistanceHeuristic(x, y);
						fScoreModel.setDistanceAt(x, y, fScore);
						//priority queue updates
						if (!priorityQueue.contains(neighbor)) {
							priorityQueue.add(neighbor);
						}
					}
				}
			}
			model.setVisited(currRow, currCol);
			curr = priorityQueue.poll();
			if (curr != null) {
				addToListOrFillNow(curr);
				int[] rowCol = gui.getRowCol(curr);
				currRow = rowCol[0];
				currCol = rowCol[1];
			}
		}
		if (model.isEndNode(currRow, currCol)) {
			findPath(startNode);
		}
		if (showSteps) {
			this.start();
		} else {
			algoFinished();
		}
	}
	
	public void addToListOrFillNow(Rectangle curr) {
		if (!showSteps) {
			curr.setFill(visitedColor);
		} else {
			visitedFill.add(curr);
		}
	}
	
	public int calculateManhattanDistanceHeuristic(int row, int col) {
		return Math.abs(row - model.getEndNodeRow()) + Math.abs(col - model.getEndNodeCol());
	}
	
	@Override
	public void handle(long now) {
		if (gui.hasAlgoFinishBeenPressed()) {
			//This part quickly finishes the algorithm
			for (int i = 0; i < visitedFill.size(); i++) {
				visitedFill.get(i).setFill(visitedColor);
			}
			while (!shortestPath.isEmpty()) {
				shortestPath.pop().setFill(pathColor);
			}
			gui.setAlgoFinishPressed(false);
			algoFinished();
			this.stop();
		} else if (expandingAnimationFinished() && delayExceeded(now)) {
			//This part draws the path
			if (shortestPath == null || shortestPath.isEmpty()) {
				this.stop();
				algoFinished();
				return;
			}
			shortestPath.pop().setFill(pathColor);
			previousTime = now;
		} else if (delayExceeded(now)) {
			//This part fills the visited nodes
			if (visitedFill.size() != 0) {
				visitedFill.remove(0).setFill(visitedColor);
			}
			previousTime = now;
		}
	}
	
	public boolean expandingAnimationFinished() {
		return visitedFill.size() == 0 ? true : false;
	}
	
	//Uses fScoreModel instead of normal model in TimerClass
	private class FScoreRectangleComparator implements Comparator<Rectangle> {

		@Override
		public int compare(Rectangle o1, Rectangle o2) {
			int row1 = GridPane.getRowIndex(o1);
			int col1 = GridPane.getColumnIndex(o1);
			int row2 = GridPane.getRowIndex(o2);
			int col2 = GridPane.getColumnIndex(o2);
			if (fScoreModel.getDistanceAt(row1, col1) < fScoreModel.getDistanceAt(row2, col2)) {
				return -1;
			} else if (fScoreModel.getDistanceAt(row1, col1) > fScoreModel.getDistanceAt(row2, col2)) {
				return 1;
			}
			return 0;
		}
		
	}

}
