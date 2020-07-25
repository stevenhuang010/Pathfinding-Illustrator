import java.util.ArrayList;


public interface PathfindingModelInterface {
	boolean isInBounds(int row, int col);
	int getStartNodeRow();
	int getStartNodeCol();
	int getEndNodeRow();
	int getEndNodeCol();
	int getNumRows();
	int getNumCols();
	boolean endNodeFound();
	boolean hasBeenTraveled(int row, int col);
	boolean isWall(int row, int col);
	void changeToTraveled(int row, int col, int tempDistance);
	void initializeGrid(int rows, int cols);
	ArrayList<Integer> getNeighborCoordinates(int row, int col);
	void setStartNode(int row, int col);
	void setEndNode(int row, int col);
	boolean startNodeSet();
	boolean endNodeSet();
	boolean isStartNode(int row, int col);
	boolean isEndNode(int row, int col);
	void setWall(int row, int col);
	void clearWall(int row, int col);
	void printArray();
	int getDistanceAt(int row, int col);
	void setDistanceAt(int row, int col, int distance);
	void setVisited(int row, int col);
}
