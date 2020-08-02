package com.quincyapps.assignment002;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GameBoardActivity extends AppCompatActivity {

    CustomView customView;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        toolbar = (Toolbar) findViewById(R.id.toolBarForApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        customView = (CustomView) findViewById(R.id.customView);
    }

    private void resentGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameBoardActivity.this);

        builder.setTitle("Do you want to reset game?");

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customView.resetGame();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.actionMenu:
                Toast.makeText(GameBoardActivity.this, "Will load menu", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.actionReset:
                resentGame();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
