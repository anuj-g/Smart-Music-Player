package com.group6.smartplayer.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.group6.smartplayer.R;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.Order;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.contract.Scores;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CameraActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    public static final String ARTIST_NAMES="artistnames";
    static int noOfFrames = 0;
    private Camera mCam;
    private MirrorView mCamPreview;
    private int mCameraId = 0;
    private FrameLayout mPreviewLayout;
    BroadcastReceiver broadcastReceiver;
    Button button;
    String email="";
    private static final int REQUEST_SELECT_IMAGE = 0;
    CallbackManager callbackManager;
  //  Intent i;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    // The button to select an image
    //private Button mButtonSelectImage;

    // The URI of the image selected to detect.
    //private Uri mImageUri;
    ImageView imageView;
    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    //private EditText mEditText;

    private EmotionServiceClient client;

    Intent startMainActivityIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        startMainActivityIntent = new Intent(getApplicationContext(),MainActivity.class);

        imageView= (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.main);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
                if(accessToken!=null)
                {
                    Log.d("access token oncuurent",accessToken.getToken());
                }
            }
        };

        accessToken= AccessToken.getCurrentAccessToken();
        if(accessToken!=null)
        {
            Log.d("access token outside",accessToken.getToken());
            new GraphRequest(AccessToken.getCurrentAccessToken(),"/me?fields=music",null, HttpMethod.GET, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response)
                {

                    // String musician =   response.getJSONObject().getString("musician");
                    Log.d("musician",response.getJSONObject().toString());
                    startMainActivityIntent.putExtra(ARTIST_NAMES,getArtistNamesFromJSON(response.getJSONObject()));
                    guessCurrentPlace();
                    //startActivity(new Intent(CameraActivity.this,MainActivity.class));

                }
            }).executeAsync();
        }

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "email", "user_about_me","user_actions.music","user_actions.video" ,"user_friends", "user_likes", "user_posts", "user_relationships", "user_status"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                if(accessToken!=null) {
                    Log.d("access token onsucces", accessToken.getToken());
                    //i.putExtra("access", accessToken.getToken());
                }
                /*GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    email = object.getString("email");
                                    Log.d("email",email);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();*/

                new GraphRequest(AccessToken.getCurrentAccessToken(),"/me?fields=music",null, HttpMethod.GET, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response)
                    {

                        // String musician =   response.getJSONObject().getString("musician");
                            Log.d("musician",response.getJSONObject().toString());
                        startMainActivityIntent.putExtra(ARTIST_NAMES,getArtistNamesFromJSON(response.getJSONObject()));
                        guessCurrentPlace();
                            //startActivity(new Intent(CameraActivity.this,MainActivity.class));

                    }
                }).executeAsync();

                //LoginManager.getInstance().logOut();
                //i=new Intent(LoginActivity.this,MainActivity.class);
                // i.setClassName(LoginActivity.this,MainActivity.class);

              //  i.putExtra("email",email);

              //  startActivity(i);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }


        });
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }
        //Toast.makeText(this, "Camera Started", Toast.LENGTH_SHORT).show();
        mCameraId = findFirstFrontFacingCamera();

        mPreviewLayout = (FrameLayout) findViewById(R.id.camPreview);
        mPreviewLayout.removeAllViews();
        /*button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCam.takePicture(null, pictureCallback, pictureCallback);
            }
        });*/
        mPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCam.takePicture(null, pictureCallback, pictureCallback);
            }
        });


        /*mCam.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                Log.d("oncreate face detected",faces.length+"");
            }
        });*/
       // guessCurrentPlace();


    }

    private void guessCurrentPlace() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback( new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult( PlaceLikelihoodBuffer likelyPlaces ) {

                PlaceLikelihood placeLikelihood = likelyPlaces.get( 0 );
                String content = "";
                if( placeLikelihood!= null && placeLikelihood.getPlace() != null && !TextUtils.isEmpty( placeLikelihood.getPlace().getName() ) )
                    content = "Most likely place: " + placeLikelihood.getPlace().getName() + "\n";
                if( placeLikelihood != null )
                    content += "Percent change of being there: " + (int) ( placeLikelihood.getLikelihood() * 100 ) + "%";
                //mTextView.setText( content );
                List<Integer> li=placeLikelihood.getPlace().getPlaceTypes();
                if( placeLikelihood.getPlace().getPlaceTypes().contains(Place.TYPE_GYM))  // 1013
                {
                    Log.d("places", content);
                    likelyPlaces.release();
                    startMainActivityIntent.putExtra("mood","happy");
                    Toast.makeText(CameraActivity.this, "Gym", Toast.LENGTH_SHORT).show();
                    startActivity(startMainActivityIntent);
                    finish();

                }
                else {
                    Log.d("places", content);
                    likelyPlaces.release();
                    startCameraInLayout(mPreviewLayout, mCameraId);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null)
            mGoogleApiClient.connect();
    }

    public void doRecognize() {
        //mButtonSelectImage.setEnabled(false);

        // Do emotion detection using auto-detected faces.
        try {
            new doRequest(false).execute();
        } catch (Exception e) {
           // mEditText.append("Error encountered. Exception is: " + e.toString());
        }

        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        if (faceSubscriptionKey.equalsIgnoreCase("Please_add_the_face_subscription_key_here")) {
            //mEditText.append("\n\nThere is no face subscription key in res/values/strings.xml. Skip the sample for detecting emotions using face rectangles\n");
        } else {
            // Do emotion detection using face rectangles provided by Face API.
            try {
                new doRequest(true).execute();
            } catch (Exception e) {
               // mEditText.append("Error encountered. Exception is: " + e.toString());
            }
        }
    }

    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long startTime = System.currentTimeMillis();
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE STARTS HERE
        // -----------------------------------------------------------------------

        List<RecognizeResult> result = null;
        //
        // Detect emotion by auto-detecting faces in the image.
        //
        result = this.client.recognizeImage(inputStream);

        String json = gson.toJson(result);
        Log.d("result", json);

        Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE ENDS HERE
        // -----------------------------------------------------------------------
        return result;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    /*private List<RecognizeResult> processWithFaceRectangles() throws EmotionServiceException, com.microsoft.projectoxford.face.rest.ClientException, IOException {
        Log.d("emotion", "Do emotion detection with known face rectangles");
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long timeMark = System.currentTimeMillis();
        Log.d("emotion", "Start face detection using Face API");
        FaceRectangle[] faceRectangles = null;
        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
        Face faces[] = faceClient.detect(inputStream, false, false, null);
        Log.d("emotion", String.format("Face detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));

        if (faces != null) {
            faceRectangles = new FaceRectangle[faces.length];

            for (int i = 0; i < faceRectangles.length; i++) {
                // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
            }
        }

        List<RecognizeResult> result = null;
        if (faceRectangles != null) {
            inputStream.reset();

            timeMark = System.currentTimeMillis();
            Log.d("emotion", "Start emotion detection using Emotion API");
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE STARTS HERE
            // -----------------------------------------------------------------------
            result = this.client.recognizeImage(inputStream, faceRectangles);

            String json = gson.toJson(result);
            Log.d("result", json);
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE ENDS HERE
            // -----------------------------------------------------------------------
            Log.d("emotion", String.format("Emotion detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));
        }
        return result;
    }*/
    private String[] getArtistNamesFromJSON(JSONObject object)
    {
        String[] artistNames=null;
        try {
            JSONArray array =object.getJSONObject("music").getJSONArray("data");
            artistNames=new String[array.length()];
            for(int i=0;i<array.length();i++)
            {
                artistNames[i]= array.getJSONObject(i).getString("name");
                Log.d("CmeraActivity",artistNames[i]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return artistNames;
    }
    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>>
    {
        // Store error message
        private Exception e = null;
        private boolean useFaceRectangles = false;

        public doRequest(boolean useFaceRectangles) {
            this.useFaceRectangles = useFaceRectangles;
        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            if (this.useFaceRectangles == false) {
                try {
                    return processWithAutoFaceDetection();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            } else {
                try {
                    //return processWithFaceRectangles();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RecognizeResult> result) {
            super.onPostExecute(result);
            // Display based on error existence

            if (this.useFaceRectangles == false) {
                //mEditText.append("\n\nRecognizing emotions with auto-detected face rectangles...\n");
            } else {
                //mEditText.append("\n\nRecognizing emotions with existing face rectangles from Face API...\n");
            }
            if (e != null) {
                //mEditText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                if (result.size() == 0) {
                   // mEditText.append("No emotion detected :(");
                } else {
                    Integer count = 0;
                    // Covert bitmap to a mutable bitmap by copying it
                    Bitmap bitmapCopy = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas faceCanvas = new Canvas(bitmapCopy);
                    faceCanvas.drawBitmap(mBitmap, 0, 0, null);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);

                    for (RecognizeResult r : result) {

                        Log.d("emotion happiness",r.scores.happiness+"");
                        String mood="";
                        double angerValue=r.scores.anger;
                        double happinessValue=r.scores.happiness;
                        double sadnessValue=r.scores.sadness;
                        if(angerValue > sadnessValue && angerValue> happinessValue)
                            mood="angry";
                        else if(happinessValue>sadnessValue)
                            mood = "happy";
                        else
                            mood = "sad";
                        //startMainActivityIntent = new Intent(CameraActivity.this,MainActivity.class);
                        startMainActivityIntent.putExtra("mood",mood);

                        startActivity(startMainActivityIntent);
                        finish();

                        Toast.makeText(CameraActivity.this, mood, Toast.LENGTH_SHORT).show();
                        /*
                        mEditText.append(String.format("\nFace #%1$d \n", count));
                        mEditText.append(String.format("\t anger: %1$.5f\n", r.scores.anger));
                        mEditText.append(String.format("\t contempt: %1$.5f\n", r.scores.contempt));
                        mEditText.append(String.format("\t disgust: %1$.5f\n", r.scores.disgust));
                        mEditText.append(String.format("\t fear: %1$.5f\n", r.scores.fear));
                        mEditText.append(String.format("\t happiness: %1$.5f\n", r.scores.happiness));
                        mEditText.append(String.format("\t neutral: %1$.5f\n", r.scores.neutral));
                        mEditText.append(String.format("\t sadness: %1$.5f\n", r.scores.sadness));
                        mEditText.append(String.format("\t surprise: %1$.5f\n", r.scores.surprise));
                        mEditText.append(String.format("\t face rectangle: %d, %d, %d, %d", r.faceRectangle.left, r.faceRectangle.top, r.faceRectangle.width, r.faceRectangle.height));
                        faceCanvas.drawRect(r.faceRectangle.left,
                                r.faceRectangle.top,
                                r.faceRectangle.left + r.faceRectangle.width,
                                r.faceRectangle.top + r.faceRectangle.height,
                                paint);
                        count++;*/
                    }
                    //ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                    //imageView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
                }
                //mEditText.setSelection(0);
            }

            //mButtonSelectImage.setEnabled(true);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            //Do something
            mCam.takePicture(null,pictureCallback,pictureCallback);
            Toast.makeText(CameraActivity.this,"Picture Taken",Toast.LENGTH_SHORT).show();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            //Do something
            finish();
            //mCam.takePicture(null,pictureCallback,pictureCallback);
        }
        return true;
    }

    private int findFirstFrontFacingCamera() {
        int foundId = -1;
        int numCams = Camera.getNumberOfCameras();
        for (int camId = 0; camId < numCams; camId++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(camId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                foundId = camId;
                break;
            }
        }
        return foundId;
    }
    private void startCameraInLayout(FrameLayout layout, int cameraId) {
        mCam = Camera.open(cameraId);
        if (mCam != null) {
            Camera.Parameters parameters = mCam.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            mCam.setParameters(parameters);
            mCam.setDisplayOrientation(90);
            mCamPreview = new MirrorView(this, mCam);
            layout.addView(mCamPreview);
        }
    }
    /*
    private class UploadAsyncTask extends AsyncTask<File,Void,Void>
    {

        @Override
        protected Void doInBackground(File... params)
        {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://ec2-35-167-175-149.us-west-2.compute.amazonaws.com/postPic");
            *//*
            MultipartEntity mpEntity = new MultipartEntity(Ht);
            if (params[0] != null) {
                //File file = new File(filePath);
                Log.d("EDIT USER PROFILE", "UPLOAD: file length = " + params[0].length());
                Log.d("EDIT USER PROFILE", "UPLOAD: file exist = " + params[0].exists());
                mpEntity.addPart("avatar", new FileBody(file, "application/octet"));
            }
            *//*
            org.apache.http.entity.mime.MultipartEntity multipartEntity=new org.apache.http.entity.mime.MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (params[0] != null) {
                //File file = new File(filePath);
                Log.d("EDIT USER PROFILE", "UPLOAD: file length = " + params[0].length());
                Log.d("EDIT USER PROFILE", "UPLOAD: file exist = " + params[0].exists());
                multipartEntity.addPart("picture", new FileBody(params[0],"multipart/form-data"));

            }
            httppost.setEntity(multipartEntity);
            try {
                httpclient.execute(httppost);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }*/
    Camera.PictureCallback pictureCallback=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data .length);
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                bitmap= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        matrix, true);
                mBitmap=bitmap;
                if(bitmap!=null){

                    File file=new File(Environment.getExternalStorageDirectory()+"/dirr");
                    if(!file.isDirectory()){
                        file.mkdir();
                    }

                    file=new File(Environment.getExternalStorageDirectory()+"/dirr",System.currentTimeMillis()+".jpg");


                    try
                    {
                        FileOutputStream fileOutputStream=new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);

                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch(Exception exception)
                    {
                        exception.printStackTrace();
                    }

                    //new UploadAsyncTask().execute(file);
                    doRecognize();
                }
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCam!=null)
            mCam.release();
    }

    public class MirrorView extends SurfaceView implements
            SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public MirrorView(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);


                mCamera.startPreview();
                mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                    @Override
                    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                        Log.d("face detected",faces.length+"");
                        CameraActivity.noOfFrames++;
                        if(CameraActivity.noOfFrames==60)
                        {
                            mCamera.takePicture(null,pictureCallback,pictureCallback);
                            mCamera.setFaceDetectionListener(null);
                            CameraActivity.noOfFrames=0;
                        }
                    }
                });
                mCamera.startFaceDetection();

            } catch (Exception error) {
                Log.d("error",
                        "Error starting mPreviewLayout: " + error.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }


        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                                   int h) {
            if (mHolder.getSurface() == null) {
                return;
            }

            // can't make changes while mPreviewLayout is active
            try {
                mCamera.stopPreview();
            } catch (Exception e) {

            }

            try {

                // start up the mPreviewLayout
                mCamera.setPreviewDisplay(mHolder);

                mCamera.startPreview();
                mCamera.startFaceDetection();

            } catch (Exception error) {
                Log.d("error",
                        "Error starting mPreviewLayout: " + error.getMessage());
            }
        }
    }
}
