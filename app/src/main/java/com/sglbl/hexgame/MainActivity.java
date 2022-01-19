package com.sglbl.hexgame;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.widget.Toast;

@SuppressWarnings("UnusedAssignment")
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private int boardSize = 8;
    private int level = 1;
    private char turn = 'x'; // Whose turn.
    private int how_many_player = 1;
    private AppCompatButton[][] buttons = new AppCompatButton[boardSize][boardSize];
    private AppCompatButton oldMove,oldMove2;
    private TextView label;
    private Button reset, undo, load, save;

    //MAIN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent(); //for info transferring between activities.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            boardSize = Integer.parseInt(intent.getStringExtra(Main.EXTRA_NUMB));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        if( intent.getStringExtra(Main.EXTRA_TEXT).equals("One Player Game"))
            how_many_player = 1;
        else if(intent.getStringExtra(Main.EXTRA_TEXT).equals("Two Players Game") )
            how_many_player = 2;

        label = (TextView)findViewById(R.id.text);

        addIDs();
    } //End of onCreate function.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i;
        i = getMenuInflater();
        i.inflate(R.menu.dots_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete){
            if( deleteFile("hexgame.txt") ) {
                Toast.makeText(this, "File succesfully deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
            else
                Toast.makeText(this, "There is no file already", Toast.LENGTH_SHORT).show();
        }

        if(item.getItemId() == R.id.difficulty) {
            level = 1;
            if(how_many_player==1) Toast.makeText(this, "Difficulty set to easy", Toast.LENGTH_SHORT).show();
            if(how_many_player==2) Toast.makeText(this, "Game type isn't 1 player game", Toast.LENGTH_SHORT).show();

        }

        if(item.getItemId() == R.id.difficulty2) {
            level = 2;
            if(how_many_player==1) Toast.makeText(this, "Difficulty set to medium", Toast.LENGTH_SHORT).show();
            if(how_many_player==2) Toast.makeText(this, "Game type isn't 1 player game", Toast.LENGTH_SHORT).show();
        }

        else if(item.getItemId() == R.id.testAll){
            int[][] visitedplaces;

            Toast.makeText(getApplicationContext(), "Playing 2 players",Toast.LENGTH_SHORT).show();
            play(2,3);
            change_turn();
            play();
            Toast.makeText(getApplicationContext(), "Checking if game ended.",Toast.LENGTH_SHORT).show();
            System.out.println( isEnd() );

            visitedplaces = new int[getBoardSize()][getBoardSize()];
            for(int i=0; i<getBoardSize(); i++)
                for(int j=0; j<getBoardSize(); j++)
                    visitedplaces[i][j] = 0;

            int x = 2, y = 3, s=0;
            Toast.makeText(getApplicationContext(),"Checking if board is full and return 2 for 'x' won and 3 for 'o' won, 0 for No one won.",Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), Integer.toString(check_full(x, y, visitedplaces, 'x', s) ),Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Checking if x:"+x+ " y:" +y + " is valid for moving" ,Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), Boolean.toString(is_valid_move(2,3,'x')) ,Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Writing to File and Reading from File",Toast.LENGTH_SHORT).show();
            writeToFile("custom.txt");
            readFromFile("custom.txt");
            Toast.makeText(getApplicationContext(), "Changing color of a button",Toast.LENGTH_SHORT).show();
            if(buttons[2][3].getText() == ".") changeColor(2,3, R.color.red);

        }


        return super.onOptionsItemSelected(item);
    }


    public void addIDs(){
        String btn;
        int buttonIdCode;
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                btn = "b" + (i+1) + (j+1);
                buttonIdCode = getResources().getIdentifier(btn, "id", getPackageName());
                buttons[i][j] = ((AppCompatButton) findViewById(buttonIdCode));
                buttons[i][j].setOnClickListener(this);
                buttons[i][j].setText(".");
                buttons[i][j].setTextColor( ContextCompat.getColor(MainActivity.this, android.R.color.transparent ) );
                changeColor(i,j, 1);
            }
        } //End of for loop

        oldMove = (AppCompatButton)findViewById(R.id.oldMove);
        oldMove2 = (AppCompatButton)findViewById(R.id.oldMove2);

        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)
                if(i>=boardSize || j>=boardSize ) //If boardsize==7 or boardsize==8 make visible that row and column.
                    buttons[i][j].setVisibility(View.GONE);

        label = (TextView)findViewById(R.id.text);
        reset = (Button)findViewById(R.id.bReset);
        undo = (Button)findViewById(R.id.bUndo);
        load = (Button)findViewById(R.id.bLoad);
        save = (Button)findViewById(R.id.bSave);
        fourButtonClick();

    }   //End of setOnClick method

    public void fourButtonClick(){
        reset.setOnClickListener(new View.OnClickListener() {
            GradientDrawable background_without_border;
            @Override
            public void onClick(View v) {   //When RESET clicked
                for(int i=0; i<boardSize; i++)
                    for(int j=0; j<boardSize; j++){
                        background_without_border = (GradientDrawable) buttons[i][j].getBackground();
                        background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.gray) );
                        buttons[i][j].setText(".");
                    }
                turn = 'x';
                label.setText(turn + "'s turn");
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            GradientDrawable background_without_border, background_without_border2;
            @Override
            public void onClick(View v) {   //When UNDO clicked
                if(oldMove.getText() != "."){
                    oldMove.setText(".");
                    background_without_border = (GradientDrawable) oldMove.getBackground();
                    background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.gray) );
                    oldMove2.setText(".");
                    if(how_many_player == 1) {
                        background_without_border2 = (GradientDrawable) oldMove2.getBackground();
                        background_without_border2.setColor(ContextCompat.getColor(MainActivity.this, R.color.gray));
                        turn = change_turn();
                    }
                    turn = change_turn();
                    label.setText(turn + "'s turn");
                }
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFromFile("hexgame.txt");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile("hexgame.txt");     //Writing board to a file called hexgame.txt
            }
        });

    }

    @Override
    public void onClick(View v) {
        for(int i=0; i<boardSize; i++){
            for(int j=0; j<boardSize; j++){
                if( v.getId() == buttons[i][j].getId() ){
                    if(turn == 'x'){        //x playing
                        if(buttons[i][j].getText().equals(".")){
                            if(how_many_player == 2 && isEnd() == 0)
                                play(i,j); //User plays if 2 players game.
                            else if(how_many_player == 1 && isEnd() == 0){
                                play(i,j); //User plays if 1 player game.
                                if(isEnd() != 0) return;
                                play();    //Computer plays if 1 player game.
                            }
                        }
                    }
                    else{       //o playing in 2 players game.
                        if(buttons[i][j].getText().equals(".") && isEnd() == 0 ){
                            play(i,j);
                        }
                    }
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void play(int row, int column) { // Play of computer
        GradientDrawable background_without_border;
        label.setText(change_turn() + "'s turn"); // x played, sp show that it's turn of o
        if (turn == 'x') {
            background_without_border = (GradientDrawable) buttons[row][column].getBackground();
            background_without_border.setColor(ContextCompat.getColor(MainActivity.this, R.color.red));
            buttons[row][column].setText("x");
        }
        else if (turn == 'o') {
            background_without_border = (GradientDrawable) buttons[row][column].getBackground();
            background_without_border.setColor(ContextCompat.getColor(MainActivity.this, R.color.blue));
            buttons[row][column].setText("o");
        }
        buttons[row][column].setText(Character.toString(turn));
        oldMove = buttons[row][column];
        if (isEnd() != 0) {
            if (turn == 'x')
                label.setText("X WON - Game over"); // Using html for new line in JLabel
            if (turn == 'o')
                label.setText("O WON Game over"); // Using html for new line in JLabel
            return;
        }
        turn = change_turn();
    }

    @SuppressLint("SetTextI18n")
    public void play() { // Play of computer
        GradientDrawable background_without_border;
        int moveVal = 0;
        Move bestMove = new Move();
        int maxVal =  -10000;
        for(int j=0; j<boardSize; j++){
            for(int i=0; i<boardSize; i++){
                if( buttons[i][j].getText().equals(".") ){
                    buttons[i][j].setText("o");
                    if(isEnd() == 3){
                        bestMove.setAll(i, j, "o");
                        buttons[bestMove.getRow()][bestMove.getColumn()].setText("o");
                        background_without_border = (GradientDrawable) buttons[bestMove.getRow()][bestMove.getColumn()].getBackground();
                        background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.blue) );
                        oldMove2 = buttons[bestMove.getRow()][bestMove.getColumn()]; // Put this value for Undo.
                        turn = change_turn();
                        label.setText("O WON - Game over");
                        return;
                    }
                    moveVal = minimax(level, false); //score
                    buttons[i][j].setText(".");
                    if(moveVal > maxVal){
                        maxVal = moveVal;
                        bestMove.setAll(i, j, "o");
                    }
                }
            }
        }
        buttons[bestMove.getRow()][bestMove.getColumn()].setText("o");
        background_without_border = (GradientDrawable) buttons[bestMove.getRow()][bestMove.getColumn()].getBackground();
        background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.blue) );

        oldMove2 = buttons[bestMove.getRow()][bestMove.getColumn()]; // Put this value for Undo.

        label.setText("x's turn"); // o played, sp show that it's turn of x
        turn = change_turn();
        if (isEnd() != 0)
            label.setText("O WON - Game over");
    }

    public boolean is_board_full(){
        for(int i=0; i<boardSize; i++)
            for(int j=0; j<boardSize; j++)
                if( buttons[i][j].getText().equals(".") )
                    return false;
        return true;
    }

    public int minimax(int depth, boolean isMaximizing){
        int score = 0;

        if(depth == 0 || isEnd() != 0){
            return minimaxScore(isMaximizing);
        }

        if( is_board_full() ){ //If game ends
            return 0;
        }

        if(isMaximizing){   //maximizing
            int bestScore = -10000;
            for(int i=0; i<boardSize; i++){
                for(int j=0; j<boardSize; j++){
                    if(buttons[i][j].getText().equals(".") ){
                        buttons[i][j].setText("o");
                        score = minimax(depth-1, !isMaximizing);
                        bestScore= max(bestScore,score);
                        buttons[i][j].setText(".");
                    }
                }
            }
            return bestScore;
        }else{                       //minimizing
            int worstScore = 10000;
            for(int i=0; i<boardSize; i++){
                for(int j=0; j<boardSize; j++){
                    if(buttons[i][j].getText().equals(".") ){
                        buttons[i][j].setText("x");
                        score = minimax(depth-1, !isMaximizing);
                        worstScore = min(worstScore, score);
                        buttons[i][j].setText(".");
                    }
                }
            }
            return worstScore;
        }

    }

    public boolean isInInterval(int i,int j){
        if( i>-1 && j>-1)
            if(i<boardSize && j<boardSize)
                return true;
        return false;
    }

    public int min(int score,int bestScore){
        if(score <= bestScore) return score;
        else return bestScore;
    }

    public int max(int score,int bestScore){
        if(score >= bestScore) return score;
        else return bestScore;
    }

    public int minimaxScore(boolean isMaximizing){
         int SCORE_POWER_1 = 3;
         int SCORE_POWER_2 = 8;
         int bridgeScore =0, score1=0, score2=0;

        for(int i=0; i<boardSize; i++)
            for(int j=0; j<boardSize; j++) {
                    if ( isInInterval(i, j) && !isMaximizing && buttons[i][j].getText() == "x" && how_Surrounded(i, j, 'x') >= 1) {
                        if (isInInterval(i - 1, j + 2) && buttons[i - 1][j + 2].getText().equals("x"))
                            bridgeScore++;
                        if (isInInterval(i + 1, j - 2)  && buttons[i + 1][j - 2].getText().equals("x"))
                            bridgeScore++;
                        if (isInInterval(i - 1, j - 1)  && buttons[i - 1][j - 1].getText().equals("x"))
                            bridgeScore++;
                        if (isInInterval(i + 2, j - 1)  && buttons[i + 2][j - 1].getText().equals("x"))
                            bridgeScore++;
                        if (isInInterval(i + 1, j + 1)  && buttons[i + 1][j + 1].getText().equals("x"))
                            bridgeScore++;
                    }
                    else if( isInInterval(i, j) && buttons[i][j].getText() == "o" && how_Surrounded(i, j, 'o') >= 2 ) {
                        if( isInInterval(i-1,j+2)  && buttons[i-1][ j+2 ].getText().equals( "o" ) )
                            bridgeScore++;
                        if( isInInterval(i+1,j-2)  && buttons[i+1][ j-2 ].getText().equals( "o" ) )
                            bridgeScore++;
                        if( isInInterval(i-1,j-1)  && buttons[i-1][ j-1 ].getText().equals( "o" ) )
                            bridgeScore++;
                        if( isInInterval(i+2,j-1)  && buttons[i+2][ j-1 ].getText().equals( "o" ) )
                            bridgeScore++;
                        if( isInInterval(i+1,j+1) && buttons[i+1][ j+1 ].getText().equals( "o" ) )
                            bridgeScore++;
                    }


                if(buttons[i][j].getText().equals("o") ) {
                    boolean flag1= false, flag2=false, flag3=false, flag4 = false;
                    if( isInInterval(i+1,j) )
                        if (buttons[i + 1][j].getText().equals("o") ) {
                            flag1 = true;
                            if(i == boardSize -3 ) score2 +=50;
                            score2++;
                        }
                        else if (buttons[i + 1][j].getText().equals(".") ) {
                            if(isEnd() == 3)    return 250;
                        }

                    if( isInInterval(i-1,j) )
                        if(buttons[i - 1][j].getText().equals("o")) {
                            flag2 = true;
                            if(i == 2 ) score2 +=50;
                            score2++;
                        }

                    if( flag1 || flag2 ) {
                        if(i < boardSize - 2)
                            if (buttons[i + 2][j].getText().equals("o") && flag1) {
                                score2 += 50;
                                flag3 = true;
                            }

                        if(i >= 2)
                            if (buttons[i - 2][j].getText().equals("o") && flag2) {
                                score2 += 50;
                                flag4 = true;
                            }

                        if(i < boardSize-2 && i >= 2)
                            if( buttons[i + 2][j].getText().equals("o") || buttons[i - 2][j].getText().equals("o"))
                                score2 += 10;

                        if     ( isMaximizing && buttons[i][j].getText().equals("o") && isInInterval(i+1,j) && isInInterval(i-1,j) && ( buttons[i+1][j].getText().equals("o") || buttons[i-1][j].getText().equals("o") ) )   score1++;
                        else if(!isMaximizing && buttons[i][j].getText().equals("x") && isInInterval(i,j+1) && isInInterval(i,j-1) && ( buttons[i][j+1].getText().equals("x") || buttons[i][j-1].getText().equals("x") ) )   score1++;


                    }

                }


            }

            if( isEnd() == 2) // x won
                return -250;
            if( isEnd() == 3) // o won
                return 250;

            if(isMaximizing)
                return score2*SCORE_POWER_1 + bridgeScore*SCORE_POWER_2 + score1*3; //if maximizing return positive
            return -(score2*SCORE_POWER_1 + bridgeScore*SCORE_POWER_2 + score1*3);  //if minimizing return negative


    }

    public int how_Surrounded(int i, int j, char letter){  //Total cells that surrounds.
        int counter=0;
        char letter2 = 'o';
        if(letter == 'x') letter2='o';
        if(letter == 'o') letter2='x';
        if(letter == 'x'){
            if( isInInterval(i,j+1)  && buttons[i][ j+1 ].getText().equals( Character.toString(letter2) ) ) counter++;
            if( isInInterval(i,j-1)  && buttons[i][ j-1 ].getText().equals( Character.toString(letter2) ) ) counter++;
            if( isInInterval(i-1,j+1) && buttons[i-1][j+1].getText().equals( Character.toString(letter2) ) ) counter++;
        }
        else if(letter == 'o'){
            if( isInInterval(i-1,j)  && buttons[i-1][ j ].getText().equals( Character.toString(letter2) ) ) counter++;
            if( isInInterval(i+1,j)  && buttons[i+1][ j ].getText().equals( Character.toString(letter2) ) ) counter++;
            if( isInInterval(i+1,j-1) && buttons[i+1][j-1].getText().equals( Character.toString(letter2) ) ) counter++;
        }
        return counter;
    }

    public char change_turn(){
        char flag = 'x';
        if(turn=='x')	flag = 'o';
        return flag;
    }

    public int isEnd(){
        int[][] visitedplaces = new int[9][9];
        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                visitedplaces[i][j] = 0;
        int score = 0;
        for(int m=0; m<boardSize; m++){
            //Checking x if wom
            for(int i=0; i<boardSize; i++)
                for(int j=0; j<boardSize; j++)
                    visitedplaces[i][j]=0;
            if(buttons[m][0].getText().equals("x") || buttons[m][0].getText().equals("X"))
                if( check_full(0, m, visitedplaces, 'x', score) == 2)	return 2;

            //Checking o if won
            for(int i=0; i<boardSize; i++)
                for(int j=0; j<boardSize; j++)
                    visitedplaces[i][j]=0;

            if(buttons[0][m].getText().equals("o") || buttons[0][m].getText().equals("O"))
                if( check_full(m, 0, visitedplaces, 'o', score) == 2)	return 3;
        }

        return 0;
    }

    public int getBoardSize(){
        return boardSize;
    }

    public int check_full(int x, int y, int[][] visitedplaces, char check, int score){
        /* If visitedplaces[y][x] == 1, then it means it's visited ;
           If visitedplaces[x][y] == 0, then it means it's not visited yet.
        ---
            case 1:	x2=x+1;	y2=y-1;		//upright
            case 2:	x2=x+1;	y2=y;		//right
            case 3:	x2=x;	y2=y+1;		//downright
            case 4:	x2=x-1;	y2=y+1;		//downleft
            case 5:	x2=x-1;	y2=y;		//left
            case 6:	x2=x;	y2=y-1;		//upleft
        */
        if(check=='x' && x == boardSize-1 ){
            for(int m=0; m<boardSize; m++)
                if (visitedplaces[m][boardSize-1] == 0 && buttons[m][boardSize-1].getText().equals("x") )
                { score=100; return 2; }
        }

        if(check=='o' && y == boardSize-1 ){
            for(int m=0; m<boardSize; m++)
                if (visitedplaces[boardSize-1][m] == 0 && buttons[boardSize-1][m].getText().equals("o")  )
                { 	return 2; }
        }

        else{
            //Recursively finding if it wins with Backtrack technique.
            if( y>=1 && x>=0 )
                if(visitedplaces[y-1][x+1]==0 && is_valid_move(x + 1, y - 1, check)){
                    visitedplaces[y][x] = 1;
                    score+=5;
                    if (check_full(x+1, y-1, visitedplaces, check, score) != 0) return 2;
                }
            if( y>=0 && x>=0 )
                if(visitedplaces[y][x+1]==0 && is_valid_move(x + 1, y, check)){
                    visitedplaces[y][x] = 1;
                    score+=5;
                    if (check_full(x+1, y, visitedplaces, check, score) != 0) return 2;
                }
            if( y>=0 && x>=0 )
                if(visitedplaces[y+1][x]==0 && is_valid_move(x, y + 1, check)){
                    visitedplaces[y][x] = 1;
                    score+=5;
                    if (check_full(x, y+1, visitedplaces, check, score) != 0) return 2;
                }
            if( y>=0 && x>=1 )
                if(visitedplaces[y+1][x-1]==0 && is_valid_move(x - 1, y + 1, check)){
                    score+=5;
                    visitedplaces[y][x] = 1;
                    if (check_full(x-1, y+1, visitedplaces, check, score) != 0) return 2;
                }
            if( y>=0 && x>=1 )
                if(visitedplaces[y][x-1]==0 && is_valid_move(x - 1, y, check)){
                    visitedplaces[y][x] = 1;
                    score+=5;
                    if (check_full(x-1, y, visitedplaces, check, score) != 0) return 2;
                }
            if( y>=1 && x>=0 )
                if(visitedplaces[y-1][x]==0 && is_valid_move(x, y - 1, check)){
                    visitedplaces[y][x] = 1;
                    score+=5;
                    if (check_full(x, y-1, visitedplaces, check, score) != 0) return 2;
                }

            visitedplaces[y][x]=0;
        }
        return 0;
    }

    public void writeToFile(String file){   //Writing to file
        int y=-1, x=-1, y2=-1, x2=-1;
        BufferedWriter bufferString;
        try {
            bufferString= new BufferedWriter( new FileWriter(getFilesDir() + "/" + file) );
            System.out.println(getFilesDir());
            bufferString.write( Integer.toString(boardSize) );      /*Writing boardSize */ bufferString.write("\n");
            bufferString.write( Integer.toString(how_many_player) ); /*Writing how many player game */ bufferString.write("\n");
            bufferString.write( Character.toString(turn) );   /*Writing whose turn */ bufferString.write("\n");

            for(int i=0; i<boardSize; i++){ //Writing cell states
                for(int j=0; j<boardSize; j++){
                    bufferString.write((String) buttons[i][j].getText());
                }
                bufferString.write("\n");
            }

            for(int i=0; i<boardSize; i++){ //Writing old move
                for(int j=0; j<boardSize; j++){
                    if(oldMove == buttons[i][j] ){
                        y = i;
                        x = j;
                    }
                    if(how_many_player == 1 && oldMove2 == buttons[i][j] ){
                        y2 = i;
                        x2 = j;
                    }
                }
            }

            bufferString.write( Character.toString((char)(x + 65)) + (y+1) + oldMove.getText() );   //Writing old move(For ex:  A1x)
            bufferString.write("\n");
            if(how_many_player==1)
                bufferString.write( Character.toString((char)(x2 + 65)) + (y2+1) + oldMove2.getText() );   //Writing old move2(computers move too)
            Toast.makeText(getApplicationContext(),"File saved to the device.",Toast.LENGTH_SHORT).show();
            bufferString.close();
        }  //End of try
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error in opening file, make sure file permission is granted.",Toast.LENGTH_SHORT).show();
        }   //End of catch
    }  //End of method

    public void readFromFile(String file){
        GradientDrawable background_without_border;
        String readedLine = ".";
        BufferedReader bufferString;
        int oldboardSize = boardSize;
        try {
            bufferString= new BufferedReader( new FileReader(getFilesDir() + "/" + file) );
            readedLine = bufferString.readLine();   //Reading boardSize
            if     (readedLine.length() == 1)    boardSize = (int)readedLine.charAt(0) - 48;
            else if(readedLine.length() == 2)    boardSize = ((int)readedLine.charAt(0) - 48)*10 + ((int)readedLine.charAt(1) -48);

            readedLine = bufferString.readLine();   //Reading how many player game
            how_many_player = (int)readedLine.charAt(0)-48;

            readedLine = bufferString.readLine();   //Reading whose turn
            turn = readedLine.charAt(0);

            if(oldboardSize > boardSize){   //Removing from screen the unnecessary buttons.
                for (int i = 0; i < oldboardSize; i++)
                    for (int j = 0; j < oldboardSize; j++) {
                        buttons[i][j].setText(".");
                        changeColor(i, j, 1); //gray
                        if (i >= boardSize || j >= boardSize) {
                            buttons[i][j].setVisibility(View.GONE); //Making invisible / removing from screen.
                        }
                    }
            }

            for (int i = 0; i < boardSize; i++)            //RESET all the button cells.
                for (int j = 0; j < boardSize; j++)
                    if(j>=oldboardSize || i>=oldboardSize)
                        buttons[i][j].setVisibility(View.VISIBLE);


            for(int i=0; i<boardSize; i++){  //Reading board cells
                readedLine = bufferString.readLine();
                for(int j=0; j<boardSize; j++){
                    buttons[i][j].setText( Character.toString( readedLine.charAt(j) ) );

                    if     (buttons[i][j].getText().equals("x")) changeColor(i,j, 2); //set red
                    else if(buttons[i][j].getText().equals("o")) changeColor(i,j, 3); //set blue
                    else if(buttons[i][j].getText().equals(".")) changeColor(i,j, 1); //set gray
                }
            }

            readedLine = bufferString.readLine();   //Reading oldMove
            oldMove = buttons[ (int)readedLine.charAt(1) -49][ (int)readedLine.charAt(0) - 65];
            oldMove.setText( Character.toString(readedLine.charAt(2)) );
            background_without_border = (GradientDrawable) oldMove.getBackground();
            if     ( Character.toString(readedLine.charAt(2)).equals("x")) background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.red) ); //red
            else if( Character.toString(readedLine.charAt(2)).equals("o")) background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.blue) ); //blue

            if(how_many_player == 1){   //Reading oldMove2 for 1 player game(both computer and user)
                readedLine = bufferString.readLine();
                oldMove2 = buttons[ (int)readedLine.charAt(1) -49][ (int)readedLine.charAt(0) - 65];
                oldMove2.setText( Character.toString(readedLine.charAt(2)) );
                background_without_border = (GradientDrawable) oldMove2.getBackground();
                if     ( Character.toString(readedLine.charAt(2)).equals("x")) background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.red) ); //red
                else if( Character.toString(readedLine.charAt(2)).equals("o")) background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.blue) ); //blue
            }
            bufferString.close();
            Toast.makeText(getApplicationContext(),"File loaded from device.",Toast.LENGTH_SHORT).show();
        }  //End of try
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error in reading file, make sure file permission is granted and board was saved.",Toast.LENGTH_SHORT).show();
        }   //End of catch
    }      //End of method


    public boolean is_valid_move(int x, int y, char letter){
        if( x>=0 && y>=0 && x<boardSize && y<boardSize && (buttons[y][x].getText().equals( Character.toString(letter) )
                || buttons[y][x].getText().equals( Character.toString((char) (letter-32)) ) ) )
            return true;

        return false;
    }

    public void changeColor(int i, int j, int color){
        // color=> 1 for gray, 2 for red, 3 for blue
        GradientDrawable background_without_border;
        background_without_border = (GradientDrawable) buttons[i][j].getBackground();
        if(color==1) background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.gray) );
        else if(color==2) background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.red) );
        else if(color==3) background_without_border.setColor( ContextCompat.getColor(MainActivity.this, R.color.blue) );
    }


} //End of class