package io.jawware.noty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.google.firebase.FirebaseException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class Dashboard extends AppCompatActivity implements TextToSpeech.OnInitListener,AIListener {

    private ProgressDialog mProgressDialog;
    private String url;
    private LinearLayout bottomDrop;

    DatabaseReference mdat;





    private String TAG = "notyme";


    private String userID;
    private DatabaseReference mDatabase;
    private TextView userId, speechText;
    private Button speakIt;
    private ImageButton sendInput;
    private FirebaseAuth mAuth;
    private Intent mSpeechIntent;
    private SpeechRecognizer mSpeechRecognizer;
    private TextToSpeech tts;
    private EditText editInput;
    private AIService aiService;
    RecognitionProgressView recognitionProgressView;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //legel commands
    private static final String[] VALID_COMMANDS = {
            "notty",
            "Who are you?",
            "What is your name",
            "I like cricket",
            "Noty what about share market",
            "Noty show me cricket updates",
            "Noty show me share market updates",
            "Web Search",
            "log out",
            "twitter top hashtag",
            "I need twitter updates"

    };

    private static final int VALID_COMMANDS_SIZE = VALID_COMMANDS.length;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);





        if (ContextCompat.checkSelfPermission(Dashboard.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Dashboard.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    200);

        }

        mDatabase = FirebaseDatabase.getInstance().getReference("notyme");

        userId = (TextView) findViewById(R.id.userid);
        speechText = (TextView) findViewById(R.id.speechtext);


        sendInput = (ImageButton) findViewById(R.id.sendinput);
        editInput = (EditText) findViewById(R.id.userinput);

        mAuth = FirebaseAuth.getInstance();



        AIConfiguration config = new AIConfiguration("4c3bc9828ad94eb496dd441b3052660e",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();


        tts = new TextToSpeech(this, this);
//        speechInput();

        recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);

        recognitionProgressView.setVisibility(View.INVISIBLE);

        bottomDrop = (LinearLayout)findViewById(R.id.backdrop);

        bottomDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);

                builder.setTitle("Choose an option");
                builder.setMessage("User Id:"+userId.getText().toString());
                        // Set the action buttons
                        builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                startActivity(new Intent(Dashboard.this,LogIn.class));
                       }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                        builder.create();
                        builder.show();
            }

        });
        userId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDrop.callOnClick();
            }
        });




//        setting colors to google recognizer animation
        int[] colors = {
                ContextCompat.getColor(Dashboard.this, R.color.white),
                ContextCompat.getColor(Dashboard.this, R.color.white),
                ContextCompat.getColor(Dashboard.this, R.color.white),
                ContextCompat.getColor(Dashboard.this, R.color.white),
                ContextCompat.getColor(Dashboard.this, R.color.white)
        };
        recognitionProgressView.setColors(colors);





        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = mAuth.getCurrentUser().getUid();
            userId.setText(uid);
        } else {
            String uid = "User is not signed in!";
            userId.setText(uid);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Dashboard.this,LogIn.class));
                }
            },3000);
        }

        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                Log.d(TAG, "on results");
                ArrayList<String> matches = null;
                if (results != null) {
                    matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null) {
                        Log.d(TAG, "results are " + matches.toString());
                        final ArrayList<String> matchesStrings = matches;
                        processCommand(matchesStrings);
                    }
                }
            }
        });
        sendInput.setImageResource(R.drawable.ic_keyboard_voice_black_24dp);

        editInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInput.setImageResource(R.drawable.ic_send_black_24dp);
            }
        });

        sendInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                recognitionProgressView.setVisibility(View.VISIBLE);
                mSpeechRecognizer.startListening(mSpeechIntent);
                speechText.setVisibility(View.INVISIBLE);
                recognitionProgressView.play();
                return true;
            }
        });

        sendInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userinput = editInput.getText().toString();


                if (userinput.equals("hi noty")||userinput.equals("Hi Noty")||userinput.equals("hinoty")) {
                    getResponse(0);
                } else if ((userinput.equals("Who are you?"))||(userinput.equals("who are you"))||(userinput.equals("who r u"))) {
                    getResponse(1);
                } else if (userinput.equals("What is your name?")) {
                    getResponse(2);
                } else if (userinput.equals("I like cricket")) {
                    getResponse(3);
                } else if (userinput.equals("What about share market")) {
                    getResponse(4);
                } else if (userinput.equals("Show me cricket news")) {
                    getResponse(5);
                } else if (userinput.equals("Show me share market updates")) {
                    getResponse(6);
                } else if (userinput.equals("Noty web search")) {
                    getResponse(7);
                }else if(userinput.equals("twitter top hastags")|| userinput.equals("twitter updates")) {
                    getResponse(9);
                }else if((userinput.equals("log out")||(userinput.equals("log me out")))){
        getResponse(8);
                } else{
                    aiRequest.setQuery(userinput);


                    if (userinput != null) {



                        new AsyncTask<AIRequest, Void, AIResponse>() {

                            @Override
                            protected AIResponse doInBackground(AIRequest... aiRequests) {
                                final AIRequest request = aiRequests[0];
                                try {
                                    final AIResponse response = aiDataService.request(aiRequest);
                                    return response;
                                } catch (AIServiceException e) {
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(AIResponse aiResponse) {
                                super.onPostExecute(aiResponse);

                                if (aiResponse != null) {

                                    Result result = aiResponse.getResult();
                                    String reply = result.getFulfillment().getSpeech();
                                    speechText.setText(reply);
                                    tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null);

                                }
                            }

                        }.execute(aiRequest);
                    } else {
                        editInput.setError("Message is Empty");
                    }
                }
                editInput.setText("");
                sendInput.setImageResource(R.drawable.ic_keyboard_voice_black_24dp);
            }
        });
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }







    private String getResponse(int command){

        String retString =  "I'm sorry, Not Possible for me";
        userID = mAuth.getCurrentUser().getUid();
        speechText.setVisibility(View.VISIBLE);
        recognitionProgressView.setVisibility(View.INVISIBLE);
        switch (command) {
            case 0:
                    recognitionProgressView.stop();
                    mSpeechRecognizer.stopListening();
                    retString = "Hello Sir! I'm Noty!";
                    speechText.setText(retString);
                    tts.speak(retString, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case 1:
                recognitionProgressView.stop();
                mSpeechRecognizer.stopListening();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                retString= "I'm NotyBot a virtual assistant helps you by notify" ;
                speechText.setText(retString);
                tts.speak(retString, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case 2:
                recognitionProgressView.stop();
                mSpeechRecognizer.stopListening();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                retString = "You can call me Noty";
                speechText.setText(retString);
                tts.speak(retString, TextToSpeech.QUEUE_FLUSH, null);
                break;

            case 3:
                recognitionProgressView.stop();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                mSpeechRecognizer.stopListening();
                retString = "Cricket subscription triggered";
                speechText.setText(retString);
                tts.speak(retString, TextToSpeech.QUEUE_FLUSH, null);
                mDatabase.child(userID).child("cricket").child("subscription").setValue("true");
                mDatabase.child(userID).child("cricket").child("url").setValue("https://timesofindia.indiatimes.com/sports/cricket");
                break;

            case 4:
                recognitionProgressView.stop();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                mSpeechRecognizer.stopListening();
                retString ="Share Market subscription triggered";
                speechText.setText(retString);
                tts.speak(retString, TextToSpeech.QUEUE_FLUSH, null);
                mDatabase.child(userID).child("share_market").child("subscription").setValue("true");
                mDatabase.child(userID).child("share_market").child("url").setValue("https://timesofindia.indiatimes.com/business/india-business/markets");
                break;

            case 5:
                recognitionProgressView.stop();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                mSpeechRecognizer.stopListening();
                retString = "Fetching you cricket updates!";
                speechText.setText(retString);
                tts.speak(retString,TextToSpeech.QUEUE_FLUSH,null);

                mDatabase.child(userID).child("cricket").child("url").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String fetchURL = dataSnapshot.getValue(String.class);
//                        Toast.makeText(Dashboard.this, fetchURL, Toast.LENGTH_SHORT).show();

                        if(fetchURL == null){
                            startActivity(new Intent(Dashboard.this,Error.class));
                        }else{
                            url = fetchURL;
                        }
                        if(url.equals("https://timesofindia.indiatimes.com/sports/cricket")){
                            Intent intent = new Intent(Dashboard.this,Main2Activity.class);
                            intent.putExtra("REQUEST_URL_FROM_SERVER", url);
                            startActivity(intent);
                        }else{
                            startActivity(new Intent(Dashboard.this,Error.class));
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Dashboard.this, "Error in database query", Toast.LENGTH_SHORT).show();

                    }
                });


                break;

            case 6:
                recognitionProgressView.stop();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                mSpeechRecognizer.stopListening();
                retString = "Fetching you share market updates!";
                speechText.setText(retString);
                tts.speak(retString,TextToSpeech.QUEUE_FLUSH,null);

                mDatabase.child(userID).child("share_market").child("url").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String fetchURL = dataSnapshot.getValue(String.class);
//                        Toast.makeText(Dashboard.this, fetchURL, Toast.LENGTH_SHORT).show();
                        if(fetchURL == null){
                            startActivity(new Intent(Dashboard.this,Error.class));
                        }else{
                            url = fetchURL;
                        }

                        if(url.equals("https://timesofindia.indiatimes.com/business/india-business/markets")){
                            Intent intent = new Intent(Dashboard.this,MainActivity.class);
                            intent.putExtra("REQUEST_URL_FROM_SERVER", url);
                            startActivity(intent);
                        }else{
                            startActivity(new Intent(Dashboard.this,Error.class));
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Dashboard.this, "Error in database query", Toast.LENGTH_SHORT).show();

                    }
                });
                break;

            case 7:
                recognitionProgressView.stop();
                mSpeechRecognizer.stopListening();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                askSpeechInput();
                break;

            case 8:
                recognitionProgressView.stop();
                mSpeechRecognizer.stopListening();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                retString="Logging Out...";
                speechText.setText(retString);
                tts.speak(retString,TextToSpeech.QUEUE_FLUSH,null);

                try {
                    mAuth.signOut();
                    startActivity(new Intent(Dashboard.this, LogIn.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 9:
                recognitionProgressView.stop();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                mSpeechRecognizer.stopListening();
                retString= "Fetching you twitter top hashtags";
                speechText.setText(retString);
                tts.speak(retString,TextToSpeech.QUEUE_FLUSH,null);
                mDatabase.child(userID).child("twitter").child("url").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String fetchURL = dataSnapshot.getValue(String.class);
//                        Toast.makeText(Dashboard.this, fetchURL, Toast.LENGTH_SHORT).show();
                        if(fetchURL == null){
                            startActivity(new Intent(Dashboard.this,Error.class));
                        }else{
                            url = fetchURL;
                        }

                        if(url.equals("http://tweeplers.com/hashtags/?cc=IN")){
                        Intent intent = new Intent(Dashboard.this,Main4Activity.class);
                        intent.putExtra("REQUEST_URL_FROM_TWITTER_SERVER", url);
                        startActivity(intent);}
                        else{
                            startActivity(new Intent(Dashboard.this,Error.class));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Dashboard.this, "Error in database query", Toast.LENGTH_SHORT).show();

                    }
                });

                break;

            case 10:
                recognitionProgressView.stop();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                mSpeechRecognizer.stopListening();
                retString = "Twitter subscription triggred";
                speechText.setText(retString);
                tts.speak(retString, TextToSpeech.QUEUE_FLUSH, null);
                mDatabase.child(userID).child("twitter").child("subscription").setValue("true");
                mDatabase.child(userID).child("twitter").child("url").setValue("http://tweeplers.com/hashtags/?cc=IN");
                break;




            default:
                recognitionProgressView.stop();
                mSpeechRecognizer.stopListening();
                recognitionProgressView.setVisibility(View.INVISIBLE);
                retString = "Im sorry!";
                speechText.setText(retString);
                tts.speak(retString, TextToSpeech.QUEUE_FLUSH, null);

                break;
        }
        return retString;
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say what you want to search");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    // Receiving speech input

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String searchData =  result.get(0);
//                    Intent intent = new Intent(Dashboard.this,MainActivity.class);
//                    intent.putExtra("REQUEST_WEB_SEARCH_FROM_SERVER", searchData);
//                    startActivity(intent);
                    speechText.setText(searchData);
                    mSpeechRecognizer.stopListening();
                    recognitionProgressView.stop();

                }
                String websearch = speechText.getText().toString();
                if(websearch !=null){
                Intent intent = new Intent(Dashboard.this,WebSearch.class);
                intent.putExtra("REQUEST_WEB_SEARCH_FROM_SERVER", websearch);
                startActivity(intent);}
                break;
            }

        }
    }

    @Override
    public void onResult(AIResponse result) {

    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    @Override
    public void onInit(int i) {



    }

    class SpeechListener implements RecognitionListener {
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "buffer recieved ");
        }

        public void onError(int error) {
            //if critical error then exit
            if (error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                Log.d(TAG, "client error");
            }
            //else ask to repeats
            else {
                Log.d(TAG, "other error");
                mSpeechRecognizer.startListening(mSpeechIntent);
            }
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent");
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "partial results");


        }

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "on ready for speech");


        }

        public void onResults(Bundle results) {

            Log.d(TAG, "on results");
            ArrayList<String> matches = null;
            if (results != null) {
                matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    Log.d(TAG, "results are " + matches.toString());
                    final ArrayList<String> matchesStrings = matches;
                    processCommand(matchesStrings);


                }
            }

        }

        public void onRmsChanged(float rmsdB) {
            //   Log.d(TAG, "rms changed");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "speach begining");


        }

        public void onEndOfSpeech() {
            Log.d(TAG, "speach done");

        }
    }


    @Override
    protected void onStart() {

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Dashboard.this);
        SpeechListener mRecognitionListener = new SpeechListener();
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
        mSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"io.jawware.noty");

        // Given an hint to the recognizer about what the user is going to say
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Specify how many results you want to receive. The results will be sorted
        // where the first result is the one with higher confidence.
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);


        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);


        recognitionProgressView.setSpeechRecognizer(mSpeechRecognizer);


        super.onStart();
    }

    @Override
    public void onDestroy() {



        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    private void processCommand(ArrayList<String> matchStrings){
        String response = "I'm sorry, Jaw. I cant hear.";

        int maxStrings = matchStrings.size();
        boolean resultFound = false;
        for(int i =0; i < VALID_COMMANDS_SIZE && !resultFound;i++){
            for(int j=0; j < maxStrings && !resultFound; j++){
                if(StringUtils.getLevenshteinDistance(matchStrings.get(j), VALID_COMMANDS[i]) <(VALID_COMMANDS[i].length() / 3) ) {
                    response = getResponse(i);
                }
            }
        }




    }
}

