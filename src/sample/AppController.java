package sample;

import javafx.animation.AnimationTimer;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Classes.Habitat;
import sample.Classes.Rabbits.AlbinosRabbit;
import sample.Classes.Rabbits.OdinaryRabbit;
import sample.Classes.Rabbits.Rabbit;
import sample.Classes.StopWatch;
import sample.Classes.Windows.WindowInformation;

public class AppController{
    private Habitat habitat;
    private StopWatch stopWatch;
    private boolean showLog = true;

    private AnimationTimer timer;   // variable timer never locally used
    {
        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //printTime();
                //if(bSimulation)
                {
                    long timeCurrent = System.currentTimeMillis() - timeStartAnimationTimer;    // naming conventions?
                    long step = timeCurrent - timePreviousAnimationTimer;    // spelling - timePreviousAnimationTimer (too verbose?) 
                    if (step >= 100) {
                        timeAnimationTimer = (int) (timeCurrent / 100);
                        habitat.update(timeAnimationTimer);
                        timePreviousAnimationTimer = timeCurrent;            // spelling - see above comment
                        secondsAnimationTimer = timeAnimationTimer - minutesAnimationTimer * 60;    // naming: secondsTimer, minutesTimer....
                        //hibitian.update(seconds);
                        if (secondsAnimationTimer % 60 == 0) {
                            minutesAnimationTimer++;
                            secondsAnimationTimer = 0;
                        }
                    }
                }
            }
        };
    }   // LINES 49-53: we should rename and reorganize variables 
    int timeStartAnimationTimer = 0;
    int timeAnimationTimer = 0;
    long timePreviousAnimationTimer = 0;
    private int secondsAnimationTimer = 0;
    private int minutesAnimationTimer = 0;

    public AppController() throws Exception {
        stopWatch = new StopWatch();
        habitat = new Habitat(Main.controller.getMainPane());
        disableButtons(stopWatch.getStateOfTimer());
    }

    public void appStart() throws Exception { 
        if(stopWatch.getStateOfTimer() == stopWatch.STOP) habitat.removeAll();
        stopWatch.start(Main.controller.getFieldTime(), habitat);
        disableButtons(stopWatch.getStateOfTimer());
        //System.out.println(stopWatch.updateTime());
    }

    public void appPause(){
        stopWatch.pause();
        disableButtons(stopWatch.getStateOfTimer());
    }

    public void appStop() throws Exception {
        stopWatch.pause();
        //showInformationDialog(makeResultLog());
        WindowInformation windows = new WindowInformation("Modal window" ,makeResultLog(),this); // Russian improperly displayed; "Модальеное окно" to English
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public boolean getShowLog(){
        return this.showLog;
    }

    private String makeResultLog(){
        return new String(
                "Total Rabbits: " + Rabbit.countsAllRabbits +
                        ";"+ '\n' +"Ordinary Rabbits: " + OdinaryRabbit.countOdinaryRabbit +    
                        ";"+ '\n' +"Albino Rabbits: " + AlbinosRabbit.countAlbinosRabbit +      
                        ";"+ '\n' +"Time of simulation Min:" + stopWatch.getMinutes() + " Sec: " +stopWatch.getSeconds()       
        );
    }
// Something wrong in this method - examine logic, simple fix. Program does not respond to start/pause correctly.
    public void disableButtons(int stateOfTimer){
        switch (stateOfTimer) {
            case StopWatch.RUNNING: {
                Main.controller.getStartButton().setDisable(true);
                Main.controller.getPauseButton().setDisable(false);
                Main.controller.getStopButton().setDisable(false);
            }
            break;
            case StopWatch.PAUSE: {
                Main.controller.getStartButton().setDisable(false);
                Main.controller.getPauseButton().setDisable(true);
                Main.controller.getStopButton().setDisable(false);
            }
            break;
            case StopWatch.STOP: {
                Main.controller.getStartButton().setDisable(false);
                Main.controller.getPauseButton().setDisable(true);
                Main.controller.getStopButton().setDisable(true);
            }
            break;
        }
    }

    /*При остановке симуляции должно появляться модальное диалоговое окно (при условии, что оно разрешено)
    с информацией о количестве и типе сгенерированных объектов, а также времени симуляции. Вся информация
    выводится в элементе TextArea, недоступном для редактирования. В диалоговом окне должно быть 2 кнопки:
    «ОК» и «Отмена». При нажатии на «ОК» симуляции полностью останавливается, а при нажатии на «Отмена»,
    соответственно продолжается;
    */
            // Jen comment reference 
        /* ENGLISH: When you stop simulation, modal dialogue box appears (if enabled) giving 
        * the amount and the type of objects generated in the time elapsed. All info
         * displayed in TextArea isn't editable.
         * We have 2 buttons in the dialogue box:
             * 1) "Ok": Click to stop simulation
             * 2) "Cancel": Continues simulation
         */
    
    private void showInformationDialog(String messageTextArea){
        Button okButton = new Button("Ok");
        Button cancelButton = new Button("Cancel");
        Stage window = new Stage();

        // Events for buttonClose
        cancelButton.setOnAction(event -> {
            stopWatch.start();
            disableButtons(stopWatch.getStateOfTimer());        // suggestion: rename to getTimerState()
            window.close();
        });
        okButton.setOnAction(event -> {
            stopWatch.stop();
            habitat.removeAll();
            disableButtons(stopWatch.getStateOfTimer());        // how about getTimerState()? see above
            window.close();
        });

        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(350);
        window.setHeight(350);
        window.setTitle("Modal Dialogue Box");  // "Модальное диалоговое окно"

        TextArea textArea = new TextArea(messageTextArea);
        textArea.setPrefColumnCount(15);
        textArea.setPrefRowCount(5);

        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10, textArea, cancelButton,okButton);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root);

        window.setScene(scene);
        window.show();
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public Habitat getHabitat() {
        return habitat;
    }
}

