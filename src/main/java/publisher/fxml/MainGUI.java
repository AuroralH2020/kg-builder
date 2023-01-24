package publisher.fxml;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import publisher.rest.ReaderThread;

public class MainGUI extends Application {
	// JavaFx
	private final PipedInputStream pipeIn = new PipedInputStream();
	private final PipedInputStream pipeIn2 = new PipedInputStream();
	Thread errorThrower;
	private Thread reader;
	private Thread reader2;
	boolean quit;
	private TextArea txtArea;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = new Scene(loadFXML("publisher-gui.fxml"));
		stage.setTitle("Helio Publisher");
		// stage.getIcons().add(new
		// Image(getClass().getResource("android-chrome-192x192.png").toString()));
		stage.getIcons().add(new Image(MainGUI.class.getResource("android-chrome-192x192.png").openStream()));

		stage.setScene(scene);
		stage.toFront();
		stage.show();

		txtArea = PublisherGUIController.staticTxtArea;
		// Thread execution for reading output stream
		executeReaderThreads();

		// Thread closing on stag close event
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {

				closeThread();
				Platform.exit();
				System.exit(0);
			}
		});

	}

	private Parent loadFXML(String fxml) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource(fxml));
		// TODO: LA IMAGEN DEL LOGO
		// TODO: PONER COMO EN GRAPH DB UN BANNER DE INICIO
		// TODO: EL LOG
		return fxmlLoader.load();
	}

	public void executeReaderThreads() {
		try {
			PipedOutputStream pout = new PipedOutputStream(this.pipeIn);
			System.setOut(new PrintStream(pout, true));
		} catch (IOException io) {
		} catch (SecurityException se) {
		}

		try {
			PipedOutputStream pout2 = new PipedOutputStream(this.pipeIn2);
			System.setErr(new PrintStream(pout2, true));
		} catch (IOException io) {
		} catch (SecurityException se) {
		}

		ReaderThread obj = new ReaderThread(pipeIn, pipeIn2, errorThrower, reader, reader2, quit, txtArea);
	}

	synchronized void closeThread() {
		System.out.println("Message: Stage is closed.");
		this.quit = true;
		notifyAll();
		try {
			this.reader.join(1000L);
			this.pipeIn.close();
		} catch (Exception e) {
		}
		try {
			this.reader2.join(1000L);
			this.pipeIn2.close();
		} catch (Exception e) {
		}
		System.exit(0);
	}

}
