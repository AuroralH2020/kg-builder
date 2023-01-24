package publisher;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

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
import publisher.fxml.MainGUI;
import publisher.fxml.PublisherGUIController;
import publisher.rest.ReaderThread;
import publisher.rest.ServiceConfiguration;
import publisher.rest.ServicePublisher;
import publisher.rest.controller.StaticPersistence;
import publisher.rest.controller.ViewRendererController;

public class Publisher  {

	// -- Main
	public static final String CONFIG_PORT_ARGUMENT = "-config.port=";
	public static final String PUBLISHER_PORT_ARGUMENT = "-publisher.port=";
	public static final String DEMON_ARGUMENT = "-d";
	public static Boolean DAEMON_MODE = false;
	public static int CONFIG_PORT = 4567;
	public static int PUBLISHER_PORT = 9000;
	

	public static void main(String[] args) {
		// Configuration
		configure(args);
		Publisher.startServices();
		if (!DAEMON_MODE) {
			MainGUI.main(args);
		}else {
			System.out.println(LOGO);
			System.out.println("Configuration service running on " + "http://localhost:" + CONFIG_PORT);
			System.out.println("Publisher service running on " + "http://localhost:" + PUBLISHER_PORT);
			ServiceConfiguration.openInBrowser();
		}



	}

	public static void startServices() {
		try {
		// Run services
		ServiceConfiguration.runConfigurationMode(CONFIG_PORT);
		System.out.println("Configuration service up!");
		ServicePublisher.runPublishMode(PUBLISHER_PORT);
		System.out.println("Publishing service up!");
		// Mock data
		StaticPersistence.updateMockData();
		System.out.println("Mocking some data");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected static final String LOGO = "\n" +
	          "  ██╗  ██╗███████╗██╗     ██╗ ██████╗                                 \n"
			+ "  ██║  ██║██╔════╝██║     ██║██╔═══██╗                                \n"
			+ "  ███████║█████╗  ██║     ██║██║   ██║                                \n"
			+ "  ██╔══██║██╔══╝  ██║     ██║██║   ██║                                \n"
			+ "  ██║  ██║███████╗███████╗██║╚██████╔╝                                \n"
			+ "  ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝ ╚═════╝                                 \n"
			+ "  ██████╗ ██╗   ██╗██████╗ ██╗     ██╗███████╗██╗  ██╗███████╗██████╗ \n"
			+ "  ██╔══██╗██║   ██║██╔══██╗██║     ██║██╔════╝██║  ██║██╔════╝██╔══██╗\n"
			+ "  ██████╔╝██║   ██║██████╔╝██║     ██║███████╗███████║█████╗  ██████╔╝\n"
			+ "  ██╔═══╝ ██║   ██║██╔══██╗██║     ██║╚════██║██╔══██║██╔══╝  ██╔══██╗\n"
			+ "  ██║     ╚██████╔╝██████╔╝███████╗██║███████║██║  ██║███████╗██║  ██║\n"
			+ "  ╚═╝      ╚═════╝ ╚═════╝ ╚══════╝╚═╝╚══════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝\n"
			+ "                                                              v0.4.0  \n"
			+ "                                                          \n" + "";

	private static void configure(String[] args) {
		// create ViewRenderer
		ViewRendererController.init();
		try {
			for (String argument : args) {
				if (argument != null && !argument.isBlank()) {
					if (argument.contains(PUBLISHER_PORT_ARGUMENT)) {
						PUBLISHER_PORT = Integer.valueOf(argument.replace(PUBLISHER_PORT_ARGUMENT, ""));
					} else if (argument.contains(CONFIG_PORT_ARGUMENT)) {
						CONFIG_PORT = Integer.valueOf(argument.replace(CONFIG_PORT_ARGUMENT, ""));
					}
					DAEMON_MODE = argument.contains(DEMON_ARGUMENT);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	

}
