/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.control;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import org.kordamp.ikonli.javafx.FontIcon;

import static java.util.logging.Level.SEVERE;
import static javafx.scene.input.MouseButton.PRIMARY;


/**
 * A JavaFX controller with 9 buttons to navigate a graphical area. Zoom, pan
 * and undo/redo buttons are provided and can be bound to the application
 * code through the provided {@link EventHandler} methods.
 *
 * @author claudio.rosati@esss.se
 */
public class NavigatorController extends AnchorPane {

	/**
	 * Called when the navigator's {@code panDown} button is pressed.
	 */
	public static final EventType<Event> ON_PAN_DOWN = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_PAN_DOWN");

	/**
	 * Called when the navigator's {@code panLeft} button is pressed.
	 */
	public static final EventType<Event> ON_PAN_LEFT = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_PAN_LEFT");

	/**
	 * Called when the navigator's {@code panRight} button is pressed.
	 */
	public static final EventType<Event> ON_PAN_RIGHT = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_PAN_RIGHT");

	/**
	 * Called when the navigator's {@code panUp} button is pressed.
	 */
	public static final EventType<Event> ON_PAN_UP = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_PAN_UP");

	/**
	 * Called when the navigator's {@code redo} button is pressed.
	 */
	public static final EventType<Event> ON_REDO = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_REDO");

	/**
	 * Called when the navigator's {@code undo} button is pressed.
	 */
	public static final EventType<Event> ON_UNDO = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_UNDO");

	/**
	 * Called when the navigator's {@code zoomIn} button is pressed.
	 */
	public static final EventType<Event> ON_ZOOM_IN = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_ZOOM_IN");

	/**
	 * Called when the navigator's {@code zoomOut} button is pressed.
	 */
	public static final EventType<Event> ON_ZOOM_OUT = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_ZOOM_OUT");

	/**
	 * Called when the navigator's {@code zoomToOne} button is pressed.
	 */
	public static final EventType<Event> ON_ZOOM_TO_ONE = new EventType<Event>(Event.ANY, "NAVIGATOR_ON_ZOOM_TO_ONE");

	private static final String DISABLED_CHANGE_LISTENER = "DISABLED_CHANGE_LISTENER";
	private static final String FOCUS_CHANGE_LISTENER = "FOCUS_CHANGE_LISTENER";
	private static final String KEY_PRESSED_HANDLER = "KEY_PRESSED_HANDLER";
	private static final String KEY_RELEASED_HANDLER = "KEY_RELEASED_HANDLER";

	private static final Logger LOGGER = Logger.getLogger(NavigatorController.class.getName());

	private String enteredStyle;
	@FXML private Path panDown;
	@FXML private FontIcon panDownIcon;
	@FXML private Path panLeft;
	@FXML private FontIcon panLeftIcon;
	@FXML private Path panRight;
	@FXML private FontIcon panRightIcon;
	@FXML private Path panUp;
	@FXML private FontIcon panUpIcon;
	@FXML private Path redo;
	@FXML private FontIcon redoIcon;
	@FXML private Path undo;
	@FXML private FontIcon undoIcon;
	@FXML private Path zoomIn;
	@FXML private FontIcon zoomInIcon;
	@FXML private Path zoomOut;
	@FXML private FontIcon zoomOutIcon;
	@FXML private Circle zoomToOne;
	@FXML private Label zoomToOneLabel;

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- onPanDown ----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onPanDown = new SimpleObjectProperty<>(NavigatorController.this, "onPanDown") {
		@Override protected void invalidated() {
			setEventHandler(ON_PAN_DOWN, get());
		}
	};

	/**
	 * Called when the {@code panDown} button is pressed.
	 *
	 * @return The "on pan down" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onPanDownProperty() {
		return onPanDown;
	}

	public final EventHandler<Event> getOnPanDown() {
		return onPanDownProperty().get();
	}

	public final void setOnPanDown( EventHandler<Event> value ) {
		onPanDownProperty().set(value);
	}

	/*
	 * ---- onPanLeft ----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onPanLeft = new SimpleObjectProperty<>(NavigatorController.this, "onPanLeft") {
		@Override protected void invalidated() {
			setEventHandler(ON_PAN_LEFT, get());
		}
	};

	/**
	 * Called when the {@code panLeft} button is pressed.
	 *
	 * @return The "on pan left" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onPanLeftProperty() {
		return onPanLeft;
	}

	public final EventHandler<Event> getOnPanLeft() {
		return onPanLeftProperty().get();
	}

	public final void setOnPanLeft( EventHandler<Event> value ) {
		onPanLeftProperty().set(value);
	}

	/*
	 * ---- onPanRight ---------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onPanRight = new SimpleObjectProperty<>(NavigatorController.this, "onPanRight") {
		@Override protected void invalidated() {
			setEventHandler(ON_PAN_RIGHT, get());
		}
	};

	/**
	 * Called when the {@code panRight} button is pressed.
	 *
	 * @return The "on pan right" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onPanRightProperty() {
		return onPanRight;
	}

	public final EventHandler<Event> getOnPanRight() {
		return onPanRightProperty().get();
	}

	public final void setOnPanRight( EventHandler<Event> value ) {
		onPanRightProperty().set(value);
	}

	/*
	 * ---- onPanUp ------------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onPanUp = new SimpleObjectProperty<>(NavigatorController.this, "onPanUp") {
		@Override protected void invalidated() {
			setEventHandler(ON_PAN_UP, get());
		}
	};

	/**
	 * Called when the {@code panUp} button is pressed.
	 *
	 * @return The "on pan up" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onPanUpProperty() {
		return onPanUp;
	}

	public final EventHandler<Event> getOnPanUp() {
		return onPanUpProperty().get();
	}

	public final void setOnPanUp( EventHandler<Event> value ) {
		onPanUpProperty().set(value);
	}

	/*
	 * ---- onRedo -------------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onRedo = new SimpleObjectProperty<>(NavigatorController.this, "onRedo") {
		@Override protected void invalidated() {
			setEventHandler(ON_REDO, get());
		}
	};

	/**
	 * Called when the {@code redo} button is pressed.
	 *
	 * @return The "on redo" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onRedoProperty() {
		return onRedo;
	}

	public final EventHandler<Event> getOnRedo() {
		return onRedoProperty().get();
	}

	public final void setOnRedo( EventHandler<Event> value ) {
		onRedoProperty().set(value);
	}

	/*
	 * ---- onUndo -------------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onUndo = new SimpleObjectProperty<>(NavigatorController.this, "onUndo") {
		@Override protected void invalidated() {
			setEventHandler(ON_UNDO, get());
		}
	};

	/**
	 * Called when the {@code undo} button is pressed.
	 *
	 * @return The "on undo" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onUndoProperty() {
		return onUndo;
	}

	public final EventHandler<Event> getOnUndo() {
		return onUndoProperty().get();
	}

	public final void setOnUndo( EventHandler<Event> value ) {
		onUndoProperty().set(value);
	}

	/*
	 * ---- onZoomIn -----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onZoomIn = new SimpleObjectProperty<>(NavigatorController.this, "onZoomIn") {
		@Override protected void invalidated() {
			setEventHandler(ON_ZOOM_IN, get());
		}
	};

	/**
	 * Called when the {@code zoomIn} button is pressed.
	 *
	 * @return The "on zoom in" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomInProperty() {
		return onZoomIn;
	}

	public final EventHandler<Event> getOnZoomIn() {
		return onZoomInProperty().get();
	}

	public final void setOnZoomIn( EventHandler<Event> value ) {
		onZoomInProperty().set(value);
	}

	/*
	 * ---- onZoomOut ----------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onZoomOut = new SimpleObjectProperty<>(NavigatorController.this, "onZoomOut") {
		@Override protected void invalidated() {
			setEventHandler(ON_ZOOM_OUT, get());
		}
	};

	/**
	 * Called when the {@code zoomOut} button is pressed.
	 *
	 * @return The "on zoom out" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomOutProperty() {
		return onZoomOut;
	}

	public final EventHandler<Event> getOnZoomOut() {
		return onZoomOutProperty().get();
	}

	public final void setOnZoomOut( EventHandler<Event> value ) {
		onZoomOutProperty().set(value);
	}

	/*
	 * ---- onZoomToOne --------------------------------------------------------
	 */
	private ObjectProperty<EventHandler<Event>> onZoomToOne = new SimpleObjectProperty<>(NavigatorController.this, "onZoomToOne") {
		@Override protected void invalidated() {
			setEventHandler(ON_ZOOM_TO_ONE, get());
		}
	};

	/**
	 * Called when the {@code zoomToOne} button is pressed.
	 *
	 * @return The "on zoom to one" property.
	 */
	public final ObjectProperty<EventHandler<Event>> onZoomToOneProperty() {
		return onZoomToOne;
	}

	public final EventHandler<Event> getOnZoomToOne() {
		return onZoomToOneProperty().get();
	}

	public final void setOnZoomToOne( EventHandler<Event> value ) {
		onZoomToOneProperty().set(value);
	}

	/*
	 * ---- panDownDisabled ----------------------------------------------------
	 */
	private BooleanProperty panDownDisabled = new SimpleBooleanProperty(NavigatorController.this, "panDownDisabled", false);

	/**
	 * Indicates whether or not the {@code panDown} button is disabled.
	 *
	 * @return Whether or not the "pan down" button is disabled.
	 */
	public final BooleanProperty panDownDisabledProperty() {
		return panDownDisabled;
	}

	public final boolean isPanDownDisabled() {
		return panDownDisabledProperty().get();
	}

	public final void setPanDownDisabled( boolean disabled ) {
		panDownDisabledProperty().set(disabled);
	}

	/*
	 * ---- panLeftDisabled ----------------------------------------------------
	 */
	private BooleanProperty panLeftDisabled = new SimpleBooleanProperty(NavigatorController.this, "panLeftDisabled", false);

	/**
	 * Indicates whether or not the {@code panLeft} button is disabled.
	 *
	 * @return Whether or not the "pan left" button is disabled.
	 */
	public final BooleanProperty panLeftDisabledProperty() {
		return panLeftDisabled;
	}

	public final boolean isPanLeftDisabled() {
		return panLeftDisabledProperty().get();
	}

	public final void setPanLeftDisabled( boolean disabled ) {
		panLeftDisabledProperty().set(disabled);
	}

	/*
	 * ---- panRightDisabled ---------------------------------------------------
	 */
	private BooleanProperty panRightDisabled = new SimpleBooleanProperty(NavigatorController.this, "panRightDisabled", false);

	/**
	 * Indicates whether or not the {@code panRight} button is disabled.
	 *
	 * @return Whether or not the "pan right" button is disabled.
	 */
	public final BooleanProperty panRightDisabledProperty() {
		return panRightDisabled;
	}

	public final boolean isPanRightDisabled() {
		return panRightDisabledProperty().get();
	}

	public final void setPanRightDisabled( boolean disabled ) {
		panRightDisabledProperty().set(disabled);
	}

	/*
	 * ---- panUpDisabled ------------------------------------------------------
	 */
	private BooleanProperty panUpDisabled = new SimpleBooleanProperty(NavigatorController.this, "panUpDisabled", false);

	/**
	 * Indicates whether or not the {@code panUp} button is disabled.
	 *
	 * @return Whether or not the "pan up" button is disabled.
	 */
	public final BooleanProperty panUpDisabledProperty() {
		return panUpDisabled;
	}

	public final boolean isPanUpDisabled() {
		return panUpDisabledProperty().get();
	}

	public final void setPanUpDisabled( boolean disabled ) {
		panUpDisabledProperty().set(disabled);
	}

	/*
	 * ---- redoDisabled -------------------------------------------------------
	 */
	private BooleanProperty redoDisabled = new SimpleBooleanProperty(NavigatorController.this, "redoDisabled", false);

	/**
	 * Indicates whether or not the {@code redo} button is disabled.
	 *
	 * @return Whether or not the "redo" button is disabled.
	 */
	public final BooleanProperty redoDisabledProperty() {
		return redoDisabled;
	}

	public final boolean isRedoDisabled() {
		return redoDisabledProperty().get();
	}

	public final void setRedoDisabled( boolean disabled ) {
		redoDisabledProperty().set(disabled);
	}

	/*
	 * ---- undoDisabled -------------------------------------------------------
	 */
	private BooleanProperty undoDisabled = new SimpleBooleanProperty(NavigatorController.this, "undoDisabled", false);

	/**
	 * Indicates whether or not the {@code undo} button is disabled.
	 *
	 * @return Whether or not the "undo" button is disabled.
	 */
	public final BooleanProperty undoDisabledProperty() {
		return undoDisabled;
	}

	public final boolean isUndoDisabled() {
		return undoDisabledProperty().get();
	}

	public final void setUndoDisabled( boolean disabled ) {
		undoDisabledProperty().set(disabled);
	}

	/*
	 * ---- zoomInDisabled -----------------------------------------------------
	 */
	private BooleanProperty zoomInDisabled = new SimpleBooleanProperty(NavigatorController.this, "zoomInDisabled", false);

	/**
	 * Indicates whether or not the {@code zoomIn} button is disabled.
	 *
	 * @return Whether or not the "zoom in" button is disabled.
	 */
	public final BooleanProperty zoomInDisabledProperty() {
		return zoomInDisabled;
	}

	public final boolean isZoomInDisabled() {
		return zoomInDisabledProperty().get();
	}

	public final void setZoomInDisabled( boolean disabled ) {
		zoomInDisabledProperty().set(disabled);
	}

	/*
	 * ---- zoomOutDisabled ----------------------------------------------------
	 */
	private BooleanProperty zoomOutDisabled = new SimpleBooleanProperty(NavigatorController.this, "zoomOutDisabled", false);

	/**
	 * Indicates whether or not the {@code zoomOut} button is disabled.
	 *
	 * @return Whether or not the "zoom out" button is disabled.
	 */
	public final BooleanProperty zoomOutDisabledProperty() {
		return zoomOutDisabled;
	}

	public final boolean isZoomOutDisabled() {
		return zoomOutDisabledProperty().get();
	}

	public final void setZoomOutDisabled( boolean disabled ) {
		zoomOutDisabledProperty().set(disabled);
	}

	/*
	 * ---- zoomToOneDisabled --------------------------------------------------
	 */
	private BooleanProperty zoomToOneDisabled = new SimpleBooleanProperty(NavigatorController.this, "zoomToOneDisabled", false);

	/**
	 * Indicates whether or not the {@code zoomToOne} button is disabled.
	 *
	 * @return Whether or not the "zoom to one" button is disabled.
	 */
	public final BooleanProperty zoomToOneDisabledProperty() {
		return zoomToOneDisabled;
	}

	public final boolean isZoomToOneDisabled() {
		return zoomToOneDisabledProperty().get();
	}

	public final void setZoomToOneDisabled( boolean disabled ) {
		zoomToOneDisabledProperty().set(disabled);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	public NavigatorController() {
		init();
		initListeners();
	}

	private void fireNavigationEvent ( Node source ) {

		String id = source.getId();

		if ( "panDown".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_PAN_DOWN));
		} else if ( "panLeft".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_PAN_LEFT));
		} else if ( "panRight".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_PAN_RIGHT));
		} else if ( "panUp".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_PAN_UP));
		} else if ( "redo".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_REDO));
		} else if ( "undo".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_UNDO));
		} else if ( "zoomIn".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_ZOOM_IN));
		} else if ( "zoomOut".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_ZOOM_OUT));
		} else if ( "zoomToOne".equals(id) ) {
			Event.fireEvent(source, new Event(this, source, ON_ZOOM_TO_ONE));
		} else {
			LOGGER.warning(MessageFormat.format("Unexpected identifier [{0}].", id));
		}

	}

	private void init() {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/navigator.fxml"));

			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch ( IOException ex ) {
			LOGGER.log(
				SEVERE,
				MessageFormat.format(
					"Unable to load 'Navigator.xml' resource [{0}].",
					getClass().getResource("Navigator.xml")
				),
				ex
			);
		}
	}

	private void initListeners() {

		initListeners(panDown, panDownIcon);
		panDown.disableProperty().bind(panDownDisabledProperty());
		panDownIcon.disableProperty().bind(panDownDisabledProperty());

		initListeners(panLeft, panLeftIcon);
		panLeft.disableProperty().bind(panLeftDisabledProperty());
		panLeftIcon.disableProperty().bind(panLeftDisabledProperty());

		initListeners(panRight, panRightIcon);
		panRight.disableProperty().bind(panRightDisabledProperty());
		panRightIcon.disableProperty().bind(panRightDisabledProperty());

		initListeners(panUp, panUpIcon);
		panUp.disableProperty().bind(panUpDisabledProperty());
		panUpIcon.disableProperty().bind(panUpDisabledProperty());

		initListeners(redo, redoIcon);
		redo.disableProperty().bind(redoDisabledProperty());
		redoIcon.disableProperty().bind(redoDisabledProperty());

		initListeners(undo, undoIcon);
		undo.disableProperty().bind(undoDisabledProperty());
		undoIcon.disableProperty().bind(undoDisabledProperty());

		initListeners(zoomIn, zoomInIcon);
		zoomIn.disableProperty().bind(zoomInDisabledProperty());
		zoomInIcon.disableProperty().bind(zoomInDisabledProperty());

		initListeners(zoomOut, zoomOutIcon);
		zoomOut.disableProperty().bind(zoomOutDisabledProperty());
		zoomOutIcon.disableProperty().bind(zoomOutDisabledProperty());

		initListeners(zoomToOne, zoomToOneLabel);
		zoomToOne.disableProperty().bind(zoomToOneDisabledProperty());
		zoomToOneLabel.disableProperty().bind(zoomToOneDisabledProperty());

	}

	private void initListeners( final Shape shape, final Node icon ) {

		Map<String, Object> listenersMap = new HashMap<>(4);
		EventHandler<KeyEvent> keyPressedHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle( KeyEvent event ) {
				if ( shape.isFocused() && !shape.isDisabled() ) {
					switch ( event.getCode() ) {
						case ENTER:
						case SPACE:
							updateStyle(shape, FillStyle.PRESSED, StrokeStyle.FOCUSED);
							event.consume();
						break;
					}
				}
			}
		};
		EventHandler<KeyEvent> keyReleasedHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle( KeyEvent event ) {
				if ( shape.isFocused() && !shape.isDisabled() ) {
					switch ( event.getCode() ) {
						case ENTER:
						case SPACE:
							fireNavigationEvent(shape);
							updateStyle(shape);
							event.consume();
							break;
					}
				}
			}
		};
		ChangeListener<Boolean> focusChangeListener = ( observable, hadFocus, hasFocus ) -> {
			updateStyle(shape);
		};
		ChangeListener<Boolean> disabledChangeListener = ( observable, wasDisabled, isDisabled ) -> {
			updateStyle(shape);
			icon.setOpacity(isDisabled ? 0.3 : 1.0);
		};

		listenersMap.put(DISABLED_CHANGE_LISTENER, disabledChangeListener);
		listenersMap.put(FOCUS_CHANGE_LISTENER, focusChangeListener);
		listenersMap.put(KEY_PRESSED_HANDLER, keyPressedHandler);
		listenersMap.put(KEY_RELEASED_HANDLER, keyReleasedHandler);

		shape.setUserData(listenersMap);
		shape.disabledProperty().addListener(new WeakChangeListener<>(disabledChangeListener));
		shape.focusedProperty().addListener(new WeakChangeListener<>(focusChangeListener));
		shape.addEventHandler(KeyEvent.KEY_PRESSED, new WeakEventHandler<>(keyPressedHandler));
		shape.addEventHandler(KeyEvent.KEY_RELEASED, new WeakEventHandler<>(keyReleasedHandler));

	}

	private boolean isPanButton( Node node ) {
		return ( node == panDown || node == panLeft || node == panRight || node == panUp );
	}

	@FXML
	@SuppressWarnings( "ConvertToStringSwitch" )
	private void mouseClicked( MouseEvent event ) {

		Node source = (Node) event.getSource();

		source.requestFocus();

		if ( source != null && !source.isDisabled() && PRIMARY == event.getButton() ) {
			fireNavigationEvent(source);
		}

		event.consume();

	}

	@FXML
	private void mouseEntered( MouseEvent event ) {
		updateStyle((Node) event.getSource());
		event.consume();
	}

	@FXML
	private void mouseExited( MouseEvent event ) {
		updateStyle((Node) event.getSource());
		event.consume();
	}

	@FXML
	private void mousePressed( MouseEvent event ) {
		updateStyle((Node) event.getSource());
		event.consume();
	}

	@FXML
	private void mouseReleased( MouseEvent event ) {
		updateStyle((Node) event.getSource());
		event.consume();
	}

	private void updateStyle( Node node ) {
		updateStyle(
			node,
			node.isDisabled() 
			? FillStyle.DISABLED
			: ( node.isPressed()
				? FillStyle.PRESSED
				: ( node.isHover()
					? FillStyle.HOVER
					: ( isPanButton(node)
						? FillStyle.LIGHTER
						: FillStyle.DEFAULT ) ) ),
			node.isFocused() ? StrokeStyle.FOCUSED : StrokeStyle.DEFAULT
		);
	}

	private void updateStyle( Node node, FillStyle fill, StrokeStyle stroke ) {
		node.setStyle(MessageFormat.format("{0}; {1};", fill.getStyle(), stroke.getStyle()));
	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	enum FillStyle {

		DEFAULT("-fx-fill: -normal-color"),
		LIGHTER("-fx-fill: -normal-color-lighter"),
		HOVER("-fx-fill: -hover-color"),
		PRESSED("-fx-fill: -pressed-color"),
		DISABLED("-fx-fill: -disabled-color");

		private final String style;

		FillStyle( String style ) {
			this.style = style;
		}

		String getStyle() {
			return style;
		}

	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	enum StrokeStyle {

		DEFAULT("-fx-stroke: -fx-outer-border"),
		FOCUSED("-fx-stroke: -fx-focus-color");

		private final String style;

		StrokeStyle( String style ) {
			this.style = style;
		}

		String getStyle() {
			return style;
		}

	}

}
