package com.quincyapps.assignment002;



import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;

import androidx.core.content.ContextCompat;

import com.quincyapps.assignment002.Circle;
import com.quincyapps.assignment002.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomView extends LinearLayout implements View.OnClickListener {

    private Paint yellow, red, cyan, green, white, black;
    private Rect square;

    private int cellPerRowColumn, size, cellPerRowColumnX2, position;


    private String[] whiteStartPositions, blackStartPositions;

    public Player player1;
    public Player player2;

    private boolean isGameStateActive, isPlayerMadeSelection, isValidMove, isCapturePlay, isMultipleCapture;
    private String activePlayer, activeCell;
    String[] activeMoveOptions, markedCells, drawOnEmptyCell, captureMoveOptions, multipleCaptureMoveOptions;
    Paint[] drawOnEmptyPaint;

    TextView textViewPlayer1;
    TextView textViewPlayer1ActiveSoldiers;
    TextView textViewPlayer1FallenSoldiers;
    TextView textViewPlayer2;
    TextView textViewPlayer2ActiveSoldiers;
    TextView textViewPlayer2FallenSoldiers;
    TextView textViewPlayer2State;

    Button buttonMenu, buttonReset;

    public CustomView(Context c) {
        super(c);
        init();
    }

    public CustomView(Context c, AttributeSet as) {
        super(c, as);
        init();
    }

    public CustomView(Context c, AttributeSet as, int default_style) {
        super(c, as, default_style);
        init();
    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.game_stats, this, true);
        setOnClickListener(this);

        red = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellow = new Paint(Paint.ANTI_ALIAS_FLAG);
        cyan = new Paint(Paint.ANTI_ALIAS_FLAG);
        green = new Paint(Paint.ANTI_ALIAS_FLAG);
        white = new Paint(Paint.ANTI_ALIAS_FLAG);
        black = new Paint(Paint.ANTI_ALIAS_FLAG);

        red.setColor(Color.RED);
        yellow.setColor(Color.YELLOW);
        cyan.setColor(Color.GRAY);
        green.setColor(Color.GREEN);
        white.setColor(Color.WHITE);
        black.setColor(Color.BLACK);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        size = getMinimumValue(width, height);

        cellPerRowColumn = 8;
        cellPerRowColumnX2 = 2*cellPerRowColumn;
        position = 0;
        blackStartPositions = new String[]{"A2", "A4", "A6", "A8", "B1", "B3", "B5", "B7", "C2", "C4", "C6", "C8"};
        whiteStartPositions = new String[]{"H1", "H3", "H5", "H7", "G2", "G4", "G6", "G8", "F1", "F3", "F5", "F7"};

        createPlayers();

        square = new Rect(-size/cellPerRowColumnX2, -size/cellPerRowColumnX2, size/cellPerRowColumnX2, size/cellPerRowColumnX2);

        //  Initialze game state params
        isGameStateActive = false;
        isPlayerMadeSelection = false;
        isMultipleCapture = false;
        isValidMove = false;
        isCapturePlay = false;
        activePlayer = null;
        activeCell = null;
        activeMoveOptions = new String[]{};
        multipleCaptureMoveOptions = new String[]{};
        captureMoveOptions = new String[]{};
        markedCells = new String[]{};
        drawOnEmptyCell = new String[]{};
        drawOnEmptyPaint = new Paint[]{};

        textViewPlayer1 = (TextView) findViewById(R.id.textViewPlayer1);
        textViewPlayer1ActiveSoldiers = (TextView) findViewById(R.id.textViewPlayer1ActiveSoldiers);
        textViewPlayer1FallenSoldiers = (TextView) findViewById(R.id.textViewPlayer1FallenSoldiers);
        textViewPlayer2 = (TextView) findViewById(R.id.textViewPlayer2);
        textViewPlayer2ActiveSoldiers = (TextView) findViewById(R.id.textViewPlayer2ActiveSoldiers);
        textViewPlayer2FallenSoldiers = (TextView) findViewById(R.id.textViewPlayer2FallenSoldiers);
        textViewPlayer2State = (TextView) findViewById(R.id.textViewPlayer2State);

        startGame();
    }

    public void createPlayers() {
        player1 = new Player(whiteStartPositions);
        player2 = new Player(blackStartPositions);
    }

    public void resetCurrentGame(Player player1, Player player2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Do you want to reset game?");

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createPlayers();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void loadGameStats() {
        textViewPlayer1.setText(player1.getName());
        textViewPlayer1ActiveSoldiers.setText(String.valueOf(player1.getPlayerButton().size()));
        textViewPlayer1FallenSoldiers.setText(String.valueOf(Player.getPlayerTileLength() - player1.getPlayerButton().size()));


        textViewPlayer2.setText(player2.getName());
        textViewPlayer2ActiveSoldiers.setText(String.valueOf(player2.getPlayerButton().size()));
        textViewPlayer2FallenSoldiers.setText(String.valueOf(Player.getPlayerTileLength() - player2.getPlayerButton().size()));

        String gamsStatus = String.format("Active Player\n%s", activePlayer.equals(player1.getName()) ? player1.getName() : player2.getName());

        if (player1.getPlayerButton().size() <= 0) {
            gamsStatus = String.format("Game Over\n%s Wins", player2.getName());
        } else if (player2.getPlayerButton().size() <= 0) {
            gamsStatus = String.format("Game Over\n%s Wins", player1.getName());
        }

        textViewPlayer2State.setText(gamsStatus);
    }

    private void startGame() {
        switchPlayers();
        isGameStateActive = true;
    }

    private void switchToPlayer1() {
        activePlayer = player1.getName();
        resetGameParams();
    }

    private void switchToPlayer2() {
        activePlayer = player2.getName();
        resetGameParams();
    }

    private void switchPlayers() {
        if (activePlayer == null) {
            switchToPlayer1();
            Toast.makeText(getContext(), String.format("%s its your turn", activePlayer), Toast.LENGTH_SHORT).show();
        } else if (activePlayer.equals(player2.getName())) {
            switchToPlayer1();
            Toast.makeText(getContext(), String.format("%s its your turn", activePlayer), Toast.LENGTH_SHORT).show();
        } else if (activePlayer.equals(player1.getName())) {
            switchToPlayer2();
            Toast.makeText(getContext(), String.format("%s its your turn", activePlayer), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetGameParams() {
        isPlayerMadeSelection = false;
        isValidMove = false;
        isCapturePlay = false;
        isMultipleCapture = false;
        activeCell = null;
        activeMoveOptions = new String[]{};
        multipleCaptureMoveOptions = new String[]{};
        markedCells = new String[]{};
        captureMoveOptions = new String[]{};
//        experiment incase error in rendering game board
        drawOnEmptyCell = new String[]{};
        drawOnEmptyPaint = new Paint[]{};
        loadGameStats();
    }

    private int getMinimumValue(int width, int height) {
        return width > height ? height : width;
    }

    private void drawCircle(Canvas canvas, Paint color) {
        canvas.drawCircle(0, 0, 50, color);
    }

    private void drawKing(Canvas canvas, Paint color) {
        canvas.drawCircle(0, 0, 30, color);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        position = 0;

        canvas.translate(-size/cellPerRowColumnX2, size/cellPerRowColumnX2);

        for (int i=0; i<cellPerRowColumn; i++) {
            if (i%2 == 0) {
                for (int j=0; j<cellPerRowColumn; j++) {
                    if (j%2 == 0) {
                        canvas.translate(size/cellPerRowColumn, 0);
                        canvas.drawRect(square, yellow);

                    } else {
                        boolean isDrawn = false, isCaptureDrawn = false;
                        canvas.translate(size/cellPerRowColumn, 0);
                        for (int k=0; k<captureMoveOptions.length; k++) {
                            if (captureMoveOptions[k].equals(get2DimensionalMatricesCoodsFromPostion(position))) {
                                canvas.drawRect(square, cyan);
                                isCaptureDrawn = true;
                            }
                        }

                        if (!isCaptureDrawn) {
                            for (int k=0; k<drawOnEmptyCell.length; k++) {
                                if (drawOnEmptyCell[k].equals(get2DimensionalMatricesCoodsFromPostion(position))) {
                                    canvas.drawRect(square, drawOnEmptyPaint[k]);
                                    isDrawn = true;
                                }
                            }
                            if (!isDrawn) {
                                canvas.drawRect(square, red);
                            }
                        }
                    }

                    for (int k=0; k<player1.getPlayerButton().size(); k++) {
                        if (player1.getPlayerButton().get(k).getCurrentPosition().equals(get2DimensionalMatricesCoodsFromPostion(position))) {
                            drawCircle(canvas, white);
                            if (player1.getPlayerButton().get(k).getIsKing()) {
                                drawKing(canvas, black);
                            }
                        }
                    }

                    for (int k=0; k<player2.getPlayerButton().size(); k++) {
                        if (player2.getPlayerButton().get(k).getCurrentPosition().equals(get2DimensionalMatricesCoodsFromPostion(position))) {
                            drawCircle(canvas, black);
                            if (player2.getPlayerButton().get(k).getIsKing()) {
                                drawKing(canvas, white);
                            }
                        }
                    }

                    position += 1;
                }
            } else {
                for (int j=0; j<cellPerRowColumn; j++) {
                    if (j%2 == 0) {
                        boolean isDrawn = false, isCaptureDrawn = false;
                        canvas.translate(size/cellPerRowColumn, 0);
                        for (int k=0; k<captureMoveOptions.length; k++) {
                            if (captureMoveOptions[k].equals(get2DimensionalMatricesCoodsFromPostion(position))) {
                                canvas.drawRect(square, cyan);
                                isCaptureDrawn = true;
                            }
                        }

                        if (!isCaptureDrawn) {
                            for (int k=0; k<drawOnEmptyCell.length; k++) {
                                if (drawOnEmptyCell[k].equals(get2DimensionalMatricesCoodsFromPostion(position))) {
                                    canvas.drawRect(square, drawOnEmptyPaint[k]);
                                    isDrawn = true;
                                }
                            }
                            if (!isDrawn) {
                                canvas.drawRect(square, red);
                            }
                        }
                    } else {
                        canvas.translate(size/cellPerRowColumn, 0);
                        canvas.drawRect(square, yellow);
                    }

                    for (int k=0; k<player1.getPlayerButton().size(); k++) {
                        if (player1.getPlayerButton().get(k).getCurrentPosition().equals(get2DimensionalMatricesCoodsFromPostion(position))) {
                            drawCircle(canvas, white);
                            if (player1.getPlayerButton().get(k).getIsKing()) {
                                drawKing(canvas, black);
                            }
                        }
                    }

                    for (int k=0; k<player2.getPlayerButton().size(); k++) {
                        if (player2.getPlayerButton().get(k).getCurrentPosition().equals(get2DimensionalMatricesCoodsFromPostion(position))) {
                            drawCircle(canvas, black);
                            if (player2.getPlayerButton().get(k).getIsKing()) {
                                drawKing(canvas, white);
                            }
                        }
                    }

                    position += 1;
                }
            }
            canvas.translate(-size, size/cellPerRowColumn);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float touchX = 0.0f;
        float touchY = 0.0f;

        if (isGameStateActive) {
            touchX = event.getX();
            touchY = event.getY();
            if ((int) event.getY() <= size) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (activeCell != null) {
                        markedCells = addItemToMarkedCells(markedCells.length, markedCells, activeCell);
                    }

                    if (activeMoveOptions.length > 0) {
                        for (int i=0; i<activeMoveOptions.length; i++) {
                            markedCells = addItemToMarkedCells(markedCells.length, markedCells, activeMoveOptions[i]);
                        }
                    }

                    if (activePlayer.equals(player1.getName())) {
                        String selectedCell =   String.format("%s%s",
                                getRowCharacter(computeRowColumnDeterminateOnTouch(size, cellPerRowColumn, touchY)),
                                computeRowColumnDeterminateOnTouch(size, cellPerRowColumn, touchX)
                        );

                        if (isCapturePlay) {
                            boolean isCaptured = false;
                            if (captureMoveOptions.length > 0) {
                                for(int i=0; i<captureMoveOptions.length; i++) {
                                    if (captureMoveOptions[i].equals(selectedCell)) {
                                        isCaptured = captureOponentPlayer(selectedCell);
                                        if (isCaptured) {
                                            clearMarkedCells();
                                            if (isGameStateActive) {
                                                switchPlayers();
                                            }
                                            if (!isGameStateActive) {
                                                resetGameParams();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), String.format("%s, you MUST capture you opp. soldier", player1.getName()), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                invalidate();
                                return true;
                            }
                        }

                        if (isPlayerMadeSelection) {
                            if (activeMoveOptions.length > 0) {
                                for(int i=0; i<activeMoveOptions.length; i++) {
                                    if (activeMoveOptions[i].equals(selectedCell)) {
                                        isValidMove = updatePlayerMoveGamePlay(player1, activeCell, selectedCell);
                                    }
                                }
                            }

                            if (isValidMove) {
                                if (markedCells.length > 0) {
                                    clearMarkedCells();
                                    switchPlayers();
                                }
                                invalidate();
                                return true;
                            } else {
                                clearMarkedCells();
                                resetGameParams();
                            }
                        }


                        if (doesSelectedCircleBelongsToActivePlayer(player1, selectedCell)) {
                            if (!isPlayerMadeSelection) {
                                if (markedCells.length > 0) {
                                    clearMarkedCells();
                                }
                            }

                            activeCell = selectedCell;
                            if (isSelectedCellHoldsKing(player1, selectedCell)) {
                                activeMoveOptions = getUpwardMovementOptiond(selectedCell);
                                String[] temp = getDownwardMovementOptiond(selectedCell);
                                for(int i=0; i<temp.length; i++) {
                                    activeMoveOptions = addItemToMarkedCells(activeMoveOptions.length, activeMoveOptions, temp[i]);
                                }
                            } else {
                                activeMoveOptions = getUpwardMovementOptiond(selectedCell);
                            }

                            for(int i=0; i<activeMoveOptions.length; i++) {
                                if (isPlayer1CaptureMove(selectedCell)) {
                                    isValidMove = true;
                                    isCapturePlay = true;
                                    break;
                                } else if (!doesSelectedCircleBelongsToActivePlayer(player1, activeMoveOptions[i]) && !doesSelectedCircleBelongsToActivePlayer(player2, activeMoveOptions[i])) {
                                    if (!isCapturePlay) {
                                        isValidMove = true;
                                        drawOnEmptyCell = addItemToMarkedCells(drawOnEmptyCell.length, drawOnEmptyCell, activeMoveOptions[i]);
                                        drawOnEmptyPaint = addItemToOnEmptyPaint(drawOnEmptyPaint.length, drawOnEmptyPaint, cyan);
                                    }
                                }
                            }
                            if (isValidMove) {
                                drawOnEmptyCell = addItemToMarkedCells(drawOnEmptyCell.length, drawOnEmptyCell, activeCell);
                                drawOnEmptyPaint = addItemToOnEmptyPaint(drawOnEmptyPaint.length, drawOnEmptyPaint, green);
                                isPlayerMadeSelection = true;
                                isValidMove = false;
                            }
                        } else {
                            if (activeMoveOptions.length > 0) {
                                for(int i=0; i<activeMoveOptions.length; i++) {
                                    if (activeMoveOptions[i].equals(selectedCell)) {
                                        isValidMove = true;
                                        break;
                                    }
                                }
                            }
                            if(!isValidMove) {
                                clearMarkedCells();
                                resetGameParams();
                            }
                            isValidMove = false;
                        }
                    } else if (activePlayer.equals(player2.getName())) {
                        String selectedCell =   String.format("%s%s",
                                getRowCharacter(computeRowColumnDeterminateOnTouch(size, cellPerRowColumn, touchY)),
                                computeRowColumnDeterminateOnTouch(size, cellPerRowColumn, touchX)
                        );

                        if (isCapturePlay) {
                            boolean isCaptured = false;
                            if (captureMoveOptions.length > 0) {
                                for(int i=0; i<captureMoveOptions.length; i++) {
                                    if (captureMoveOptions[i].equals(selectedCell)) {
                                        isCaptured = captureOponentPlayer(selectedCell);
                                        if (isCaptured) {
                                            clearMarkedCells();
                                            if (isGameStateActive) {    switchPlayers();    }
                                            if (!isGameStateActive) {   resetGameParams();  }
                                        } else {
                                            Toast.makeText(getContext(), String.format("%s, you MUST capture you opp. soldier", player2.getName()), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                invalidate();
                                return true;
                            }
                        }

                        if (isPlayerMadeSelection) {
                            if (activeMoveOptions.length > 0) {
                                for(int i=0; i<activeMoveOptions.length; i++) {
                                    if (activeMoveOptions[i].equals(selectedCell)) {
                                        isValidMove = updatePlayerMoveGamePlay(player2, activeCell, selectedCell);
                                    }
                                }
                            }

                            if (isValidMove) {
                                if (markedCells.length > 0) {
                                    clearMarkedCells();
                                    switchPlayers();
                                }
                                invalidate();
                                return true;
                            } else {
                                clearMarkedCells();
                                resetGameParams();
                            }
                        }


                        if (doesSelectedCircleBelongsToActivePlayer(player2, selectedCell)) {
                            if (!isPlayerMadeSelection) {
                                if (markedCells.length > 0) {
                                    clearMarkedCells();
                                }
                            }

                            activeCell = selectedCell;
                            if (isSelectedCellHoldsKing(player2, selectedCell)) {
                                activeMoveOptions = getDownwardMovementOptiond(selectedCell);
                                String[] temp = getUpwardMovementOptiond(selectedCell);
                                for(int i=0; i<temp.length; i++) {
                                    activeMoveOptions = addItemToMarkedCells(activeMoveOptions.length, activeMoveOptions, temp[i]);
                                }
                            } else {
                                activeMoveOptions = getDownwardMovementOptiond(selectedCell);
                            }

                            for(int i=0; i<activeMoveOptions.length; i++) {
                                if (isPlayer2CaptureMove(selectedCell)) {
                                    isValidMove = true;
                                    isCapturePlay = true;
                                    break;
                                } else if (!doesSelectedCircleBelongsToActivePlayer(player1, activeMoveOptions[i]) && !doesSelectedCircleBelongsToActivePlayer(player2, activeMoveOptions[i])) {
                                    if (!isCapturePlay) {
                                        isValidMove = true;
                                        drawOnEmptyCell = addItemToMarkedCells(drawOnEmptyCell.length, drawOnEmptyCell, activeMoveOptions[i]);
                                        drawOnEmptyPaint = addItemToOnEmptyPaint(drawOnEmptyPaint.length, drawOnEmptyPaint, cyan);
                                    }
                                }
                            }
                            if (isValidMove) {
                                drawOnEmptyCell = addItemToMarkedCells(drawOnEmptyCell.length, drawOnEmptyCell, activeCell);
                                drawOnEmptyPaint = addItemToOnEmptyPaint(drawOnEmptyPaint.length, drawOnEmptyPaint, green);
                                isPlayerMadeSelection = true;
                                isValidMove = false;
                            }
                        } else {
                            if (activeMoveOptions.length > 0) {
                                for(int i=0; i<activeMoveOptions.length; i++) {
                                    if (activeMoveOptions[i].equals(selectedCell)) {
                                        isValidMove = true;
                                        break;
                                    }
                                }
                            }
                            if(!isValidMove) {
                                clearMarkedCells();
                                resetGameParams();
                            }
                            isValidMove = false;
                        }
                    }
                }
            }
        }

        invalidate();
        return true;
    }

    private boolean isSelectedCellHoldsKing(Player player, String selectedCell) {
        for (int i=0; i<player.getPlayerButton().size(); i++) {
            if (player.getPlayerButton().get(i).getCurrentPosition().equals(selectedCell) && player.getPlayerButton().get(i).getIsKing()) {
                return true;
            }
        }
        return false;
    }

    private void crownActivePlayerKingOnMove() {
        if (activePlayer.equals(player1.getName())) {
            for (int i=0; i<player1.getPlayerButton().size(); i++) {
                if (getKingMakerRow(activePlayer).contains(player1.getPlayerButton().get(i).getCurrentPosition())) {
                    player1.getPlayerButton().get(i).setIsKing(true);
                }
            }
        } else {
            for (int i=0; i<player2.getPlayerButton().size(); i++) {
                if (getKingMakerRow(activePlayer).contains(player2.getPlayerButton().get(i).getCurrentPosition())) {
                    player2.getPlayerButton().get(i).setIsKing(true);
                }
            }
        }
    }

    private List<String> getKingMakerRow(String activePlayer) {
        String[] kingMakerRow = new String[]{};

        switch(activePlayer) {
            case "Player 1":
                kingMakerRow = new String[]{"A2", "A4", "A6", "A8"};
                break;
            case "Player 2":
                kingMakerRow = new String[]{"H1", "H3", "H5", "H7"};
                break;
            default:
                kingMakerRow = new String[]{};
                break;
        }
        return Arrays.asList(kingMakerRow);
    }

    private boolean captureOponentPlayer(String selectedCell) {
        boolean captured = false;

        if (captureMoveOptions[captureMoveOptions.length-1].equals(selectedCell) || captureMoveOptions[captureMoveOptions.length-2].equals(selectedCell)) {}
        else {  return false;   }

        String[] toBeDeletedCircles = new String[]{};
        String[] upward = new String[]{};  String[] downward = new String[]{}; String[] reverseUpward = new String[]{}; String[] reverseDownward = new String[]{}; String[] tempArr = new String[]{};
        for (int r=0; r<captureMoveOptions.length; r++) {
            if (r == 0) {
                upward = activePlayer.equals(player1.getName()) ? getUpwardMovementOptiond(activeCell) : getDownwardMovementOptiond(activeCell);
            } else {
                upward = activePlayer.equals(player1.getName()) ? getUpwardMovementOptiond(captureMoveOptions[r-1]) : getDownwardMovementOptiond(captureMoveOptions[r-1]);
            }
            downward = activePlayer.equals(player1.getName()) ? getDownwardMovementOptiond(captureMoveOptions[r]) : getUpwardMovementOptiond(captureMoveOptions[r]);

            Player player = activePlayer.equals(player1.getName()) ? player2 : player1;
            Player currentPlayer = activePlayer.equals(player1.getName()) ? player1 : player2;

            boolean isKing = false;
            for (int i=0; i<currentPlayer.getPlayerButton().size(); i++) {
                if (r == 0) {
                    if (currentPlayer.getPlayerButton().get(i).getCurrentPosition().equals(activeCell)) {
                        if (currentPlayer.getPlayerButton().get(i).getIsKing()) {
                            upward = getUpwardMovementOptiond(activeCell);
                            tempArr = getDownwardMovementOptiond(activeCell);
                            for (int j=0; j<tempArr.length; j++) {
                                upward = addItemToMarkedCells(upward.length, upward, tempArr[j]);
                            }
                            isKing = true;
                        }
                    }
                } else {
                    if (currentPlayer.getPlayerButton().get(i).getCurrentPosition().equals(captureMoveOptions[r-1])) {
                        if (currentPlayer.getPlayerButton().get(i).getIsKing()) {
                            upward = getUpwardMovementOptiond(captureMoveOptions[r-1]);
                            tempArr = getDownwardMovementOptiond(captureMoveOptions[r-1]);
                            for (int j=0; j<tempArr.length; j++) {
                                upward = addItemToMarkedCells(upward.length, upward, tempArr[j]);
                            }
                            isKing = true;
                        }
                    }
                }

            }

            if (isKing) {
                downward = getUpwardMovementOptiond(captureMoveOptions[r]);
                tempArr = getDownwardMovementOptiond(captureMoveOptions[r]);
                for (int j=0; j<tempArr.length; j++) {
                    downward = addItemToMarkedCells(downward.length, downward, tempArr[j]);
                }
            }

            for (int i=0; i<upward.length; i++) {
                for (int j=0; j<downward.length; j++) {
                    if (upward[i].equals(downward[j])) {
                        String capturedCell = upward[i];
                        toBeDeletedCircles = addItemToMarkedCells(toBeDeletedCircles.length, toBeDeletedCircles, capturedCell);
                    }
                }
            }

            if (r > 0) {
                reverseUpward = activePlayer.equals(player1.getName()) ?  getDownwardMovementOptiond(captureMoveOptions[r-1]): getUpwardMovementOptiond(captureMoveOptions[r-1]);
                reverseDownward = activePlayer.equals(player1.getName()) ? getUpwardMovementOptiond(captureMoveOptions[r]): getDownwardMovementOptiond(captureMoveOptions[r]);

                for (int i=0; i<reverseUpward.length; i++) {
                    for (int j=0; j<reverseDownward.length; j++) {
                        if (reverseUpward[i].equals(reverseDownward[j])) {
                            String capturedCell = reverseUpward[i];
                            toBeDeletedCircles = addItemToMarkedCells(toBeDeletedCircles.length, toBeDeletedCircles, capturedCell);
                        }
                    }
                }
            }

            if (r == (captureMoveOptions.length-1)) {
                int targetIndex = getItemIndexFromArray(captureMoveOptions, selectedCell);

                for (int i=0; i<=targetIndex; i++) {
                    boolean isDeleted = false;
                    for (int j=0; j<toBeDeletedCircles.length; j++) {
                        String[] updownMoves = getDownwardMovementOptiond(captureMoveOptions[i]);
                        String[] tempUp = getUpwardMovementOptiond(captureMoveOptions[i]);
                        for (int k=0; k<tempUp.length; k++) {
                            updownMoves = addItemToMarkedCells(updownMoves.length, updownMoves, tempUp[k]);
                        }

                        for (int k=0; k<updownMoves.length; k++) {
                            if (updownMoves[k].equals(toBeDeletedCircles[j])) {
                                for (int l=0; l<player.getPlayerButton().size(); l++) {
                                    if (player.getPlayerButton().get(l).getCurrentPosition().equals(toBeDeletedCircles[j])) {
                                        player.getPlayerButton().remove(l);
                                        isDeleted = true;
                                        break;
                                    }
                                }
                            }
                            if (isDeleted) {
                                break;
                            }
                        }
                        if (isDeleted) {
                            break;
                        }
                    }
                }
                captured = updatePlayerMoveGamePlay(currentPlayer, activeCell, captureMoveOptions[r].equals(selectedCell) ? captureMoveOptions[r] : captureMoveOptions[r-1]);
                if (player1.getPlayerButton().size() <= 0) {
                    isGameStateActive = false;
                } else if (player2.getPlayerButton().size() <= 0) {
                    isGameStateActive = false;
                }
                if (targetIndex != (captureMoveOptions.length-1)) {
//                    return false;
                }
            }
        }
        return captured;
    }

    private int getItemIndexFromArray(String arr[], String item)
    {
        if (arr == null) {  return -1;  }   int len = arr.length;int i = 0;
        while (i < len) {
            if (arr[i].equals(item)) {   return i;   }
            else {  i = i + 1;  }
        }
        return -1;
    }

    private boolean isPlayer1CaptureMove(String selectedCell) {
        boolean itIsCaptureMove = false;
        if (activeMoveOptions.length > 0) {
            for (int i=0; i<activeMoveOptions.length; i++) {
                for (int j=0; j<player2.getPlayerButton().size(); j++) {
                    if (player2.getPlayerButton().get(j).getCurrentPosition().equals(activeMoveOptions[i])) {
                        String[] temp = getUpwardMovementOptiond(activeMoveOptions[i]);
                        for (int k=0; k<player1.getPlayerButton().size(); k++) {
                            if (player1.getPlayerButton().get(k).getCurrentPosition().equals(selectedCell)) {
                                if (player1.getPlayerButton().get(k).getIsKing()) {
                                    String[] temp2 = getDownwardMovementOptiond(activeMoveOptions[i]);
                                    for (int l=0; l<temp2.length; l++) {
                                        temp = addItemToMarkedCells(temp.length, temp, temp2[l]);
                                    }
                                }
                            }
                        }
                        for (int k=0; k<temp.length; k++) {
                            if (
                                    !doesSelectedCircleBelongsToActivePlayer(player1, temp[k]) &&
                                            !doesSelectedCircleBelongsToActivePlayer(player2, temp[k])
                            ) {
                                String[] diagonals = getDiagonalRelationShip(activeCell);
                                for (int z=0; z<diagonals.length; z++) {
                                    if (diagonals[z].equals(temp[k])) {
                                        itIsCaptureMove = true;
                                        captureMoveOptions = addItemToMarkedCells(captureMoveOptions.length, captureMoveOptions, temp[k]);
                                        if (isMultipleCaptureMove(player2, temp[k])) {
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return itIsCaptureMove;
    }


    private boolean isMultipleCaptureMove(Player opponent, String selectedCell) {
        boolean itIsCaptureMove = false;
        multipleCaptureMoveOptions = getDownwardMovementOptiond(selectedCell);
        String[] temp = getUpwardMovementOptiond(selectedCell);
        for (int k=0; k<temp.length; k++) {
            multipleCaptureMoveOptions = addItemToMarkedCells(multipleCaptureMoveOptions.length, multipleCaptureMoveOptions, temp[k]);
        }

        for (int i=0; i<multipleCaptureMoveOptions.length; i++) {
            for (int j=0; j<opponent.getPlayerButton().size(); j++) {
                if (opponent.getPlayerButton().get(j).getCurrentPosition().equals(multipleCaptureMoveOptions[i])) {
                    String[] temp2 = getUpwardMovementOptiond(multipleCaptureMoveOptions[i]);
                    String[] temp3 = getDownwardMovementOptiond(multipleCaptureMoveOptions[i]);
                    for (int k=0; k<temp3.length; k++) {
                        temp2 = addItemToMarkedCells(temp2.length, temp2, temp3[k]);
                    }
                    for (int k=0; k<temp2.length; k++) {
                        if (
                                !doesSelectedCircleBelongsToActivePlayer(player1, temp2[k]) &&
                                        !doesSelectedCircleBelongsToActivePlayer(player2, temp2[k])
                        ) {
                            String[] diagonals = getDiagonalRelationShip(selectedCell);
                            for (int z=0; z<diagonals.length; z++) {
                                if (diagonals[z].equals(temp2[k])) {
                                    itIsCaptureMove = true;
                                    isMultipleCapture = true;
                                    captureMoveOptions = addItemToMarkedCells(captureMoveOptions.length, captureMoveOptions, temp2[k]);
                                }
                            }
                        }
                    }
                }
            }
        }

        return itIsCaptureMove;
    }

    private boolean isPlayer2CaptureMove(String selectedCell) {
        boolean itIsCaptureMove = false;
        if (activeMoveOptions.length > 0) {
            for (int i=0; i<activeMoveOptions.length; i++) {
                for (int j=0; j<player1.getPlayerButton().size(); j++) {
                    if (player1.getPlayerButton().get(j).getCurrentPosition().equals(activeMoveOptions[i])) {
                        String[] temp = getDownwardMovementOptiond(activeMoveOptions[i]);
                        for (int k=0; k<player2.getPlayerButton().size(); k++) {
                            if (player2.getPlayerButton().get(k).getCurrentPosition().equals(selectedCell)) {
                                if (player2.getPlayerButton().get(k).getIsKing()) {
                                    String[] temp2 = getUpwardMovementOptiond(activeMoveOptions[i]);
                                    for (int l=0; l<temp2.length; l++) {
                                        temp = addItemToMarkedCells(temp.length, temp, temp2[l]);
                                    }
                                }
                            }
                        }
                        for (int k=0; k<temp.length; k++) {
                            if (
                                    !doesSelectedCircleBelongsToActivePlayer(player1, temp[k]) &&
                                            !doesSelectedCircleBelongsToActivePlayer(player2, temp[k])
                            ) {
                                String[] diagonals = getDiagonalRelationShip(activeCell);
                                for (int z=0; z<diagonals.length; z++) {
                                    if (diagonals[z].equals(temp[k])) {
                                        itIsCaptureMove = true;
                                        captureMoveOptions = addItemToMarkedCells(captureMoveOptions.length, captureMoveOptions, temp[k]);
                                        if (isMultipleCaptureMove(player1, temp[k])) {
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return itIsCaptureMove;
    }


    private String[] getDiagonalRelationShip(String activeCell) {
        char[] rowColChar = activeCell.toCharArray();
        char[] rowOptions = null, colOptions = null;

        String[] diagonals = new String[]{};

        switch (rowColChar[0]){
            case 'A':
                rowOptions = new char[]{'C'};
                break;
            case 'B':
                rowOptions = new char[]{'D'};
                break;
            case 'C':
                rowOptions = new char[]{'A', 'E'};
                break;
            case 'D':
                rowOptions = new char[]{'B', 'F'};
                break;
            case 'E':
                rowOptions = new char[]{'C', 'G'};
                break;
            case 'F':
                rowOptions = new char[]{'D', 'H'};
                break;
            case 'G':
                rowOptions = new char[]{'E'};
                break;
            case 'H':
                rowOptions = new char[]{'F'};
                break;
        }

        switch (rowColChar[1]){
            case '1':
                colOptions = new char[]{'3'};
                break;
            case '2':
                colOptions = new char[]{'4'};
                break;
            case '3':
                colOptions = new char[]{'1', '5'};
                break;
            case '4':
                colOptions = new char[]{'2', '6'};
                break;
            case '5':
                colOptions = new char[]{'3', '7'};
                break;
            case '6':
                colOptions = new char[]{'4', '8'};
                break;
            case '7':
                colOptions = new char[]{'5'};
                break;
            case '8':
                colOptions = new char[]{'6'};
                break;
        }

        for (int i=0; i<rowOptions.length; i++) {
            for (int j=0; j<colOptions.length; j++) {
                diagonals = addItemToMarkedCells(diagonals.length, diagonals, String.format("%s%s", rowOptions[i], colOptions[j]));
            }
        }

        return diagonals;
    }

    private boolean doesSelectedCircleBelongsToActivePlayer(Player player, String selectedCell) {
        boolean itDoesBelongToActivePlayer = false;
        for (int i=0; i<player.getPlayerButton().size(); i++) {
            if (player.getPlayerButton().get(i).getCurrentPosition().equals(selectedCell)) {
                itDoesBelongToActivePlayer = true;
                break;
            }
        }
        return itDoesBelongToActivePlayer;
    }

    private boolean updatePlayerMoveGamePlay(Player player, String currentCellCoords, String destinationCellCoords) {
        for (int k=0; k<player.getPlayerButton().size(); k++) {
            if (player.getPlayerButton().get(k).getCurrentPosition().equals(currentCellCoords)) {
                Player opponent = player.getId() == player1.getId() ? player2 : player1;
                for (int j = 0; j < opponent.getPlayerButton().size(); j++) {
                    if (opponent.getPlayerButton().get(j).getCurrentPosition().equals(destinationCellCoords)) {
                        return false;
                    }
                }

                for (int j = 0; j < player.getPlayerButton().size(); j++) {
                    if (player.getPlayerButton().get(j).getCurrentPosition().equals(destinationCellCoords)) {
                        return false;
                    }
                }
                player.getPlayerButton().get(k).setCurrentPosition(destinationCellCoords);
                crownActivePlayerKingOnMove();
                return true;
            }
        }
        return false;
    }

    public static String[] addItemToMarkedCells(int n, String[] markedCells, String value){
        int i;
        String[] newMarkedCell = new String[n+1];
        for (i=0; i<n; i++) {
            newMarkedCell[i] = markedCells[i];
        }
        newMarkedCell[n] = value;
        return newMarkedCell;
    }

    public static Paint[] addItemToOnEmptyPaint(int n, Paint[] onEmptyPaintCollection, Paint value){
        int i;
        Paint[] newonEmptyPaintCollection = new Paint[n+1];
        for (i=0; i<n; i++) {
            newonEmptyPaintCollection[i] = onEmptyPaintCollection[i];
        }
        newonEmptyPaintCollection[n] = value;
        return newonEmptyPaintCollection;
    }

    public void clearMarkedCells(){
        for (int i=0; i<markedCells.length; i++) {
            drawOnEmptyCell = addItemToMarkedCells(drawOnEmptyCell.length, drawOnEmptyCell, markedCells[i]);
            drawOnEmptyPaint = addItemToOnEmptyPaint(drawOnEmptyPaint.length, drawOnEmptyPaint, red);
        }
    }

    private String get2DimensionalMatricesCoodsFromPostion(int position) {
        String cellPostion = null;

        if (position <= 7) {
            cellPostion = String.format("A%s", (position % 8) + 1);
        } else if (position > 7 && position <= 15) {
            cellPostion = String.format("B%s", (position % 8) + 1);
        } else if (position > 15 && position <= 23) {
            cellPostion = String.format("C%s", (position % 8) + 1);
        } else if (position > 23 && position <= 31) {
            cellPostion = String.format("D%s", (position % 8) + 1);
        } else if (position > 31 && position <= 39) {
            cellPostion = String.format("E%s", (position % 8) + 1);
        } else if (position > 39 && position <= 47) {
            cellPostion = String.format("F%s", (position % 8) + 1);
        } else if (position > 47 && position <= 55) {
            cellPostion = String.format("G%s", (position % 8) + 1);
        } else if (position > 55 && position <= 63) {
            cellPostion = String.format("H%s", (position % 8) + 1);
        }

        return cellPostion;
    }

    private char getRowCharacter(int row) {
        char[] rowChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        return rowChar[row-1];
    }

    private int computeRowColumnDeterminateOnTouch(int size, int cellPerRowColumn, float targetedCoords) {
        int start = 1,
                squareLength = (int) (size/cellPerRowColumn),
                end = squareLength,
                rowColumn = 0;

        for (int i=0; i < cellPerRowColumn; i++) {
            if (start <= ((int) targetedCoords) && ((int) targetedCoords) <= end) {
                rowColumn = i;
                break;
            }
            start += squareLength;
            end += squareLength;
        }
        return rowColumn + 1;
    }

    private String[] getDownwardMovementOptiond(String cellCoords) {
        String[] moveOptions;
        char[] strChar = cellCoords.toCharArray();

        switch(strChar[0]) {
            case 'G':
                moveOptions = getMoveOptions('H', strChar[1]);
                break;
            case 'F':
                moveOptions = getMoveOptions('G', strChar[1]);
                break;
            case 'E':
                moveOptions = getMoveOptions('F', strChar[1]);
                break;
            case 'D':
                moveOptions = getMoveOptions('E', strChar[1]);
                break;
            case 'C':
                moveOptions = getMoveOptions('D', strChar[1]);
                break;
            case 'B':
                moveOptions = getMoveOptions('C', strChar[1]);
                break;
            case 'A':
                moveOptions = getMoveOptions('B', strChar[1]);
                break;
            default:
                moveOptions = new String[]{};
                break;
        }
        return moveOptions;
    }

    private String[] getMoveOptions(char cellRowChar, char cellColumnChar) {
        switch (cellColumnChar) {
            case '1':
                return new String[]{cellRowChar+"2"};
            case '2':
                return new String[]{cellRowChar+"1", cellRowChar+"3"};
            case '3':
                return new String[]{cellRowChar+"2", cellRowChar+"4"};
            case '4':
                return new String[]{cellRowChar+"3", cellRowChar+"5"};
            case '5':
                return new String[]{cellRowChar+"4", cellRowChar+"6"};
            case '6':
                return new String[]{cellRowChar+"5", cellRowChar+"7"};
            case '7':
                return new String[]{cellRowChar+"6", cellRowChar+"8"};
            case '8':
                return new String[]{cellRowChar+"7"};
            default:
                return new String[]{};
        }
    }

    private String[] getUpwardMovementOptiond (String cellCoords) {
        String[] moveOptions;
        char[] strChar = cellCoords.toCharArray();

        switch(strChar[0]) {
            case 'H':
                moveOptions = getMoveOptions('G', strChar[1]);
                break;
            case 'G':
                moveOptions = getMoveOptions('F', strChar[1]);
                break;
            case 'F':
                moveOptions = getMoveOptions('E', strChar[1]);
                break;
            case 'E':
                moveOptions = getMoveOptions('D', strChar[1]);
                break;
            case 'D':
                moveOptions = getMoveOptions('C', strChar[1]);
                break;
            case 'C':
                moveOptions = getMoveOptions('B', strChar[1]);
                break;
            case 'B':
                moveOptions = getMoveOptions('A', strChar[1]);
                break;
            default:
                moveOptions = new String[]{};
                break;
        }
        return moveOptions;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    public void resetGame() {
        player1 = new Player(whiteStartPositions);
        player2 = new Player(blackStartPositions);
        loadGameStats();
        invalidate();
    }
}
