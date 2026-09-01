package com.marksman.controller;

import com.marksman.entity.Arrow;
import com.marksman.entity.Target;
import com.marksman.view.GameField;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameViewController {

    @FXML private Label pointValue;
    @FXML private Label shotValue;
    @FXML private Label recordValue;
    @FXML private Label timeValue;

    @FXML private Button startBtn;
    @FXML private Button pauseBtn;
    @FXML private Button shotBtn;

    @FXML private GameField gameField;

    private GameController gameController;
    private Target nearTarget;
    private Target farTarget;
    private int boomTimer = 0;
    private int flickerTimer = 0;
    private Target boomTarget = null;
    private boolean respawnFar = false;
    private Arrow arrow;
    private AnimationTimer gameLoop;
    private boolean flag = false;

    @FXML
    public void initialize() {
        gameController = new GameController();

        // если есть сохранённая игра — восстанавливаем UI
        if (gameController.getState().getPaused()) {
            pauseBtn.setText("Продолжить");
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameField.render();
                gameField.drawPlayer();

                // рисуем мишени с эффектом мерцания при появлении
                if (nearTarget != null) {
                    if (flickerTimer <= 0 || respawnFar || flickerTimer % 4 < 2) {
                        gameField.drawTarget(nearTarget);
                    }
                }
                if (farTarget != null) {
                    if (flickerTimer <= 0 || !respawnFar || flickerTimer % 4 < 2) {
                        gameField.drawTarget(farTarget);
                    }
                }

                if (arrow != null) gameField.drawArrow(arrow);

                getUI();

                // завершение игры по таймеру
                if (gameController.getTimeLeft() <= 0 && flag) {
                    endGameByTime();
                    return;
                }

                // отображаем бум и запускаем появление мишени после него
                if (boomTimer > 0) {
                    gameField.drawBoom(boomTarget);
                    boomTimer--;
                    if (boomTimer == 0) {
                        if (respawnFar) {
                            farTarget = new Target(700, 300, 60, 6, true);
                            farTarget.start();
                        } else {
                            nearTarget = new Target(500, 300, 120, 3, false);
                            nearTarget.start();
                        }
                        flickerTimer = 30;
                    }
                }

                if (flickerTimer > 0) {
                    flickerTimer--;
                }

                // проверки попаданий
                if (arrow != null) {
                    if (nearTarget != null && nearTarget.isHit(arrow)) {
                        gameController.addPoint(1);
                        arrow.stopArrow();
                        arrow = null;
                        nearTarget.toDoBoom(nearTarget.getxPos(), nearTarget.getyPos());
                        boomTarget = nearTarget;
                        boomTimer = 30;
                        respawnFar = false;
                        nearTarget = null;

                    } else if (farTarget != null && farTarget.isHit(arrow)) {
                        gameController.addPoint(2);
                        arrow.stopArrow();
                        arrow = null;
                        farTarget.toDoBoom(farTarget.getxPos(), farTarget.getyPos());
                        boomTarget = farTarget;
                        boomTimer = 30;
                        respawnFar = true;
                        farTarget = null;
                    }
                }
            }
        };

        gameLoop.start();
        gameField.render();
    }

    @FXML
    private void onStartClick() {
        flag = true;

        gameController.startGame();
        onCreateTerget();

        pauseBtn.setText("Пауза");

        getUI();
    }

    @FXML
    private void onPauseClick() {

        // ИГРА ИДЕТ \ СТАВИМ НА ПАУЗУ
        if (gameController.getState().getActive()) {

            flag = false;

            if (nearTarget != null) nearTarget.stopTarget();
            if (farTarget != null) farTarget.stopTarget();
            if (arrow != null) arrow.stopArrow();

            gameController.stopGame();

            pauseBtn.setText("Продолжить");
        }

        // ИГРА НА ПАУЗЕ \ ПРОДОЛЖАЕМ (включая загрузку из меню)
        else if (gameController.getState().getPaused()) {

            flag = true;

            gameController.resumeGame();
            onCreateTerget();

            pauseBtn.setText("Пауза");
        }

        getUI();
    }

    @FXML
    private void onShotClick() {
        if (flag) {
            gameController.addShot();

            if (arrow != null) arrow.stopArrow();

            arrow = new Arrow(100, 280, 70, 10);
            arrow.start();

            getUI();
        }
    }

    private void getUI() {
        pointValue.setText(String.valueOf(gameController.getState().getPoint()));
        shotValue.setText(String.valueOf(gameController.getState().getShot()));
        recordValue.setText(String.valueOf(gameController.getState().getRecordPoint()));
        timeValue.setText(String.valueOf(gameController.getTimeLeft()));
    }

    private void onCreateTerget() {
        if (nearTarget != null) nearTarget.stopTarget();
        if (farTarget != null) farTarget.stopTarget();

        this.nearTarget = new Target(500, 300, 120, 3, false);
        this.farTarget = new Target(700, 300, 60, 6, true);

        nearTarget.start();
        farTarget.start();
    }

    private void endGameByTime() {
        flag = false;

        if (nearTarget != null) nearTarget.stopTarget();
        if (farTarget != null) farTarget.stopTarget();
        if (arrow != null) arrow.stopArrow();

        nearTarget = null;
        farTarget = null;
        arrow = null;

        gameController.finishGame();

        pauseBtn.setText("Пауза");

        getUI();

        System.out.println("Game Over (time)");
    }
}