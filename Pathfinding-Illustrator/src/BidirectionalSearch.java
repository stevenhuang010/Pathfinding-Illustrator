import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/*
 * A Bidirectional Breadth-First Search
 */
public class BidirectionalSearch extends TimerClass {
	PathfindingModel modelForEnd;
	Hashtable<Rectangle, Rectangle> previousNodeEnd;
	ArrayList<Rectangle> visitedStart;
	ArrayList<Rectangle> visitedEnd;
	boolean intersected;
	Rectangle intersectingRectangle;
	Stack<Rectangle> startToIntersect;
	Stack<Rectangle> endToIntersect;
	
	public BidirectionalSearch(PathfindingModel m, PathfindingGUI g) {
		super(m, g);
	}

	public void bidirectionalSearch() {
		algoStart();
		//Stores the visited nodes from going from start to end
		visitedStart = new ArrayList<Rectangle>();
		//Stores the visited nodes from going from end to start
		visitedEnd = new ArrayList<Rectangle>();
		showSteps = gui.showSteps.isSelected();
		if (!gui.getHasBeenRefreshed()) {
			showSteps = false;
		}
		//Replicates first model to second model
		initializeSecondModel();
		//Determines whether the visited nodes from visited start and visited end have intersected
		intersected = false;
		//Path from start to the intersection
		startToIntersect = new Stack<Rectangle>();
		//Path from end to the intersection
		endToIntersect = new Stack<Rectangle>();
		
		ArrayList<Rectangle> startNodeList = new ArrayList<Rectangle>();
		ArrayList<Rectangle> endNodeList = new ArrayList<Rectangle>();
		
		previousNode = new Hashtable<Rectangle, Rectangle>();
		previousNodeEnd = new Hashtable<Rectangle, Rectangle>();
		
		int currRowStart = model.getStartNodeRow();
		int currColStart = model.getStartNodeCol();
		int currColEnd = model.getEndNodeCol();
		int currRowEnd = model.getEndNodeRow();
		Rectangle endNode = gui.getRectangle(currRowEnd, currColEnd);
		Rectangle startNode = gui.getRectangle(currRowStart, currColStart);
		Rectangle currStart = startNode;
		Rectangle currEnd = endNode;
		//Initializes both hashtables
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				if (!model.isStartNode(row, col)) {
					previousNode.put(gui.getRectangle(row, col), startNode);
				}
				if (!model.isEndNode(row, col)) {
					previousNodeEnd.put(gui.getRectangle(row, col), endNode);
				}
			}
		}
		//Runs as long as one of them isn't null and they haven't intersected yet
		while ((currStart != null || currEnd != null) && !intersected) {
			if (currStart != null) {
				int[] startArr = bfsBody(currRowStart, currColStart, startNodeList, currStart, model, previousNode, visitedStart);
				currRowStart = startArr[0];
				currColStart = startArr[1];
				if (currRowStart == -1) {
					currStart = null;
				} else {
					currStart = gui.getRectangle(currRowStart, currColStart);
				}
			}
			if (currEnd != null && !intersected) {
				int[] endArr = bfsBody(currRowEnd, currColEnd, endNodeList, currEnd, modelForEnd, previousNodeEnd, visitedEnd);
				currRowEnd = endArr[0];
				currColEnd = endArr[1];
				if (currRowEnd == -1) {
					currEnd = null;
				} else {
					currEnd = gui.getRectangle(currRowEnd, currColEnd);
				}
			}
		}
		if (intersected) {
			findPath(startNode, endNode);
		}
		if (showSteps) {
			this.start();
		} else {
			algoFinished();
		}
	}
	
	public void initializeSecondModel() {
		modelForEnd = new PathfindingModel();
		modelForEnd.initializeGrid(model.getNumRows(), model.getNumCols());
		modelForEnd.setStartNode(model.getStartNodeRow(), model.getStartNodeCol());
		modelForEnd.setEndNode(model.getEndNodeRow(), model.getEndNodeCol());
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				modelForEnd.setDistanceAt(row, col, model.getDistanceAt(row, col));
			}
		}
	}
	
	public void addToListOrFillNow(Rectangle curr, ArrayList<Rectangle> list) {
		if (!showSteps) {
			curr.setFill(visitedColor);
		} else {
			list.add(curr);
		}
	}
	
	public void findPath(Rectangle startNode, Rectangle endNode) {
		//Works from the rectangle where the two visited node lists intersect back to start
		Rectangle currNode = intersectingRectangle;
		startToIntersect.push(currNode);
		while (previousNode.get(currNode) != null && previousNode.get(currNode) != startNode) {
			currNode = previousNode.get(currNode);
			startToIntersect.push(currNode);
		}
		startToIntersect.push(startNode);
		
		//Works from the rectangle where the two visited node lists intersect back to end
		currNode = intersectingRectangle;
		endToIntersect.push(currNode);
		while (previousNodeEnd.get(currNode) != null && previousNodeEnd.get(currNode) != endNode) {
			currNode = previousNodeEnd.get(currNode);
			endToIntersect.push(currNode);
		}
		endToIntersect.push(endNode);
		if (!showSteps) {
			drawPath();
		}
	}
	
	public void drawPath() {
		while (!startToIntersect.isEmpty()) {
			startToIntersect.pop().setFill(pathColor);
		}
		while (!endToIntersect.isEmpty()) {
			endToIntersect.pop().setFill(pathColor);
		}
	}
	
	public int[] bfsBody(int currRow, int currCol, ArrayList<Rectangle> nodeList, Rectangle curr, PathfindingModel m, Hashtable<Rectangle, Rectangle> prevNode, ArrayList<Rectangle> fillList) {
		int[] returnArr = new int[2];
		ArrayList<Integer> neighborCoordinates = m.getNeighborCoordinates(currRow, currCol);
		m.setVisited(currRow, currCol);
		if (visitedListsOverlapped(currRow, currCol)) {
			intersected = true;
			returnArr[0] = -1;
			returnArr[1] = -1;
			intersectingRectangle = gui.getRectangle(currRow, currCol);
			return returnArr;
		}
		addToListOrFillNow(gui.getRectangle(currRow, currCol), fillList);
		for (int i = 0; i < neighborCoordinates.size(); i += 2 ) {
			int x = neighborCoordinates.get(i);
			int y = neighborCoordinates.get(i + 1);
			Rectangle neighbor = gui.getRectangle(x, y);
			if (!nodeList.contains(neighbor) && !m.isWall(x, y) && m.getDistanceAt(x, y) != m.VISITED) {
				prevNode.put(neighbor, curr);
				nodeList.add(neighbor);
			}
		}
		if (nodeList.isEmpty()) {
			curr = null;
			returnArr[0] = -1;
			returnArr[1] = -1;
		} else {
			curr = nodeList.remove(0);
			returnArr[0] = GridPane.getRowIndex(curr);
			returnArr[1] = GridPane.getColumnIndex(curr);
		}
		return returnArr;
	}
	
	public boolean visitedListsOverlapped(int currRow, int currCol) {
		return model.getDistanceAt(currRow, currCol) == model.VISITED && modelForEnd.getDistanceAt(currRow, currCol) == model.VISITED;
	}
	@Override
	public void handle(long now) {
		if (gui.hasAlgoFinishBeenPressed()) {
			//Finishes algorithm quickly
			for (int i = 0; i < visitedStart.size(); i++) {
				visitedStart.get(i).setFill(visitedColor);
			}
			for (int i = 0; i < visitedEnd.size(); i++) {
				visitedEnd.remove(0).setFill(visitedColor);
			}
			while (!startToIntersect.isEmpty()) {
				startToIntersect.pop().setFill(pathColor);
			}
			while (!endToIntersect.isEmpty()) {
				endToIntersect.pop().setFill(pathColor);
			}
			gui.setAlgoFinishPressed(false);
			algoFinished();
			this.stop();
		} else if (expandingAnimationFinished() && delayExceeded(now)) {
			//Path from both sides
			if (shortestPath == null || startToIntersect.isEmpty() && endToIntersect.isEmpty()) {
				this.stop();
				algoFinished();
				return;
			}
			if (!startToIntersect.isEmpty()) {
				startToIntersect.pop().setFill(pathColor);
			}
			if (!endToIntersect.isEmpty()) {
				endToIntersect.pop().setFill(pathColor);
			}
			previousTime = now;
		} else if (delayExceeded(now)) {
			//Filling visited from both sides
			if (visitedStart.size() != 0) {
				visitedStart.remove(0).setFill(visitedColor);
			}
			if (visitedEnd.size() != 0) {
				visitedEnd.remove(0).setFill(visitedColor);
			}
			previousTime = now;
		}
	}

	public boolean expandingAnimationFinished() {
		//Need both visitedStart and visitedEnd to be empty for the animation to be finished
		return (visitedStart.size() == 0 && visitedEnd.size() == 0) ? true : false;
	}
}
