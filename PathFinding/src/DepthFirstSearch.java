import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import javafx.scene.shape.Rectangle;

public class DepthFirstSearch extends TimerClass {

	ArrayList<Rectangle> visitedFill;
	Stack<Rectangle> nodeStack;
	public DepthFirstSearch(PathfindingModel m, PathfindingGUI g) {
		super(m, g);
	}
	
	public void dfs() {
		//Normal initialization
		algoStart();
		visitedFill = new ArrayList<Rectangle>();
		previousNode = new Hashtable<Rectangle, Rectangle>();
		showSteps = gui.showSteps.isSelected();
		nodeStack = new Stack<Rectangle>();
		if (!gui.getHasBeenRefreshed()) {
			showSteps = false;
		}
		
		int currRow = model.getStartNodeRow();
		int currCol = model.getStartNodeCol();
		Rectangle endNode = gui.getRectangle(model.getEndNodeRow(), model.getEndNodeCol());
		Rectangle startNode = gui.getRectangle(currRow, currCol);
		startNode.setFill(visitedColor);
		Rectangle curr = startNode;
		nodeStack.push(curr);
		
		//Hashtable
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				if (!model.isStartNode(row, col)) {
					previousNode.put(gui.getRectangle(row, col), curr);
				}
			}
		}
		
		while (curr != null && curr != endNode) {
			if (curr == endNode) {
				break;
			}
			ArrayList<Integer> neighborCoordinates = model.getNeighborCoordinates(currRow, currCol);
			for (int i = 0; i < neighborCoordinates.size(); i += 2 ) {
				int x = neighborCoordinates.get(i);
				int y = neighborCoordinates.get(i + 1);
				Rectangle neighbor = gui.getRectangle(x, y);
				if (nodeStack.contains(neighbor)) {
					//Remove and re-add them so that they're at the beginning of the stack and won't cause row and col blanks
					nodeStack.remove(neighbor);
				}
				if (!model.isWall(x, y) && model.getDistanceAt(x, y) != model.VISITED) {
					previousNode.put(neighbor, curr);
					nodeStack.push(neighbor);
				}
			}
			model.setVisited(currRow, currCol);
			if (nodeStack.isEmpty()) {
				curr = null;
			} else {
				curr = nodeStack.pop();
				addToListOrFillNow(curr);
				int[] rowCol = gui.getRowCol(curr);
				currRow = rowCol[0];
				currCol = rowCol[1];
			}
		}
		if (curr == endNode) {
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
			if (shortestPath == null || shortestPath.isEmpty()) {
				//if no path or done filling in
				this.stop();
				algoFinished();
				return;
			}
			shortestPath.pop().setFill(pathColor);
			previousTime = now;
		} else if (delayExceeded(now)) {
			if (visitedFill.size() != 0) {
				visitedFill.remove(0).setFill(visitedColor);
				//fill transition
				 
			}
			previousTime = now;
		}
	}
	
	public boolean expandingAnimationFinished() {
		return visitedFill.size() == 0 ? true : false;
	}
}
