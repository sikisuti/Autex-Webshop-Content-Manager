package org.autex.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NotificationController {
    @FXML private Label lbNotification;

    Task<Void> sleeper;

    private static NotificationController instance;

    public NotificationController() {
        instance = this;
    }

    public static void notify(String notification) {
        instance.lbNotification.setText(notification);
        instance.delayThenHide();
    }

    private void delayThenHide() {
        if (sleeper != null && sleeper.isRunning()) {
            sleeper.cancel();
        }

        sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }

                return null;
            }
        };

        sleeper.setOnSucceeded(workerStateEvent -> lbNotification.setText(null));
        Thread thread = new Thread(sleeper);
        thread.start();
    }
}
