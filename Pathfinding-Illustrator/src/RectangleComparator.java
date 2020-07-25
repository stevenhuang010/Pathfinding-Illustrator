import java.util.Comparator;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class RectangleComparator implements Comparator<Rectangle> {

	PathfindingModel model;
	
	public RectangleComparator(PathfindingModel m) {
		model = m;
	}
	
	//-1 for o1 < o2
	//1 for o1 > o2
	//0 for o1 = o2
	@Override
	public int compare(Rectangle o1, Rectangle o2) {
		int row1 = GridPane.getRowIndex(o1);
		int col1 = GridPane.getColumnIndex(o1);
		int row2 = GridPane.getRowIndex(o2);
		int col2 = GridPane.getColumnIndex(o2);
		if (model.getDistanceAt(row1, col1) < model.getDistanceAt(row2, col2)) {
			return -1;
		} else if (model.getDistanceAt(row1, col1) > model.getDistanceAt(row2, col2)) {
			return 1;
		}
		return 0;
	}

}
