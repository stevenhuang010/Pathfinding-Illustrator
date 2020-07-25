import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RecursiveMazeGeneration extends TimerClass {
	
	ArrayList<Rectangle> cannotAddWall;
	Random r;
	ArrayList<Rectangle> mazeAnimationFill;
	
	public RecursiveMazeGeneration(PathfindingModel m, PathfindingGUI g) {
		super(m, g);
	}

	
	public void recursiveMazeDivision() {
		algoStart();
		r = new Random();
		//Stores rectangles where walls cannot be added in order to have a successful maze
		cannotAddWall = new ArrayList<Rectangle>();
		mazeAnimationFill = new ArrayList<Rectangle>();
		//remove all current walls and animations
		gui.clearEverything();
		showSteps = gui.showSteps.isSelected();
		if (!gui.getHasBeenRefreshed()) {
			showSteps = false;
		}
		
		//Add border walls
		addWallsOnAllBorders();
		
		divide(1, model.getNumRows() - 2, 1, model.getNumCols() - 2, true);
		
		removeWallOnStartNode();
		removeWallOnEndNode();
		
		if (showSteps) {
			this.start();
		} else {
			//Must reset toggle buttons when maze finishes because there's no need to re-execute maze algorithm
			gui.resetToggleButtons();
			gui.setCurrentAlgorithm(null);
			algoFinished();
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
			} else {
				//Removes all animation fill rectangles even if there are duplicates
				while (mazeAnimationFill.contains(startNode)) {
					mazeAnimationFill.remove(startNode);
				}
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
			} else {
				while (mazeAnimationFill.contains(endNode)) {
					mazeAnimationFill.remove(endNode);
				}
			}
		}
	}
	
	public void divide(int start, int end, int upLeftBound, int bottomRightBound, boolean horizontalDivision) {
		if (end - start <= 1 || bottomRightBound - upLeftBound < 1) {
			return;
		}
		if (horizontalDivision) {
			//Fills a row while leaving out 1 random passage
			int rowFill = generateRandomNumber(start + 1, end - 1);
			fillRow(rowFill, upLeftBound, bottomRightBound);
			int upperForTopHalf = start;
			int bottomForTopHalf = rowFill - 1;
			int upperForBottomHalf = rowFill + 1;
			int bottomForBottomHalf = end;
			//Top Half
			if (isWiderThanTall(bottomForTopHalf, upperForTopHalf, bottomRightBound, upLeftBound)) {
				//vertical division
				divide(upLeftBound, bottomRightBound, upperForTopHalf, bottomForTopHalf, false);
			} else {
				//horizontal division
				divide(upperForTopHalf, bottomForTopHalf, upLeftBound, bottomRightBound, true);
			}
			//Bottom Half
			if (isWiderThanTall(bottomForBottomHalf, upperForBottomHalf, bottomRightBound, upLeftBound)) {
				//vertical division
				divide(upLeftBound, bottomRightBound, upperForBottomHalf, bottomForBottomHalf, false);
			} else {
				//horizontal division
				divide(upperForBottomHalf, bottomForBottomHalf, upLeftBound, bottomRightBound, true);
			}
		} else {
			int colFill = generateRandomNumber(start + 1, end - 1);
			fillCol(colFill, upLeftBound, bottomRightBound);
			int leftForLeftHalf = start;
			int rightForLeftHalf = colFill - 1;
			int leftForRightHalf = colFill + 1;
			int rightForRightHalf = end;
			//Left Half
			if (isTallerThanWide(rightForLeftHalf, leftForLeftHalf, bottomRightBound, upLeftBound)) {
				//horizontal division
				divide(upLeftBound, bottomRightBound, leftForLeftHalf, rightForLeftHalf, true);
			} else {
				//vertical division
				divide(leftForLeftHalf, rightForLeftHalf, upLeftBound, bottomRightBound, false);
			}
			//Right Half
			if (isTallerThanWide(rightForRightHalf, leftForRightHalf, bottomRightBound, upLeftBound)) {
				//horizontal division
				divide(upLeftBound, bottomRightBound, leftForRightHalf, rightForRightHalf, true);
			} else {
				//vertical division
				divide(leftForRightHalf, rightForRightHalf, upLeftBound, bottomRightBound, false);
			}
		}
		
	}
	
	public boolean isWiderThanTall(int bottom, int upper, int rightBound, int leftBound) {
		return (bottom - upper < rightBound - leftBound) ? true : false;
	}
	
	public boolean isTallerThanWide(int right, int left, int bottomBound, int upperBound) {
		return (right - left < bottomBound - upperBound) ? true : false;
	}
	
	public void fillRow(int row, int leftBound, int rightBound) {
		int clearCol = generateRandomNumber(leftBound, rightBound);
		for (int col = leftBound; col <= rightBound; col++) {
			//If you are allowed to add a wall here
			if (isWallLegal(row, col)) {
				//If you are not making a blank passage here
				if (col != clearCol) {
					model.setDistanceAt(row, col, model.WALL);
					addToListOrFillNow(row, col);
				} else {
					//Add top and bottom neighbors to rectangle arraylist that cannot become walls
					cannotAddWall.add(gui.getRectangle(row - 1, col));
					cannotAddWall.add(gui.getRectangle(row + 1, col));
				}
			} else {
				//If you are not allowed to add a wall here, this will become the "blank" passage of the wall
				//If another blank passage has been made
				if (passageAlreadyMade(col, clearCol)) {
					//re-fill the original blank passage
					model.setDistanceAt(row, clearCol, model.WALL);
					addToListOrFillNow(row, clearCol);
					//remove the top and bottom rectangles from the original blank passage
					cannotAddWall.remove(gui.getRectangle(row - 1, clearCol));
					cannotAddWall.remove(gui.getRectangle(row + 1, clearCol));
				} else {
					//If another blank passage hasn't been made yet, set the clear col to an invalid index number so that another passage will not be made
					clearCol = model.getNumCols();
				}
			}
		}
	}
	
	public void fillCol(int col, int upperBound, int bottomBound) {
		int clearRow = generateRandomNumber(upperBound, bottomBound);
		for (int row = upperBound; row <= bottomBound; row++) {
			if (isWallLegal(row, col)) {
				if (row != clearRow) {
					model.setDistanceAt(row, col, model.WALL);
					addToListOrFillNow(row, col);
				} else {
					cannotAddWall.add(gui.getRectangle(row, col - 1));
					cannotAddWall.add(gui.getRectangle(row, col + 1));
				}
			} else {
				if (passageAlreadyMade(row, clearRow)) {
					model.setDistanceAt(clearRow, col, model.WALL);
					addToListOrFillNow(clearRow, col);
					cannotAddWall.remove(gui.getRectangle(clearRow, col - 1));
					cannotAddWall.remove(gui.getRectangle(clearRow, col + 1));
				} else {
					clearRow = model.getNumRows();
				}
			}
		}
	}
	
	//If the current row/col is greater than the one that is supposed to be cleared, it implies that an empty passage has already been made
	public boolean passageAlreadyMade(int curr, int cleared) {
		return curr > cleared ? true : false;
	}
	
	public boolean isWallLegal(int row, int col) {
		return !cannotAddWall.contains(gui.getRectangle(row, col)) ? true : false;
	}
	
 	public void addToListOrFillNow(int row, int col) {
		if (!showSteps) {
			gui.getRectangle(row, col).setFill(gui.WALL_COLOR);
		} else {
			mazeAnimationFill.add(gui.getRectangle(row, col));
		}
	}
 	
 	//Generates a random number between lower and upper inclusive
 	//pass in 2, 2 -> (2 - 2 + 1) = r.nextInt(1) -> [0,0] + lower -> 0 + 2
	public int generateRandomNumber(int lower, int upper) {
		return lower + r.nextInt(1 + upper - lower);
	}

	public void addWallsOnAllBorders() {
		int numRows = model.getNumRows();
		int numCols = model.getNumCols();
		for (int i = 0; i < numRows; i++) {
			//Left side
			model.setDistanceAt(i, 0, model.WALL);
			addToListOrFillNow(i, 0);
			//Right side
			model.setDistanceAt(i, numCols - 1, model.WALL);
			addToListOrFillNow(i, numCols - 1);
		}
		for (int i = 1; i < numCols; i++) {
			//Top
			model.setDistanceAt(0, i, model.WALL);
			addToListOrFillNow(0, i);
			//Bottom
			model.setDistanceAt(numRows - 1, i, model.WALL);
			addToListOrFillNow(numRows - 1, i);
		}
	}

	@Override
	public void handle(long now) {
		if (gui.hasAlgoFinishBeenPressed()) {
			//Quickly finishes maze
			for (int i = 0; i < mazeAnimationFill.size(); i++) {
				mazeAnimationFill.get(i).setFill(gui.WALL_COLOR);
			}
			gui.setAlgoFinishPressed(false);
			algoFinished();
			gui.resetToggleButtons();
			gui.setCurrentAlgorithm(null);
			this.stop();
		} else if (delayExceeded(now)) {
			//Fills out maze
			if (mazeAnimationFill.size() != 0) {
				mazeAnimationFill.remove(0).setFill(gui.WALL_COLOR);
			} else {
				algoFinished();
				gui.resetToggleButtons();
				gui.setCurrentAlgorithm(null);
				this.stop();
			}
			previousTime = now;
		}
	}

}
