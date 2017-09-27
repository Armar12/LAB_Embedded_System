package ece.course.labassignment_1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int resultNo=1;
    Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

       // Random random = new Random();


        Button btnPicture=(Button) findViewById(R.id.btnThrow);
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resultNo=random.nextInt(6);

                TextView titleName=(TextView) findViewById(R.id.titleName);
                TextView resultName=(TextView) findViewById(R.id.result);
                ImageView ivPicture=(ImageView)findViewById(R.id.ivPicture);

                if (resultNo==0)
                {
                    resultName.setText(R.string.result1);
                    ivPicture.setImageResource(R.drawable.one);
                    resultNo = random.nextInt(6);
                }
                else if (resultNo==1)
                {
                    resultName.setText(R.string.result2);
                    ivPicture.setImageResource(R.drawable.two);
                    resultNo = random.nextInt(6);
                }
                else if (resultNo==2)
                {
                    resultName.setText(R.string.result3);
                    ivPicture.setImageResource(R.drawable.three);
                    resultNo = random.nextInt(6);
                }
                else if (resultNo==3)
                {
                    resultName.setText(R.string.result4);
                    ivPicture.setImageResource(R.drawable.four);
                    resultNo = random.nextInt(6);
                }
                else if (resultNo==4)
                {
                    resultName.setText(R.string.result5);
                    ivPicture.setImageResource(R.drawable.five);
                    resultNo = random.nextInt(6);
                }
                else if (resultNo==5)
                {
                    resultName.setText(R.string.result6);
                    ivPicture.setImageResource(R.drawable.six);
                    resultNo = random.nextInt(6);
                }
            }
        });













    }














    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
