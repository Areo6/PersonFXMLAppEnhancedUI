/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personfxmlappenhancedui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import com.asgteach.familytree.model.Familytreemanager;
import com.asgteach.familytree.model.Person;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Developer
 */
public class PersonFXMLController implements Initializable {
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField middlenameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField suffixTextField;
    @FXML
    private TreeView<Person> personTreView;
    @FXML
    private TextArea notesTextArea;
    @FXML
    private Button updateButton;
    @FXML
    private RadioButton maleRadioButton;
    @FXML
    private RadioButton unknownRadioButton;
    @FXML
    private RadioButton femaleRadioButton;
    @FXML
    private ToggleGroup genderToggleGroup;
    
  
    private static final Logger logger = Logger.getLogger(
                       PersonFXMLController.class.getName());
    private final Familytreemanager ftm = Familytreemanager.getInstance();
    private Person thePerson = null;
    private ObjectBinding<Person.Gender> genderBinding;
    private boolean changeOK = false;
    private BooleanProperty enableUpdateProperty;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
          logger.setLevel(Level.FINE);
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);
        try {
            FileHandler fileHandler = new FileHandler();
            // records sent to file javaN.log in user's home directory
            fileHandler.setLevel(Level.FINE);
            logger.addHandler(fileHandler);
            logger.log(Level.FINE, "Created File Handler");
        } catch (IOException | SecurityException ex) {
            logger.log(Level.SEVERE, "Couldn't create FileHandler", ex);
        }

        enableUpdateProperty = new SimpleBooleanProperty(
                  this, "enableUpdate", false);
        //updateButton.disableProperty().bind(enableUpdateProperty.not());

        // the radio button custom binding
        genderBinding = new ObjectBinding<Person.Gender>() {
            {
                super.bind(maleRadioButton.selectedProperty(),
                        femaleRadioButton.selectedProperty(),
                        unknownRadioButton.selectedProperty());
            }

            @Override
            protected Person.Gender computeValue() {
                if (maleRadioButton.isSelected()) {
                    return Person.Gender.MALE;
                } else if (femaleRadioButton.isSelected()) {
                    return Person.Gender.FEMALE;
                } else {
                    return Person.Gender.UNKNOWN;
                }
            }
        };

        buildData();
        TreeItem<Person> rootNode = new TreeItem<>(
                     new Person("People", "", Person.Gender.UNKNOWN));
        buildTreeView(rootNode);
        personTreView.setRoot(rootNode);
        personTreView.getRoot().setExpanded(true);
        personTreView.getSelectionModel().selectedItemProperty().addListener(treeSelectionListener);
                     
    
    }    
     private void buildData() {
        ftm.addPerson(new Person("Malaba", "Mashauri", Person.Gender.MALE));
        ftm.addPerson(new Person("Liz", "Mashauri", Person.Gender.FEMALE));
        ftm.addPerson(new Person("Allan", "Mashauri", Person.Gender.MALE));
        ftm.addPerson(new Person("Sasha", "Simpson", Person.Gender.FEMALE));
        ftm.addPerson(new Person("Maggie", "Simpson", Person.Gender.FEMALE));
        logger.log(Level.FINE, ftm.getAllPeople().toString());
    }

    private void buildTreeView(TreeItem<Person> root) {
        
        ftm.addListener(familyTreeListener);
        
        ftm.getAllPeople().stream().forEach((p) -> {
            root.getChildren().add(new TreeItem<>(p));
        });
    }
     private final ChangeListener<TreeItem<Person>> treeSelectionListener =
            (ov, oldValue, newValue) -> {
        TreeItem<Person> treeItem = newValue;
        logger.log(Level.FINE, "selected item = {0}", treeItem);
        enableUpdateProperty.set(false);
        changeOK = false;
        if (treeItem == null || treeItem.equals(personTreView.getRoot())) {
            clearForm();
            return;
        }
       
        thePerson = new Person(treeItem.getValue());
        logger.log(Level.FINE, "selected person = {0}", thePerson);
        configureEditPanelBindings(thePerson);
        
        switch (thePerson.getGender()) {
            case MALE:
                maleRadioButton.setSelected(true);
                break;
            case FEMALE:
                femaleRadioButton.setSelected(true);
                break;
            default:
                unknownRadioButton.setSelected(true);
                break;
        }
        thePerson.genderProperty().bind(genderBinding);
        changeOK = true;
        };
      private void configureEditPanelBindings(Person p) {
        firstnameTextField.textProperty()
                  .bindBidirectional(p.firstnameProperty());
        middlenameTextField.textProperty()
                  .bindBidirectional(p.middlenameProperty());
        lastnameTextField.textProperty()
                  .bindBidirectional(p.lastnameProperty());
        suffixTextField.textProperty().bindBidirectional(p.suffixProperty());
        notesTextArea.textProperty().bindBidirectional(p.notesProperty());
    }

    private void clearForm() {
        firstnameTextField.setText("");
        middlenameTextField.setText("");
        lastnameTextField.setText("");
        suffixTextField.setText("");
        notesTextArea.setText("");
        maleRadioButton.setSelected(false);
        femaleRadioButton.setSelected(false);
        unknownRadioButton.setSelected(false);
    }
    @FXML
    private void handleKeyAction(KeyEvent event) {
         if (changeOK) {
            enableUpdateProperty.set(true);
         }
    }

    @FXML
    private void genderSelectionAction(ActionEvent event) {
          if (changeOK) {
            enableUpdateProperty.set(true);
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent event) {
           enableUpdateProperty.set(false);
        ftm.updatePerson(thePerson);
    }
      private final MapChangeListener<Long, Person> familyTreeListener =
                                          (change) -> {
      if (Platform.isFxApplicationThread()) {
         logger.log(Level.FINE, "is JavaFX Application Thread");
         updateTree(change);
      }else {
            logger.log(Level.FINE, "Is BACKGROUND Thread");
            Platform.runLater(()-> updateTree(change));
            }
        };
          private void updateTree(MapChangeListener.Change<? extends Long,
                                          ? extends Person> change) {
        if (change.getValueAdded() != null) {
            
         for (TreeItem<Person> node : personTreView.getRoot().getChildren()) {
            if (change.getKey().equals(node.getValue().getId())) {
               
               node.setValue(change.getValueAdded());
               return;
            }
         }
      }
   };
}
