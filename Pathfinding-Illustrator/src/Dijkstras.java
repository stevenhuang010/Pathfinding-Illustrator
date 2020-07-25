import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Stack;
import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Dijkstras extends TimerClass {

	public final int EDGE_WEIGHT = 1;
	
	
	ArrayList<Rectangle> visitedFill;
	public Dijkstras(PathfindingModel m, PathfindingGUI g) {
		super(m, g);
	}
	
	public void dijkstrasAlgorithm() {
		//Normal initialization
		algoStart();
		visitedFill = new ArrayList<Rectangle>();
		showSteps = gui.showSteps.isSelected();
		PriorityQueue<Rectangle> priorityQueue = new PriorityQueue<Rectangle>(new RectangleComparator(model));
		previousNode = new Hashtable<Rectangle, Rectangle>();
		//If the GUI needs to be updated in real-time, showSteps will be false
		if (!gui.getHasBeenRefreshed()) {
			showSteps = false;
		}
		
		int currRow = model.getStartNodeRow();
		int currCol = model.getStartNodeCol();
		Rectangle startNode = gui.getRectangle(currRow, currCol);
		startNode.setFill(visitedColor);
		Rectangle curr = startNode;
		
		//Initializes HashTable
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				if (!model.isStartNode(row, col)) {
					previousNode.put(gui.getRectangle(row, col), curr);
				}
			}
		}
		while (curr != null && !model.isEndNode(currRow, currCol)) {
			ArrayList<Integer> neighborCoordinates = model.getNeighborCoordinates(currRow, currCol);
			int currAssignedDistance = model.getDistanceAt(currRow, currCol);
			for (int i = 0; i < neighborCoordinates.size(); i += 2) {
				int x = neighborCoordinates.get(i);
				int y = neighborCoordinates.get(i + 1);
				Rectangle neighbor = gui.getRectangle(x, y);
				int tentativeAssignedValue = currAssignedDistance + EDGE_WEIGHT;
				if (!model.hasBeenTraveled(x, y) && !model.isWall(x, y) && tentativeAssignedValue < model.getDistanceAt(x, y)) {
					//Model update
					model.setDistanceAt(x, y, tentativeAssignedValue);
					//hashtable update
					previousNode.put(neighbor, curr);
					//priority queue update
					if (!priorityQueue.contains(neighbor)) {
						priorityQueue.add(neighbor);
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
	@Override
	public void handle(long now) {
		if (gui.hasAlgoFinishBeenPressed()) {
			//Quickly finish
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
			//drawing path
			if (shortestPath == null || shortestPath.isEmpty()) {
				this.stop();
				algoFinished();
				return;
			}
			shortestPath.pop().setFill(pathColor);
			previousTime = now;
		} else if (delayExceeded(now)) {
			//filling neighbors
			if (visitedFill.size() != 0) {
				visitedFill.remove(0).setFill(visitedColor);
			}
			previousTime = now;
		}
		
	}

	public boolean expandingAnimationFinished() {
		return visitedFill.size() == 0 ? true : false;
	}
}
