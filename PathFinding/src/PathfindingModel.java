import java.util.ArrayList;

public class PathfindingModel implements PathfindingModelInterface{

	public final int WALL = -2;
	public final int VISITED = -3;
	public final int NO_WALL = Integer.MAX_VALUE;
	private int startNodeRow = -1;
	private int startNodeCol;
	private int endNodeRow = -1;
	private int endNodeCol;
	private final String NO_WALL_STRING = "o";
	
	int[][] distanceGrid;
	
	@Override
	public void initializeGrid(int rows, int cols) {
		distanceGrid = new int[rows][cols];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				distanceGrid[row][col] = NO_WALL;
			}
		}
	}
	
	@Override
	public boolean isInBounds(int row, int col) {
		return (row >= 0 && row < distanceGrid.length && col >= 0 && col < distanceGrid[0].length) ? true : false;
	}

	@Override
	public int getStartNodeRow() {
		return startNodeRow;
	}

	@Override
	public int getStartNodeCol() {
		return startNodeCol;
	}

	@Override
	public int getEndNodeRow() {
		return endNodeRow;
	}

	@Override
	public int getEndNodeCol() {
		return endNodeCol;
	}

	@Override
	public int getNumRows() {
		return distanceGrid.length;
	}

	@Override
	public int getNumCols() {
		return distanceGrid[0].length;
	}

	@Override
	public boolean endNodeFound() {
		return distanceGrid[endNodeRow][endNodeCol] == VISITED ? true : false;
	}

	//Works for algorithms that update neighbors
	@Override
	public boolean hasBeenTraveled(int row, int col) {
		return distanceGrid[row][col] != Integer.MAX_VALUE ? true : false;
	}

	@Override
	public void changeToTraveled(int row, int col, int tempDistance) {
		distanceGrid[row][col] = tempDistance;
	}

	@Override
	public ArrayList<Integer> getNeighborCoordinates(int row, int col) {
		ArrayList<Integer> coordinates = new ArrayList<Integer>();
		//left
		if (isInBounds(row, col - 1)) {
			coordinates.add(row);
			coordinates.add(col - 1);
		}
		//down
		if (isInBounds(row + 1, col)) {
			coordinates.add(row + 1);
			coordinates.add(col);
		}
		//right
		if (isInBounds(row, col + 1)) {
			coordinates.add(row);
			coordinates.add(col + 1);
		}
		//up
		if (isInBounds(row - 1, col)) {
			coordinates.add(row - 1);
			coordinates.add(col);
		}
		return coordinates;
	}

	@Override
	public void setStartNode(int row, int col) {
		startNodeRow = row;
		startNodeCol = col;
		distanceGrid[row][col] = 0;
	}

	@Override
	public void setEndNode(int row, int col) {
		endNodeRow = row;
		endNodeCol = col;
	}

	@Override
	public boolean startNodeSet() {
		return startNodeRow == -1 ? false : true;
	}

	@Override
	public boolean endNodeSet() {
		return endNodeRow == -1 ? false : true;
	}

	@Override
	public void setWall(int row, int col) {
		distanceGrid[row][col] = WALL;
	}

	@Override
	public void clearWall(int row, int col) {
		distanceGrid[row][col] = NO_WALL;
	}
	
	/*
	 * "e" for target node
	 * "0" for start node
	 * "o" for non-wall nodes
	 * "-2" for wall nodes
	 */
	@Override
	public void printArray() {
		System.out.printf("%4s", "");
		for (int col = 0; col < distanceGrid[0].length; col++) {
			System.out.printf("%4d", col);
		}
		System.out.println();
		for (int row = 0; row < distanceGrid.length; row++) {
			System.out.printf("%4d", row);
			for (int col = 0; col < distanceGrid[0].length; col++) {
				if (isEndNode(row, col)) {
					System.out.printf("%4s", "e");
				}else if (distanceGrid[row][col] == NO_WALL) {
					System.out.printf("%4s", NO_WALL_STRING);
				} else {
					System.out.printf("%4d", distanceGrid[row][col]);
				}
			}
			System.out.println();
		}
	}

	@Override
	public boolean isWall(int row, int col) {
		return distanceGrid[row][col] == WALL ? true: false;
	}

	@Override
	public boolean isStartNode(int row, int col) {
		return row == startNodeRow && col == startNodeCol ? true : false;
	}
	
	@Override
	public boolean isEndNode(int row, int col) {
		return row == endNodeRow && col == endNodeCol ? true : false;
	}
	
	@Override
	public int getDistanceAt(int row, int col) {
		return distanceGrid[row][col];
	}

	@Override
	public void setDistanceAt(int row, int col, int distance) {
		distanceGrid[row][col] = distance;
	}

	@Override
	public void setVisited(int row, int col) {
		distanceGrid[row][col] = VISITED;
	}

}
