package com.sglbl.hexgame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Main extends AppCompatActivity {
    private Button startButton;
    private RadioGroup rGroup, rGroup2;
    private RadioButton rbutton, rbutton2;
    public static final String EXTRA_TEXT = "com.sglbl.hexgame.transfer.EXTRA_TEXT";
    public static final String EXTRA_NUMB = "com.sglbl.hexgame.transfer.EXTRA_NUMB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        rGroup  = findViewById(R.id.rGroup);
        rGroup2 = findViewById(R.id.rGroup2);

        startButton = (Button) findViewById(R.id.startGameButton);
        startButton.setOnClickListener(v -> openSecondPage());
    }

    public void checkHowManyPlayer(View v){
        rbutton = findViewById( rGroup.getCheckedRadioButtonId() );
        Toast.makeText(getApplicationContext(),"Selected player type: " + rbutton.getText() ,Toast.LENGTH_SHORT).show();
    }

    public void checkSize(View v){
        rbutton2 = findViewById( rGroup2.getCheckedRadioButtonId() );
        Toast.makeText(getApplicationContext(),"Selected size: " + rbutton2.getText(),Toast.LENGTH_SHORT).show();
    }

    public void openSecondPage(){
        Intent i; //This is for opening game.
        i = new Intent(this, MainActivity.class);

        rbutton = findViewById( rGroup.getCheckedRadioButtonId() );
        i.putExtra(EXTRA_TEXT, rbutton.getText()  );
        rbutton2 = findViewById( rGroup2.getCheckedRadioButtonId() );
        i.putExtra(EXTRA_NUMB, rbutton2.getText() );
        startActivity(i);
    }


}