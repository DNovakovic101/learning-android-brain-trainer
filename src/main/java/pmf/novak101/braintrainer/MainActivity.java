package pmf.novak101.braintrainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayerBackground;
    MediaPlayer clickRight;
    MediaPlayer clickWrong;
    ValueAnimator colorAnimationRight;
    ValueAnimator colorAnimationWrong;

    LinearLayout upperInfo;
    GridLayout options;
    ConstraintLayout endGameInfo;
    ConstraintLayout layoutMain;

    Button startGame;
    Button playAgain;

    TextView timer;
    TextView score;
    TextView mathProblem;
    TextView textViewGameDone;

    TextView option1;
    TextView option2;
    TextView option3;
    TextView option4;

    int totalQuestions = 0;
    int rightQuestions = 0;

    int rightOption;
    int a;
    int b;
    ArrayList<TextView> buttons = new ArrayList<TextView>();
    ArrayList<Integer> finalOptions = new ArrayList<Integer>();

    public void populateGameState() {
        a = (int)(Math.random() * 50 + 8);
        b = (int)(Math.random() * 50 + 8);
        rightOption = a + b;
        finalOptions.clear();

        int option2int = generateRandomWrongAnswer(rightOption,10);
        int option3int = generateRandomWrongAnswer(rightOption,5);
        int option4int = generateRandomWrongAnswer(rightOption,20);


        Collections.addAll(finalOptions, rightOption, option2int, option3int, option4int);
        Collections.shuffle(finalOptions);

        mathProblem.setText(a + " + " + b);

        setTextAndTagForTextView(option1, 0);
        setTextAndTagForTextView(option2, 1);
        setTextAndTagForTextView(option3, 2);
        setTextAndTagForTextView(option4, 3);

    }

    public void setTextAndTagForTextView(TextView tv, int arrayPosition){
        tv.setText(String.valueOf(finalOptions.get(arrayPosition)));
        tv.setTag(finalOptions.get(arrayPosition));
    }

    public int generateRandomWrongAnswer(int rightAnswer, int proximity){
        int temp = (int) (Math.random() * rightAnswer+proximity + rightAnswer-proximity);
        while(temp == rightAnswer)
            temp = (int) (Math.random() * rightAnswer+proximity + rightAnswer-proximity);
        return temp;
    }

    public int calculateScore(){
        int wrongQuestions = totalQuestions-rightQuestions;
        return (int) (rightQuestions*(50 +(rightQuestions*1.5)) - (wrongQuestions*(20+wrongQuestions*1.25)));
    }

    public void startGame(View view){
        startGame.setVisibility(View.INVISIBLE);
        clickRight.seekTo(0);
        clickRight.start();
        upperInfo.setVisibility(View.VISIBLE);
        options.setVisibility(View.VISIBLE);
        toggleButtons(true);

        populateGameState();
        timer.setText("30");
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf( ((int) millisUntilFinished) / 1000 ));
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(),"Score: "+rightQuestions + "/"+totalQuestions, Toast.LENGTH_SHORT ).show();
                MediaPlayer.create(getApplicationContext(), R.raw.congrats).start();

                textViewGameDone.setText("Score: "+ calculateScore()  );
                endGameInfo.setVisibility(View.VISIBLE);
                toggleButtons(false);
            }
        }.start();


    }

    public void playAgain(View view){
        endGameInfo.setVisibility(View.INVISIBLE);
        rightQuestions = 0;
        totalQuestions = 0;
        updateScore();
        clickRight.seekTo(0);
        clickRight.start();
        startGame(null);
    }

    public void pickAnswer(View view) {

        int pickedAnswer =  (int) view.getTag();
        if(pickedAnswer == rightOption){
            clickRight.seekTo(0);
            clickRight.start();

            rightQuestions++;
            totalQuestions++;
            colorAnimationRight.start();
        }else{
            clickWrong.seekTo(0);
            clickWrong.start();
            totalQuestions++;
            colorAnimationWrong.start();
        }
        updateScore();

        populateGameState();

    }



    public void updateScore(){
        score.setText(rightQuestions + "/"+totalQuestions);
    }

    public void toggleButtons(boolean on ){
        for(TextView tv : buttons){
            tv.setEnabled(on);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upperInfo = (LinearLayout) findViewById(R.id.layoutLinearUpperInfo);
        options = (GridLayout) findViewById(R.id.layoutGridOptions);
        endGameInfo = (ConstraintLayout) findViewById(R.id.layoutConstraintEndGame);
        layoutMain = (ConstraintLayout) findViewById(R.id.layoutMain);

        upperInfo.setVisibility(View.INVISIBLE);
        options.setVisibility(View.INVISIBLE);
        endGameInfo.setVisibility(View.INVISIBLE);

        startGame = (Button) findViewById(R.id.startButton);
        playAgain = (Button) findViewById(R.id.buttonPlayAgain);

        timer = (TextView) findViewById(R.id.textViewTimer);
        score = (TextView) findViewById(R.id.textViewSolvedCounter);
        mathProblem = (TextView) findViewById(R.id.textViewMathProblem);
        textViewGameDone = (TextView) findViewById(R.id.textViewGameDone);

        option1 = (TextView) findViewById(R.id.textViewFirstOption);
        option2 = (TextView) findViewById(R.id.textViewSecondOption);
        option3 = (TextView) findViewById(R.id.textViewThirdOption);
        option4 = (TextView) findViewById(R.id.textViewFourthOption);

        Collections.addAll(buttons, option1,option2,option3, option4);

        mediaPlayerBackground = MediaPlayer.create(getApplicationContext(), R.raw.background);
        mediaPlayerBackground.setLooping(true);
        mediaPlayerBackground.start();

        clickRight = MediaPlayer.create(getApplicationContext(), R.raw.right);
        clickWrong = MediaPlayer.create(getApplicationContext(), R.raw.wrong);

        int colorFrom = ContextCompat.getColor(this, R.color.white);
        int colorToRight = ContextCompat.getColor(this, R.color.right);
        int colorToWrong = ContextCompat.getColor(this, R.color.wrong);

        colorAnimationRight = ValueAnimator.ofArgb(colorFrom, colorToRight,colorFrom);
        colorAnimationRight.setDuration(250); // milliseconds
        colorAnimationRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                layoutMain.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

        colorAnimationWrong = ValueAnimator.ofArgb(colorFrom, colorToWrong,colorFrom);
        colorAnimationWrong.setDuration(250); // milliseconds
        colorAnimationWrong.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                layoutMain.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });


}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerBackground.release();
        mediaPlayerBackground = null;
    }
}