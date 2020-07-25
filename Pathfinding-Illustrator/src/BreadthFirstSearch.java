import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BreadthFirstSearch extends TimerClass {
	ArrayList<Rectangle> visitedFill;
	
	public BreadthFirstSearch(PathfindingModel m, PathfindingGUI g) {
		super(m, g);
	}

	public void bfs() {
		//normal initialization
		algoStart();
		visitedFill = new ArrayList<Rectangle>();
		previousNode = new Hashtable<Rectangle, Rectangle>();
		showSteps = gui.showSteps.isSelected();
		if (!gui.getHasBeenRefreshed()) {
			showSteps = false;
		}
		//Can use queue instead
		ArrayList<Rectangle> nodeList = new ArrayList<Rectangle>();
		int currRow = model.getStartNodeRow();
		int currCol = model.getStartNodeCol();
		Rectangle endNode = gui.getRectangle(model.getEndNodeRow(), model.getEndNodeCol());
		Rectangle startNode = gui.getRectangle(currRow, currCol);
		startNode.setFill(visitedColor);
		Rectangle curr = startNode;
		
		//hashtable initialization
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				if (!model.isStartNode(row, col)) {
					previousNode.put(gui.getRectangle(row, col), curr);
				}
			}
		}
		while (curr != null && curr != endNode) {
			ArrayList<Integer> neighborCoordinates = model.getNeighborCoordinates(currRow, currCol);
			for (int i = 0; i < neighborCoordinates.size(); i += 2 ) {
				int x = neighborCoordinates.get(i);
				int y = neighborCoordinates.get(i + 1);
				Rectangle neighbor = gui.getRectangle(x, y);
				if (!nodeList.contains(neighbor) && !model.isWall(x, y) && model.getDistanceAt(x, y) != model.VISITED) {
					previousNode.put(neighbor, curr);
					nodeList.add(neighbor);
				}
			}
			model.setVisited(currRow, currCol);
			if (nodeList.isEmpty()) {
				curr = null;
			} else {
				curr = nodeList.remove(0);
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
				this.stop();
				algoFinished();
				return;
			}
			shortestPath.pop().setFill(pathColor);
			previousTime = now;
		} else if (delayExceeded(now)) {
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
