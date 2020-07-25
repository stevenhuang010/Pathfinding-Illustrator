import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PrimsMazeGeneration extends TimerClass {

	ArrayList<Rectangle> mazeAnimationFill;
	ArrayList<Rectangle> potentialNeighbors;
	Random r;
	public PrimsMazeGeneration(PathfindingModel m, PathfindingGUI g) {
		super(m, g);
	}

	public void primsAlgorithm() {
		algoStart();
		//Changes the GUI and model into all walls
		makeGUIModelAllWall();
		r = new Random();
		mazeAnimationFill = new ArrayList<Rectangle>();
		showSteps = gui.showSteps.isSelected();
		if (!gui.getHasBeenRefreshed()) {
			showSteps = false;
		}
		previousNode = new Hashtable<Rectangle, Rectangle>();
		potentialNeighbors = new ArrayList<Rectangle>();
		//Starting on an odd row and col
		int currRow = randomlyGenerateOddNumber(model.getNumRows() - 2);
		int currCol = randomlyGenerateOddNumber(model.getNumCols() - 2);
		Rectangle curr = gui.getRectangle(currRow, currCol);
		model.setDistanceAt(currRow, currCol, model.NO_WALL);
		
		//Adds potential neighbors of current cell
		addPotentialNeighbors(curr, currRow, currCol);
		addToListOrFillNow(curr);
		while (!potentialNeighbors.isEmpty()) {
			//Choose random neighbor
			curr = chooseRandomNeighbor(potentialNeighbors);
			potentialNeighbors.remove(curr);
			
			currRow = GridPane.getRowIndex(curr);
			currCol = GridPane.getColumnIndex(curr);
			
			//Carve out no wall in the random neighbor
			model.setDistanceAt(currRow, currCol, model.NO_WALL);
			
			//Clears the passage in between the random neighbor and the original cell
			clearPassage(curr, currRow, currCol);
			addPotentialNeighbors(curr, currRow, currCol);
			addToListOrFillNow(curr);
		}
		removeWallOnStartNode();
		removeWallOnEndNode();
		clearSecondToLastRow();
		clearSecondToLastCol();
		if (showSteps) {
			this.start();
		} else {
			gui.resetToggleButtons();
			gui.setCurrentAlgorithm(null);
			algoFinished();
		}
	}
	
	public void addToListOrFillNow(Rectangle curr) {
		if (showSteps) {
			mazeAnimationFill.add(curr);
		} else {
			curr.setFill(gui.NO_WALL_COLOR);
		}
	}
	
	public void removeWallOnStartNode() {
		int startNodeRow = model.getStartNodeRow();
		int startNodeCol = model.getStartNodeCol();
		if (model.getDistanceAt(startNodeRow, startNodeCol) == model.WALL) {
			model.setDistanceAt(startNodeRow, startNodeCol, model.NO_WALL);
			Rectangle startNode = gui.getRectangle(startNodeRow, startNodeCol);
			if (!showSteps) {
				startNode.setFill(gui.NO_WALL_COLOR);
			} else if (!mazeAnimationFill.contains(startNode)) {
				mazeAnimationFill.add(startNode);
			}
		}
	}
	
	public void removeWallOnEndNode() {
		int endNodeRow = model.getEndNodeRow();
		int endNodeCol = model.getEndNodeCol();
		if (model.getDistanceAt(endNodeRow, endNodeCol) == model.WALL) {
			model.setDistanceAt(endNodeRow, endNodeCol, model.NO_WALL);
			Rectangle endNode = gui.getRectangle(endNodeRow, endNodeCol);
			if (!showSteps) {
				endNode.setFill(gui.NO_WALL_COLOR);
			} else if (!mazeAnimationFill.contains(endNode)) {
				mazeAnimationFill.add(endNode);
			}
		}
	}
	@Override
	public void handle(long now) {
		if (gui.hasAlgoFinishBeenPressed()) {
			for (int i = 0; i < mazeAnimationFill.size(); i++) {
				mazeAnimationFill.get(i).setFill(gui.NO_WALL_COLOR);
			}
			gui.setAlgoFinishPressed(false);
			algoFinished();
			gui.resetToggleButtons();
			gui.setCurrentAlgorithm(null);
			this.stop();
		} else if (delayExceeded(now)) {
			if (mazeAnimationFill.size() != 0) {
				mazeAnimationFill.remove(0).setFill(gui.NO_WALL_COLOR);
			} else {
				algoFinished();
				gui.resetToggleButtons();
				gui.setCurrentAlgorithm(null);
				this.stop();
			}
			previousTime = now;
		}
	}
	
	//Potential neighbors are up down left right 2 cells away
	public void addPotentialNeighbors(Rectangle current, int row, int col) {
		int upRow = row - 2;
		int bottomRow = row + 2;
		int leftCol = col - 2;
		int rightCol = col + 2;
		//up
		addIfPotentialNeighbor(current, upRow, col);
		//down
		addIfPotentialNeighbor(current, bottomRow, col);
		//left
		addIfPotentialNeighbor(current, row, leftCol);
		//right
		addIfPotentialNeighbor(current, row, rightCol);
	}
	
	public void addIfPotentialNeighbor(Rectangle current, int row, int col) {
		if (isInBounds(row, col) && model.getDistanceAt(row, col) == model.WALL) {
			Rectangle curr = gui.getRectangle(row, col);
			previousNode.put(curr, current);
			if (!potentialNeighbors.contains(curr)) {
				potentialNeighbors.add(curr);
			}
		}
	}
	public boolean isInBounds(int row, int col) {
		return (row >= 1 && row <= model.getNumRows() - 2 && col >= 1 && col <= model.getNumCols() - 2) ? true : false;
	}
	
	public int randomlyGenerateOddNumber(int maxBound) {
		int returnInt = r.nextInt(maxBound);
		if (returnInt % 2 == 0) {
			returnInt++;
		}
		return returnInt;
	}
	
	private void clearSecondToLastCol() {
		int numCols = model.getNumCols();
		int numRows = model.getNumRows();
		for (int i = 1; i < numRows - 1; i++) {
			if (model.getDistanceAt(i, numCols - 3) == model.NO_WALL) {
				model.setDistanceAt(i, numCols - 2, model.NO_WALL);
				Rectangle rect = gui.getRectangle(i, numCols - 2);
				addToListOrFillNow(rect);
			}
		}
	}

	private void clearSecondToLastRow() {
		int numCols = model.getNumCols();
		int numRows = model.getNumRows();
		for (int i = 1; i < numCols - 1; i++) {
			if (model.getDistanceAt(numRows - 3, i) == model.NO_WALL) {
				model.setDistanceAt(numRows - 2, i, model.NO_WALL);
				Rectangle rect = gui.getRectangle(numRows - 2, i);
				addToListOrFillNow(rect);
			}
		}
	}

	public void clearPassage(Rectangle curr, int row, int col) {
		Rectangle previous = previousNode.get(curr);
		int previousRow = GridPane.getRowIndex(previous);
		int previousCol = GridPane.getColumnIndex(previous);
		Rectangle middle = curr;
		if (row == previousRow) {
			//indicates new rectangle is on the left of old rectangle
			//Need to add 2 to the new column to get to the old column
			if (col + 2 == previousCol) {
				//clear passage between old and new
				middle = gui.getRectangle(row, col + 1);
				model.setDistanceAt(row, col + 1, model.NO_WALL);
			} else {
				middle = gui.getRectangle(row, col - 1);
				model.setDistanceAt(row, col - 1, model.NO_WALL);
			}
		} else {
			//indicates new rectangle is above old rectangle
			//Need to add 2 to the new row to get to the old row
			if (row + 2 == previousRow) {
				//clear passage between old and new
				middle = gui.getRectangle(row + 1, col);
				model.setDistanceAt(row + 1, col, model.NO_WALL);
			} else {
				middle = gui.getRectangle(row - 1, col);
				model.setDistanceAt(row - 1, col, model.NO_WALL);
			}
		}
		addToListOrFillNow(middle);
	}
	
	public Rectangle chooseRandomNeighbor(ArrayList<Rectangle> neighbors) {
		return neighbors.get(r.nextInt(neighbors.size()));
	}
	
	public void makeGUIModelAllWall() {
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				model.setDistanceAt(row, col, model.WALL);
				gui.getRectangle(row, col).setFill(gui.WALL_COLOR);
			}
		}
	}
}
