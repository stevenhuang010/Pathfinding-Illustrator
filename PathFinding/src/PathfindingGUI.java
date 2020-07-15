import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class PathfindingGUI extends Application {
	
	final int NUM_ROWS = 30;
	final int NUM_COLS = 50;
	final int RECT_WIDTH = 20;
	final double NODE_RADIUS = RECT_WIDTH / 2.0;
	final int NODE_OFFSET = 10;
	final int SCENE_WIDTH = NUM_COLS * RECT_WIDTH;
	final int SCENE_HEIGHT = 700;
	final int DEFAULT_START_NODE_ROW = NUM_ROWS / 2;
	final int DEFAULT_START_NODE_COL = NUM_COLS / 2 - NODE_OFFSET;
	final int DEFAULT_END_NODE_ROW = NUM_ROWS / 2;
	final int DEFAULT_END_NODE_COL = NUM_COLS / 2 + NODE_OFFSET;
	final Paint START_NODE_COLOR = Color.PINK;
	final Paint END_NODE_COLOR = Color.LIGHTSKYBLUE;
	final Paint TOP_BAR_COLOR = Color.WHITE;
	final Paint LEGEND_COLOR = Color.WHITE;
	final Paint WALL_COLOR = Color.rgb(124, 80, 128);
	final Paint ROOT_COLOR = Color.WHITE;
	final Paint NO_WALL_COLOR = Color.WHITE;
	final String DIJKSTRAS = "Dijkstra's";
	final String PRIMS_MAZE_GENERATION = "Prim's Maze Generation";
	final String RECURSIVE_MAZE_GENERATION = "Recursive Maze Generation";
	final String BFS = "Breadth-First Search";
	final String A_STAR = "A*";
	final String DFS = "Depth-First Search";
	final String BIDIRECTIONAL_SEARCH = "Bidirectional Search";
	
	Text pathfinder;
	PathfindingModel model;
	Rectangle[][] rectangles;
	GridPane view;
	HBox legend;
	CheckBox drawWall;
	CheckBox eraseWall;
	CheckBox showSteps;
	VBox root;
	HBox topBar;
	Circle startNode;
	Circle endNode;
	double nodeOriginalX;
	double nodeOriginalY;
	Slider speedSlider;
	ToggleButton dijkstrasButton;
	ToggleButton primsMazeButton;
	ToggleButton recursiveMazeButton;
	ToggleButton bfsButton;
	ToggleButton aStarButton;
	ToggleButton bidirectionalButton;
	ToggleButton dfsButton;
	Dijkstras dijkstrasInstance;
	PrimsMazeGeneration primsMazeInstance;
	BreadthFirstSearch bfsInstance;
	AStar aStarInstance;
	DepthFirstSearch dfsInstance;
	BidirectionalSearch bidirectionalInstance;
	RecursiveMazeGeneration recursiveMazeInstance;
	Button finishAlgorithm;
	boolean isAlgoFinishButtonPressed;
	boolean hasOtherAlgoBeenSelected;
	int[] coordinatesOfReplacedWall;
	Label sliderText;
	VBox sliderContainer;
	ArrayList<ToggleButton> toggleButtons;
	String currentAlgorithm;
	Button clearBoard;
	Button about;
	Stage instructionPage;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		stage.setTitle("PathFinder");
		model = new PathfindingModel();
		//Initializes model and 2D rectangle array
		model.initializeGrid(NUM_ROWS, NUM_COLS);
		rectangles = new Rectangle[NUM_ROWS][NUM_COLS];
		//VBOX
		initializeRoot();
		//Initializes algorithm toggle buttons
		initializeDijkstras();
		initializePrimsMazeGeneration();
		initializeBFS();
		initializeDFS();
		initializeAStar();
		initializeBidirectional();
		initializeRecursiveMazeGeneration();
		//Top HBOX
		initializeTopBar();
		//Creates Text
		pathfinder = new Text("PathFinder");
		pathfinder.getStyleClass().add("title");
		//Add's HBOX Children
		topBar.getChildren().addAll(pathfinder, dijkstrasButton, aStarButton, bfsButton, dfsButton, bidirectionalButton, primsMazeButton, recursiveMazeButton);
		//GridPane
		initializeGridPane();
		//Initiate Full-Press Drag When Grid Is Clicked On
		view.setOnDragDetected(new GridDragDetector());
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				Rectangle rect = new Rectangle();
				initializeRectAppearance(rect);
				//Rectangle Colored When Drag Entered
				rect.setOnMouseDragEntered(new RectDragClickHandler(rect));
				//Rectangle Colored When Clicked On
				rect.setOnMouseClicked(new RectDragClickHandler(rect));
				//Fills 2D Array of Rectangles
				rectangles[row][col] = rect;
				//Adds Rectangles to GridPane
				view.add(rect, col, row);
			}
		}
		//Update View With Start Node
		initializeStartNode();
		view.add(startNode, DEFAULT_START_NODE_COL, DEFAULT_START_NODE_ROW);
		//Update View with End Node
		initializeEndNode();
		view.add(endNode, DEFAULT_END_NODE_COL, DEFAULT_END_NODE_ROW);
		//Adds MousePressed Listener to StartNode and end node so that node is no longer dictated by gridpane when dragged around
		startNode.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				startNode.setManaged(false);
			}
			
		});
		endNode.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				endNode.setManaged(false);
			}
		});
		//Adds MouseDragged Listener to StartNode and EndNode
		startNode.setOnMouseDragged(new NodeDragHandler(startNode, "start"));
		endNode.setOnMouseDragged(new NodeDragHandler(endNode, "end"));
		//Adds MouseReleased Listener to StartNode and EndNode
		startNode.setOnMouseReleased(new RemoveNodeFocus(startNode, "start"));
		endNode.setOnMouseReleased(new RemoveNodeFocus(endNode, "end"));
		//Creates Bottom HBOX
		initalizeLegend();
		drawWall = new CheckBox("Draw Wall");
		eraseWall = new CheckBox("Erase Wall");
		//Adds Listeners to DrawWall and ClearWall to Switch If Other Pressed
		drawWall.selectedProperty().addListener(new BoxSelector(drawWall));
		eraseWall.selectedProperty().addListener(new BoxSelector(eraseWall));
		//Initialize ShowSteps CheckBox
		showSteps = new CheckBox("Show Steps");
		showSteps.setSelected(true);
		showSteps.selectedProperty().addListener(new ShowSteps());
		//Initialize SpeedSlider
		initializeSlider();
		//Adds Listener to SpeedSlider
		speedSlider.valueProperty().addListener(new SliderListener());
		//Creates Button that Allows Quick Finishing of Algorithm
		finishAlgorithm = new Button("Finish Algorithm");
		finishAlgorithm.setOnAction(new ButtonHandler());
		//Creates About Button
		about = new Button("About PathFinder");
		about.setOnAction(new AboutHandler());
		//Initialize label with slider text
		initializeSliderText();
		initializeSliderContainer();
		//Initialize clear board button
		clearBoard = new Button("Clear Board");
		clearBoard.setOnAction(new ButtonHandler());
		//Add slider container children
		sliderContainer.getChildren().addAll(speedSlider, sliderText);
		//Add HBOX's Children
		legend.getChildren().addAll(about, clearBoard, sliderContainer, drawWall, eraseWall, showSteps, finishAlgorithm);
		//Add TopHBOX, View, BottomHBOX to VBOX root
		root.getChildren().addAll(topBar, view, legend);
		
		//Creates Scene
		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		scene.getStylesheets().add("stylesheet.css");
		//Display Scene
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		
		//Creates Instance of Algorithm Classes
		dijkstrasInstance = new Dijkstras(model, this);
		primsMazeInstance = new PrimsMazeGeneration(model, this);
		bfsInstance = new BreadthFirstSearch(model, this);
		dfsInstance = new DepthFirstSearch(model, this);
		aStarInstance = new AStar(model, this);
		recursiveMazeInstance = new RecursiveMazeGeneration(model, this);
		bidirectionalInstance = new BidirectionalSearch(model, this);
		//Initializes Variable that Determines Whether Dragging Node Will Automatically Update with Current Algorithm
		hasOtherAlgoBeenSelected = true;
		//Initializes Array that Holds the row, col Positions of Walls Replaced While Dragging Nodes
		coordinatesOfReplacedWall = new int[2];
		coordinatesOfReplacedWall[0] = -1;
		coordinatesOfReplacedWall[1] = -1;
		//Initialize togglebutton arraylist
		toggleButtons = new ArrayList<ToggleButton>();
		toggleButtons.add(dijkstrasButton);
		toggleButtons.add(primsMazeButton);
		toggleButtons.add(bfsButton);
		toggleButtons.add(aStarButton);
		toggleButtons.add(dfsButton);
		toggleButtons.add(bidirectionalButton);
		toggleButtons.add(recursiveMazeButton);
		}
	
	//Initialize Algorithm Buttons
	private void initializeDijkstras() {
		dijkstrasButton = new ToggleButton(DIJKSTRAS);
		dijkstrasButton.setOnAction(new AlgorithmHandler());
	}
	
	private void initializePrimsMazeGeneration() {
		primsMazeButton = new ToggleButton(PRIMS_MAZE_GENERATION);
		primsMazeButton.setOnAction(new AlgorithmHandler());
	}

	private void initializeRecursiveMazeGeneration() {
		recursiveMazeButton = new ToggleButton(RECURSIVE_MAZE_GENERATION);
		recursiveMazeButton.setOnAction(new AlgorithmHandler());
	}
	
	private void initializeBidirectional() {
		bidirectionalButton = new ToggleButton(BIDIRECTIONAL_SEARCH);
		bidirectionalButton.setOnAction(new AlgorithmHandler());
	}
	
	private void initializeDFS() {
		dfsButton = new ToggleButton(DFS);
		dfsButton.setOnAction(new AlgorithmHandler());
	}
	
	private void initializeAStar() {
		aStarButton = new ToggleButton(A_STAR);
		aStarButton.setOnAction(new AlgorithmHandler());
	}
	
	private void initializeBFS() {
		bfsButton = new ToggleButton(BFS);
		bfsButton.setOnAction(new AlgorithmHandler());
	}
	
	//For slider
	private void initializeSliderText() {
		sliderText = new Label("Speed");
		sliderText.setId("speed-text");
	}
	
	private void initializeSliderContainer() {
		sliderContainer = new VBox();
		sliderContainer.setSpacing(-5.0);
		sliderContainer.setAlignment(Pos.CENTER);
	}
	private void initializeSlider() {
		speedSlider = new Slider(0, 100, 2);
		speedSlider.setShowTickLabels(true);
		speedSlider.adjustValue(50);
	}
	
	//Start and end node
	private void initializeEndNode() {
		endNode = new Circle();
		endNode.setRadius(NODE_RADIUS);
		endNode.setFill(END_NODE_COLOR);
		model.setEndNode(DEFAULT_END_NODE_ROW, DEFAULT_END_NODE_COL);
	}

	private void initializeStartNode() {
		startNode = new Circle();
		startNode.setRadius(NODE_RADIUS);
		startNode.setFill(START_NODE_COLOR);
		model.setStartNode(DEFAULT_START_NODE_ROW, DEFAULT_START_NODE_COL);
	}

	//Root
	private void initializeRoot() {
		root = new VBox();
		BackgroundFill fill = new BackgroundFill(ROOT_COLOR, new CornerRadii(2), new Insets(0));
		root.setBackground(new Background(fill));
		root.setAlignment(Pos.TOP_CENTER);
		root.setPadding(new Insets(10, 10, 10, 10));
		root.setSpacing(10);
	}

	//Top HBOX
	private void initializeTopBar() {
		topBar = new HBox();
		topBar.setAlignment(Pos.CENTER);
		topBar.setSpacing(10);
		BackgroundFill fill = new BackgroundFill(TOP_BAR_COLOR, new CornerRadii(2), new Insets(0));
		topBar.setBackground(new Background(fill));
	}
		
	//View
	private void initializeGridPane() {
		view = new GridPane();
		view.setAlignment(Pos.CENTER);
		view.setHgap(0);
		view.setVgap(0);
		view.getStyleClass().add("gridpane");
	}
	
	private void initializeRectAppearance(Rectangle rect) {
		rect.setWidth(RECT_WIDTH);
		rect.setHeight(RECT_WIDTH);
		rect.setFill(NO_WALL_COLOR);
		rect.setStroke(Color.GAINSBORO);
		rect.setStrokeType(StrokeType.INSIDE);
	}
	
	//Bottom HBOX
	private void initalizeLegend() {
		legend = new HBox();
		legend.setAlignment(Pos.TOP_CENTER);
		legend.setSpacing(20);
		BackgroundFill fill = new BackgroundFill(LEGEND_COLOR, new CornerRadii(2), new Insets(0));
		legend.setBackground(new Background(fill));
	}
	
	//Starts Full Drag For Drawing and Clearing Walls
	private class GridDragDetector implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (event.getButton() == MouseButton.PRIMARY) {
				event.consume();
				view.startFullDrag();
			}
		}
		
	}
	//Handles Dragging Start and End Nodes While Mouse is Pressed
	private class NodeDragHandler implements EventHandler<MouseEvent> {

		Circle currNode;
		String str;
		public NodeDragHandler(Circle node, String s) {
			currNode = node;
			str = s;
		}
		
		@Override
		public void handle(MouseEvent event) {
			boolean edgeHandlingNeeded = edgeHandling(event);
            if (!edgeHandlingNeeded) {
            	//Sets the Node Layout If No EdgeHandling Was Required
            	currNode.setLayoutX(event.getSceneX() - NODE_RADIUS / 2.0);//?
                currNode.setLayoutY(event.getSceneY() - topBar.getHeight() - NODE_RADIUS * 2.0);//?
            }
            //Need To Constantly Update Board in Real-Time Based On Where Node Is and Using Current Algorithm
            if (!hasOtherAlgoBeenSelected) {
            	//Gets the Row and Col of the Node Based on the Current Dragging Position
            	int[] nodeCoordinates = determineNodeCurrentRowCol(currNode);
            	int row = nodeCoordinates[0];
            	int col = nodeCoordinates[1];
            	//Restores Wall If Needed At original row, col position
    			if (coordinatesOfReplacedWall[0] != -1) {
					model.setWall(coordinatesOfReplacedWall[0], coordinatesOfReplacedWall[1]);
					rectangles[coordinatesOfReplacedWall[0]][coordinatesOfReplacedWall[1]].setFill(WALL_COLOR);
				}
    			if (str.equals("start")) {
    				//Stores If There Is a Wall at new row, col position in case node is moved off of the wall and the wall must be restored
    				storeCurrWall(row, col);
    				//Sets StartNode
    				model.setStartNode(row, col);
    			} else {
    				model.setEndNode(row, col);
    				storeCurrWall(row, col);
    			}
    			//Clear Wall at New Row, Col Position
    			model.clearWall(row, col);
    			//Reinitialize Board Based on Updated Model
    			reinitializeModelAndView();
    			//Re-Execute Current Algorithm to update the visited nodes
    			executeCurrentAlgorithmAgain();
            }
		}
		
		//Handles if The Node Needs to Be Controlled Because Mouse if Off GridPane, Returns Whether EdgeHandling Was Needed
		private boolean edgeHandling(MouseEvent event) {
			//Left
			if (event.getSceneX() < RECT_WIDTH / 2) {
            	currNode.setLayoutX(RECT_WIDTH / 2);
            	return true;
            }
			//Right 
            if (event.getSceneX() > SCENE_WIDTH - RECT_WIDTH / 2) {
            	currNode.setLayoutX(SCENE_WIDTH - RECT_WIDTH / 2);
            	return true;
            }
            //Up
            if (event.getSceneY() <= topBar.getHeight() + 3 / 2.0 * RECT_WIDTH) {
            	currNode.setLayoutY(RECT_WIDTH / 2);
            	return true;
		    }
            //Down
            if (event.getSceneY() >= topBar.getHeight() + view.getHeight()) {
		        currNode.setLayoutY(NUM_ROWS * RECT_WIDTH - RECT_WIDTH / 2.0);
		        return true;
			}
            return false;
		}
		
	}
	
	//Stores if There Is a Wall Where Node is Being Dragged that Will Be Restored If Node is Moved Off
	public void storeCurrWall(int row, int col) {
		if (model.isWall(row, col)) {
			coordinatesOfReplacedWall[0] = row;
			coordinatesOfReplacedWall[1] = col;
		} else {
			coordinatesOfReplacedWall[0] = -1;
			coordinatesOfReplacedWall[1] = -1;
		}
	}
	
	//Determines What Row and Col the node is Currently At
	public int[] determineNodeCurrentRowCol(Circle node) {
		int[] returnArr = new int[2];
		//Node X, Y Position
		double currentX = node.getLayoutX();
		double currentY = node.getLayoutY();
		//Upper Left Rectangle x, y
		double upperLeftX = RECT_WIDTH / 2;
		double upperLeftY = RECT_WIDTH / 2;
		double differenceX = currentX - upperLeftX;
		double differenceY = currentY - upperLeftY;
		//New Row, Col
		returnArr[0] = (int)differenceY / RECT_WIDTH;
		returnArr[1] = (int)differenceX / RECT_WIDTH;
		//Remainder Adjustment
		double remainderRow = differenceY % RECT_WIDTH;
		double remainderCol = differenceX % RECT_WIDTH;
		if (remainderRow > RECT_WIDTH / 2) {
			returnArr[0] += 1;
		}
		if (remainderCol > RECT_WIDTH / 2) {
			returnArr[1] += 1;
		}
		return returnArr;
	}
	
	//Handles when the node is dropped
	private class RemoveNodeFocus implements EventHandler<MouseEvent> {

		Circle node;
		String str;
		
		public RemoveNodeFocus(Circle node, String string) {
			this.node = node;
			str = string;
		}
		
		@Override
		public void handle(MouseEvent event) {
			int[] newCoordinates = determineNodeCurrentRowCol(node);
			int row = newCoordinates[0];
			int col = newCoordinates[1];
			//Stores whether nodes overlapped
			boolean overLapped = false;
			//Make Sure That Nodes Don't Overlap
			int newCol = preventNodeOverlap(row, col, str);
			if (col != newCol) {
				col = newCol;
				overLapped = true;
			}
			//Removes the Node from the GridPane
			view.getChildren().remove(node);
			//Adds Node to the GridPane at Updated Row, Col
			view.add(node, col, row);
			//Only set rectangle to white if another algorithm has been selected so that the path color from real-time pathfinding is not replaced
			if (hasOtherAlgoBeenSelected) {
				rectangles[row][col].setFill(NO_WALL_COLOR);
			} else if (!hasOtherAlgoBeenSelected && overLapped){
				//If overlapped and other algo hasn't been selected, need to perform algorithm again because dragging node doesn't prepare for sudden shift in col
				executeCurrentAlgorithmAgain();
			}
			//Sets Model to Integer.MAX (endNode, clear Walls); cannot be startNode (assignedDistance = 0)
			if (node != startNode) {
				model.clearWall(row, col);
			}
			//GridPane Now Controls Node
			node.setManaged(true);
			//Resets so wall will not return
			coordinatesOfReplacedWall[0] = -1;
			coordinatesOfReplacedWall[1] = -1;
		}
	}
	
	public int preventNodeOverlap(int row, int col, String str) {
		if (str.equals("start")) {
			if (model.getEndNodeRow() == row && model.getEndNodeCol() == col) {
				//If EndNode is On same Space as StartNode, change startNodeCol
				if (col != 0) {
					col--;
				} else {
					col++;
				}
			}
			//Clear the Wall at New Row, Col
			model.clearWall(GridPane.getRowIndex(startNode), GridPane.getColumnIndex(startNode));
			//Set StartNode
			model.setStartNode(row, col);
		} else {
			if (model.getStartNodeRow() == row && model.getStartNodeCol() == col) {
				//If StartNode is on same space as endNode, change endNodeCol
				if (col != 0) {
					col--;
				} else {
					col++;
				}
			}
			//Clear the Wall at New Row, Col
			model.setEndNode(row, col);
			//Set EndNode
			model.clearWall(GridPane.getRowIndex(endNode), GridPane.getColumnIndex(endNode));
		}
		return col;
	}
	
	//Handles Drawing Walls and Erasing Walls
	private class RectDragClickHandler implements EventHandler<MouseEvent> {
		Rectangle rect;
		
		public RectDragClickHandler(Rectangle r) {
			rect = r;
		}
		@Override
		public void handle(MouseEvent event) {
			event.consume();
			int row = GridPane.getRowIndex(rect);
			int col = GridPane.getColumnIndex(rect);
			if (drawWall.isSelected()) {
				if (!model.isStartNode(row, col) && !model.isEndNode(row, col)) {
					rect.setFill(WALL_COLOR);
					model.setWall(row, col);
				}
			} else if (eraseWall.isSelected()){
				//ensures startnode remains at 0 in the model; ensures that only walls are erased
				if (!model.isStartNode(row, col) && model.isWall(row, col)) {
					rect.setFill(NO_WALL_COLOR);
					model.clearWall(row, col);
				}
			}
		}
		
	}
	
	//Interchanges DrawWall and EraseWall
	private class BoxSelector implements ChangeListener<Boolean> {
		CheckBox boxType;
		public BoxSelector(CheckBox box) {
			boxType = box;
		}
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (boxType == drawWall) {
				if (eraseWall.isSelected()) {
					eraseWall.setSelected(!newValue);
				}
				drawWall.setSelected(newValue);
			} else {
				if (drawWall.isSelected()) {
					drawWall.setSelected(!newValue);
				}
				eraseWall.setSelected(newValue);
			}
		}
	}
	
	//Handles when Algorithm is Selected from Menu; sets hasOtherAlgoBeenSelected to true so that will not automatically refresh anymore
	private class AlgorithmHandler implements EventHandler<ActionEvent> {
			@Override
		public void handle(ActionEvent event) {
			hasOtherAlgoBeenSelected = true;
			resetToggleButtons();
			reinitializeModelAndView();
			if (event.getSource() == dijkstrasButton) {
				dijkstrasButton.setSelected(true);
				dijkstrasInstance.dijkstrasAlgorithm();
				currentAlgorithm = DIJKSTRAS;
			} else if (event.getSource() == primsMazeButton) {
				clearEverything();
				primsMazeButton.setSelected(true);
				currentAlgorithm = PRIMS_MAZE_GENERATION;
				primsMazeInstance.primsAlgorithm();
			} else if (event.getSource() == bfsButton) {
				bfsButton.setSelected(true);
				currentAlgorithm = BFS;
				bfsInstance.bfs();
			} else if (event.getSource() == aStarButton) {
				aStarButton.setSelected(true);
				currentAlgorithm = A_STAR;
				aStarInstance.aStar();
			} else if (event.getSource() == dfsButton) {
				dfsButton.setSelected(true);
				currentAlgorithm = DFS;
				dfsInstance.dfs();
			} else if (event.getSource() == recursiveMazeButton) {
				recursiveMazeButton.setSelected(true);
				currentAlgorithm = RECURSIVE_MAZE_GENERATION;
				recursiveMazeInstance.recursiveMazeDivision();
			} else if (event.getSource() == bidirectionalButton) {
				bidirectionalButton.setSelected(true);
				currentAlgorithm = BIDIRECTIONAL_SEARCH;
				bidirectionalInstance.bidirectionalSearch();
			}
		}	
	}
	
	//Controls Slider Speed Range
	private class SliderListener implements ChangeListener<Number> {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if (Math.floor((double)newValue / 2) == 0.0) {
				TimerClass.setDelay(5 * Math.pow(10, 8));//max 500_000_000
			} else { 
				TimerClass.setDelay(5 * Math.pow(10, 8) / ((double)newValue / 2));//min is 10_000_000
			}
		}
		
	}
	
	//Creates about page 
	private class AboutHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (instructionPage == null || !instructionPage.isShowing()) {
				WebView webView = new WebView();
				WebEngine engine = webView.getEngine();
				File f = new File("AboutPathfinder.html");
				String url = f.getAbsolutePath();
				engine.load("file:///" + url);
				instructionPage = new Stage();
				instructionPage.setTitle("About Pathfinder");
				Scene scene = new Scene(webView, 1300, 550);
				instructionPage.setScene(scene);
				instructionPage.show();
			} else {
				instructionPage.toFront();
			}
		}
	}
	
	//Controls the ShowSteps variable that is accessed in Algorithms Class
	private class ShowSteps implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			showSteps.setSelected(newValue);
		}
	}
	
	//Finishes algorithm if Finish algorithm pressed; clears board if clear board is pressed
	private class ButtonHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == finishAlgorithm &&TimerClass.isAlgoOccurring()) {
				isAlgoFinishButtonPressed = true;//default is false
				forceQuitCurrentAlgorithm();
			}
			if (event.getSource() == clearBoard) {
				hasOtherAlgoBeenSelected = true;
				resetToggleButtons();
				clearEverything();
			}
		}		
	}
	
	//Returns Rectangle from 2D Array
	public Rectangle getRectangle(int row, int col) {
		return rectangles[row][col];
	}
	
	//Returns row, col of a particular rectangle
	public int[] getRowCol(Rectangle r) {
		int[] returnArr = new int[2];
		returnArr[0] = GridPane.getRowIndex(r);
		returnArr[1] = GridPane.getColumnIndex(r);
		return returnArr;
	}
	
	//Reinitalizes Model and View for New Algorithm Run
	public void reinitializeModelAndView() {
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				int curr = model.getDistanceAt(row, col);
				if (curr == model.WALL) {
					continue;
				}
				if (model.isStartNode(row, col)) {
					model.setDistanceAt(row, col, 0);
				} else if (curr == model.VISITED) {
					//Gets visited nodes
					model.setDistanceAt(row, col, model.NO_WALL);
				} else if (curr != Integer.MAX_VALUE) {
					//Gets neighbors
					model.setDistanceAt(row, col, model.NO_WALL);
				}
				rectangles[row][col].setFill(NO_WALL_COLOR);
			}
		}
	}
	
	//Clears walls, animations
	public void clearEverything() {
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				rectangles[row][col].setFill(NO_WALL_COLOR);
				if (row == model.getStartNodeRow() && col == model.getStartNodeCol()) {
					model.setDistanceAt(row, col, 0);
				} else {
					model.setDistanceAt(row, col, model.NO_WALL);
				}
			}
		}
	}
	
	//Returns value of this variable
	public boolean hasAlgoFinishBeenPressed() {
		return isAlgoFinishButtonPressed;
	}
	
	//Sets Value of this Variable
	public void setAlgoFinishPressed(boolean finish) {
		isAlgoFinishButtonPressed = finish;
	}
	
	//Disables Grid for when Algorithm is Running
	public void disableGridListener() {
		view.setDisable(true);
	}
	
	//Enables Grid after Algorithm is Done Running
	public void enableGridListener() {
		view.setDisable(false);
	}
	
	//Returns value of this variable
	public boolean getHasBeenRefreshed() {
		return hasOtherAlgoBeenSelected;
	}
	
	//Sets value of this variable
	public void setHasBeenRefreshed(boolean b) {
		hasOtherAlgoBeenSelected = b;
	}
	
	public void resetToggleButtons() {
		for (int i = 0; i < toggleButtons.size(); i++) {
			toggleButtons.get(i).setSelected(false);
		}
	}
	
	//Is called when node moves, so algorithm needs to be re-executed
	public void executeCurrentAlgorithmAgain() {
		if (currentAlgorithm == DIJKSTRAS) {
			dijkstrasInstance.dijkstrasAlgorithm();
		} else if (currentAlgorithm == PRIMS_MAZE_GENERATION) {
			//DO NOTHING BECAUSE MOVING NODES SHOULDN'T REGENERATE MAZE IN REAL-TIME
			//mazeInstance.generateMaze();
		} else if (currentAlgorithm == BFS) {
			bfsInstance.bfs();
		} else if (currentAlgorithm == A_STAR) {
			aStarInstance.aStar();
		} else if (currentAlgorithm == DFS) {
			dfsInstance.dfs();
		} else if (currentAlgorithm == RECURSIVE_MAZE_GENERATION) {
			//DO NOTHING
		} else if (currentAlgorithm == BIDIRECTIONAL_SEARCH) {
			bidirectionalInstance.bidirectionalSearch();
		}
	}
	
	//Finishes current algorithm
	public void forceQuitCurrentAlgorithm() {
		if (currentAlgorithm == DIJKSTRAS) {
			dijkstrasInstance.handle(0);
		} else if (currentAlgorithm == PRIMS_MAZE_GENERATION) {
			primsMazeInstance.handle(0);
		} else if (currentAlgorithm == BFS) {
			bfsInstance.handle(0);
		} else if (currentAlgorithm == A_STAR) {
			aStarInstance.handle(0);
		} else if (currentAlgorithm == DFS) {
			dfsInstance.handle(0);
		} else if (currentAlgorithm == RECURSIVE_MAZE_GENERATION) {
			recursiveMazeInstance.handle(0);
		} else if (currentAlgorithm == BIDIRECTIONAL_SEARCH) {
			bidirectionalInstance.handle(0);
		}
	}
	
	public void setCurrentAlgorithm(String s) {
		currentAlgorithm = s;
	}
	
	public void disableAlgorithmButtons() {
		for (int i = 0; i < toggleButtons.size(); i++) {
			toggleButtons.get(i).setDisable(true);
		}
	}
	
	public void enableAlgorithmButtons() {
		for (int i = 0; i < toggleButtons.size(); i++) {
			toggleButtons.get(i).setDisable(false);
		}
	}
	
	public void disableClearBoard() {
		clearBoard.setDisable(true);
	}
	
	public void enableClearBoard() {
		clearBoard.setDisable(false);
	}
}