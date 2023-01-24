package publisher.fxml;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import publisher.Publisher;
import publisher.rest.ServiceConfiguration;
import publisher.rest.ServicePublisher;


public class PublisherGUIController implements Initializable {

	private boolean started = true;

	public static TextArea staticTxtArea;

	@FXML
	private Button startStopButton;

	@FXML
	private Button openConfigurationServiceButton;

	@FXML
	private TextField publicationPort;

	@FXML
	private TextField configurationPort;

	@FXML
	private TextArea logScreen;


	public TextArea getLogScreen() {
		return logScreen;
	}

	 @FXML
	private void handleStartStop(MouseEvent event){
		 startStopButton.setDisable(true);
	        if(started) {
	        	startStopButton.setText("START");
	        	ServicePublisher.stopService();
	        	ServiceConfiguration.stopService();
	        	publicationPort.setDisable(false);
	        	configurationPort.setDisable(false);
	        }else {
	        	startStopButton.setText("STOP");
	        	Publisher.CONFIG_PORT = Integer.valueOf(configurationPort.getText());
	        	Publisher.PUBLISHER_PORT = Integer.valueOf(publicationPort.getText());
	        	Publisher.startServices();
	        	publicationPort.setDisable(true);
	        	configurationPort.setDisable(true);
	        }
	        started = !started;
	        startStopButton.setDisable(false);
	}

	 @FXML
	 private void openConfigurationService(MouseEvent event){
		  ServiceConfiguration.openInBrowser();
	  }






	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		staticTxtArea = logScreen;
	}


}
