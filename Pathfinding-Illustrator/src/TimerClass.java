import java.util.Hashtable;
import java.util.Stack;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


public abstract class TimerClass extends AnimationTimer {
	
	static double delay = 20_000_000;//from 10_000_000 to 500_000_000
	static long previousTime = 0;
	
	PathfindingModel model;
	PathfindingGUI gui;
	Color visitedColor = Color.rgb(234, 147, 245);
	Color pathColor = Color.rgb(209, 232, 222);
	static boolean algoOccurring;
	boolean showSteps;
	Hashtable<Rectangle, Rectangle> previousNode;
	Stack<Rectangle> shortestPath;
	
	public TimerClass(PathfindingModel m, PathfindingGUI g) {
		super();
		model = m;
		gui = g;
		algoOccurring = false;
		
	}
	public boolean delayExceeded(long now) {
		return now - previousTime >= delay ? true : false;
	}
	
	public double getDelay() {
		return delay;
	}
	
	public long getPreviousTime() {
		return previousTime;
	}
	
	public static void setDelay(double delay) {
		TimerClass.delay = delay;
	}
	
	public static void setPreviousTime(long previousTime) {
		TimerClass.previousTime = previousTime;
	}
	
	public static boolean isAlgoOccurring() {
		return algoOccurring;
	}	
		
	//Called at end of algorithm
	//resets grid listener, algoOccurring, clear board, algorithm buttons, sets no refreshed, refreshes showSteps
	public void algoFinished() {
		gui.enableGridListener();
		algoOccurring = false;
		gui.enableClearBoard();
		gui.enableAlgorithmButtons();
		gui.setHasBeenRefreshed(false);
		//reassigns show steps based on checkbox
		showSteps = gui.showSteps.isSelected();
	}
	
	//Called at the start of the algorithm
	public void algoStart() {
		gui.disableAlgorithmButtons();
		gui.disableClearBoard();
		gui.disableGridListener();
		shortestPath = new Stack<Rectangle>();
		algoOccurring = true;
	}
	
	public void setShowSteps(boolean b) {
		showSteps = b;
	}
	
	//Finds path from start to end node
	public void findPath(Rectangle startNode) {
		Rectangle currNode = gui.getRectangle(model.getEndNodeRow(), model.getEndNodeCol());
		shortestPath.push(currNode);
		while (null != previousNode.get(currNode) && previousNode.get(currNode) != startNode) {
			currNode = previousNode.get(currNode);
			shortestPath.push(currNode);
		}
		shortestPath.push(startNode);
		if (!showSteps) {
			while (!shortestPath.isEmpty()) {
				shortestPath.pop().setFill(pathColor);
			}
		}
	}
}
