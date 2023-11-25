package com.example.imagepro;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Handler;

public class objectDetectorClass {
    // should start from small letter

    // this is used to load model and predict
    private Interpreter interpreter;
    private Interpreter signInterpreter;

    // store all label in array
    private List<String> labelList;
    private int INPUT_SIZE;
    private int PIXEL_SIZE=3; // for RGB
    private int IMAGE_MEAN=0;
    private  float IMAGE_STD=255.0f;
    // use to initialize gpu in app
    private GpuDelegate gpuDelegate;
    private int height=0;
    private  int width=0;

    private int classificationInputSize = 0;
    private TextView tvSign;

    private String val;

    private android.os.Handler myHandler = new android.os.Handler();

    objectDetectorClass(TextView tvSign, AssetManager assetManager, String modelPath, String labelPath, int inputSize, String classificationModel, int classificationInputSize) throws IOException{
        this.tvSign = tvSign;
        INPUT_SIZE=inputSize;
        this.classificationInputSize = classificationInputSize;
        // use to define gpu or cpu // no. of threads
        Interpreter.Options options=new Interpreter.Options();
        gpuDelegate=new GpuDelegate();
        options.addDelegate(gpuDelegate);
        options.setNumThreads(4); // set it according to your phone
        // loading model
        interpreter=new Interpreter(loadModelFile(assetManager,modelPath),options);
        // load labelmap
        labelList=loadLabelList(assetManager,labelPath);

        Interpreter.Options options2 = new Interpreter.Options();
        options2.setNumThreads(2);
        signInterpreter = new Interpreter(loadModelFile(assetManager,classificationModel),options2);

    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        // to store label
        List<String> labelList=new ArrayList<>();
        // create a new reader
        BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        // loop through each line and store it to labelList
        while ((line=reader.readLine())!=null){
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        // use to get description of file
        AssetFileDescriptor fileDescriptor=assetManager.openFd(modelPath);
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset =fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
    }
    // create new Mat function
    public Mat recognizeImage(Mat mat_image){
        // Rotate original image by 90 degree get get portrait frame

        // This change was done in video: Does Your App Keep Crashing? | Watch This Video For Solution.
        // This will fix crashing problem of the app

        Mat rotated_mat_image=new Mat();

        Mat a=mat_image.t();
        Core.flip(a,rotated_mat_image,1);
        // Release mat
        a.release();

        // if you do not do this process you will get improper prediction, less no. of object
        // now convert it to bitmap
        Bitmap bitmap=null;
        bitmap=Bitmap.createBitmap(rotated_mat_image.cols(),rotated_mat_image.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rotated_mat_image,bitmap);
        // define height and width
        height=bitmap.getHeight();
        width=bitmap.getWidth();

        // scale the bitmap to input size of model
        Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false);

        // convert bitmap to bytebuffer as model input should be in it
        ByteBuffer byteBuffer=convertBitmapToByteBuffer(scaledBitmap);

        // defining output
        // 10: top 10 object detected
        // 4: there coordinate in image
        //  float[][][]result=new float[1][10][4];
        Object[] input=new Object[1];
        input[0]=byteBuffer;

        Map<Integer,Object> output_map=new TreeMap<>();
        // we are not going to use this method of output
        // instead we create treemap of three array (boxes,score,classes)

        float[][][]boxes =new float[1][10][4];
        // 10: top 10 object detected
        // 4: there coordinate in image
        float[][] scores=new float[1][10];
        // stores scores of 10 object
        float[][] classes=new float[1][10];
        // stores class of object

        // add it to object_map;
        output_map.put(0,boxes);
        output_map.put(1,classes);
        output_map.put(2,scores);

        // now predict
        interpreter.runForMultipleInputsOutputs(input,output_map);
        // Before watching this video please watch my previous 2 video of
        //      1. Loading tensorflow lite model
        //      2. Predicting object
        // In this video we will draw boxes and label it with it's name

        Object value=output_map.get(0);
        Object Object_class=output_map.get(1);
        Object score=output_map.get(2);

        float mainx1,mainx2,mainy1,mainy2;
        mainx1=Float.MAX_VALUE;
        mainx2=Float.MIN_VALUE;
        mainy1=Float.MAX_VALUE;
        mainy2=Float.MIN_VALUE;
        // loop through each object
        // as output has only 10 boxes
        for (int i=0;i<10;i++){
            // here we are loading through each hand which is detected

            float class_value=(float) Array.get(Array.get(Object_class,0),i);
            float score_value=(float) Array.get(Array.get(score,0),i);
            // define threshold for score

            // Here you can change threshold according to your model
            // Now we will do some change to improve app

            //System.out.println("Score value : "+score_value);


            if(score_value>0.5){
                System.out.print("Hand : "+i);

                Object box1=Array.get(Array.get(value,0),i);
                // we are multiplying it with Original height and width of frame

                //change this into x1,y1 and x2,y2co-ordinates
//                float top=(float) Array.get(box1,0)*height;
//                float left=(float) Array.get(box1,1)*width;
//                float bottom=(float) Array.get(box1,2)*height;
//                float right=(float) Array.get(box1,3)*width;

                float y1=(float) Array.get(box1,0)*height;
                float x1=(float) Array.get(box1,1)*width;
                float y2=(float) Array.get(box1,2)*height;
                float x2=(float) Array.get(box1,3)*width;

                if(y1 < 0) y1 = 0;
                if(x1 < 0) x1 = 0;
                if(x2 > width) x2 = width;
                if(y2 > height) y2 = height;

                mainx1=Math.min(mainx1,x1);
                mainy1=Math.min(mainy1,y1);

                mainx2=Math.max(mainx2,x2);
                mainy2=Math.max(mainy2,y2);

                /*now set the height and width of box.*/


                System.out.println("Point : "+x1+", "+x2+" ,"+y1+" ,"+y2);
                /*
                So if you don't know
                (x1,y1) is the starting point of hand
                (x2,y2) is the ending point of hand
                */



            }

        }

        if(mainx1!=Float.MAX_VALUE){

            float w1 = mainx2-mainx1;
            float h1 = mainy2-mainy1;
            /* crop hand image from original frame */
            Rect croppedRoi = new Rect((int)mainx1,(int)mainy1,(int)w1,(int)h1);
            Mat cropped = new Mat(rotated_mat_image,croppedRoi).clone();

            /* Now convert this cropped Mat to Bitmap */
            Bitmap bitmap1 = Bitmap.createBitmap(cropped.cols(),cropped.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cropped,bitmap1);

            /* Resize bitmap to classification input size */
            Bitmap scaledBitmap1 = Bitmap.createScaledBitmap(bitmap1,classificationInputSize,classificationInputSize,false);

            /* Convert bitmap to buffer */
            ByteBuffer byteBuffer1 = convertBitmapToByteBuffer1(scaledBitmap1);

            /* create an array for output of interpreter2 */
            float[][] outputClassValue = new float[1][1];

            Log.d("output_class", Arrays.deepToString(outputClassValue));

            /* Predict output for byteBuffer */
            signInterpreter.run(byteBuffer1,outputClassValue);
            Log.d("output_class", Arrays.deepToString(outputClassValue));

            /* Convert output class value to alphabets */
            String signVal = getAlphabets(outputClassValue[0][0]);


            myHandler.post(() -> tvSign.setText(signVal));

            /* use put-text to add class name in image */
            Imgproc.putText(rotated_mat_image,outputClassValue[0][0] + "-"+ signVal,new Point(mainx1+10,mainy1+40),2,1.5,new Scalar(255,255,255,255),2);



            // draw rectangle in Original frame //  starting point    // ending point of box  // color of box       thickness
            Imgproc.rectangle(rotated_mat_image,new Point(mainx1,mainy1),new Point(mainx2,mainy2),new Scalar(0, 255, 0, 255),2);
            // write text on frame
            // string of class name of object  // starting point                         // color of text           // size of text

            //Imgproc.putText(rotated_mat_image,labelList.get((int) class_value),new Point(left,top),3,1,new Scalar(255, 0, 0, 255),2);
        }

        // select device and run

        // before returning rotate back by -90 degree

        // Do same here
        Mat b=rotated_mat_image.t();
        Core.flip(b,mat_image,0);
        b.release();
        // Now for second change go to CameraBridgeViewBase
        return mat_image;
    }

    private String getAlphabets(float signValue) {
        System.out.println("signValue "+signValue);
        //Log.d("CameraActivity","Signvalue");

        if (signValue <= 2.5 ) val = "অ";
        else if (signValue <= 2.7) val = "আ";
        else if (signValue <= 5.5) val = "ই";
        else if (signValue <= 6.5) val = "উ";
        else if (signValue <= 7.5) val = "এ";
        else if (signValue <= 8.5) val = "ও";
        else if (signValue <= 9.5) val = "ক";
        else if (signValue <= 10.5) val = "খ";
        else if (signValue <= 11.5) val = "গ";
        else if (signValue <= 12.5) val = "ঘ";
        else if (signValue <= 13.5) val = "চ";
        else if (signValue <= 14.5) val = "ছ";
        else if (signValue <= 15.5) val = "জ";
        else if (signValue <= 16.5) val = "ঝ";
        else if (signValue <= 17.5) val = "ট";
        else if (signValue <= 18.5) val = "ঠ";
        else if (signValue <= 19.5) val = "ড";
        else if (signValue <= 20.5) val = "ঢ";
        else if (signValue <= 21.5) val = "ত";
        else if (signValue <= 22.5) val = "থ";
        else if (signValue <= 23.5) val = "দ";
        else if (signValue <= 24.5) val = "ধ";
        else if (signValue <= 25.5) val = "ন";
        else if (signValue <= 26.5) val = "প";
        else if (signValue <= 27.5) val = "ফ";
        else if (signValue <= 28.5) val = "ব";
        else if (signValue <= 29.5) val = "ভ";
        else if (signValue <= 30.5) val = "ম";
        else if (signValue <= 31.5) val = "য়";
        else if (signValue <= 32.5) val = "র";
        else if (signValue <= 33.5) val = "ল";
        else if (signValue <= 34.5) val = "স";
        else if (signValue <= 35.5) val = "হ";
        else if (signValue <= 36.5) val = "ড়";
        else if (signValue <= 37.5) val = "ং";
        else if (signValue <= 38.5) val = "ঃ";


        else val="Can't find symbol";
        return val;
    }

    private ByteBuffer convertBitmapToByteBuffer1(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        int quant=1;
        int size_images=classificationInputSize;
        byteBuffer=ByteBuffer.allocateDirect(4*1*size_images*size_images*3);

        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues=new int[size_images*size_images];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        int pixel=0;

        // some error
        //now run
        for (int i=0;i<size_images;++i){
            for (int j=0;j<size_images;++j){
                final  int val=intValues[pixel++];
                if(quant==0){
                    byteBuffer.put((byte) ((val>>16)&0xFF));
                    byteBuffer.put((byte) ((val>>8)&0xFF));
                    byteBuffer.put((byte) (val&0xFF));
                }
                else {
                    // paste this
                    byteBuffer.putFloat((((val >> 16) & 0xFF)));
                    byteBuffer.putFloat((((val >> 8) & 0xFF)));
                    byteBuffer.putFloat((((val) & 0xFF)));
                }
            }
        }
        return byteBuffer;
    }
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        // some model input should be quant=0  for some quant=1
        // for this quant=0
        // Change quant=1
        // As we are scaling image from 0-255 to 0-1
        int quant=1;
        int size_images=INPUT_SIZE;
        if(quant==0){
            byteBuffer=ByteBuffer.allocateDirect(1*size_images*size_images*3);
        }
        else {
            byteBuffer=ByteBuffer.allocateDirect(4*1*size_images*size_images*3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues=new int[size_images*size_images];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        int pixel=0;

        // some error
        //now run
        for (int i=0;i<size_images;++i){
            for (int j=0;j<size_images;++j){
                final  int val=intValues[pixel++];
                if(quant==0){
                    byteBuffer.put((byte) ((val>>16)&0xFF));
                    byteBuffer.put((byte) ((val>>8)&0xFF));
                    byteBuffer.put((byte) (val&0xFF));
                }
                else {
                    // paste this
                    byteBuffer.putFloat((((val >> 16) & 0xFF))/255.0f);
                    byteBuffer.putFloat((((val >> 8) & 0xFF))/255.0f);
                    byteBuffer.putFloat((((val) & 0xFF))/255.0f);
                }
            }
        }
        return byteBuffer;
    }
}
// Next video is about drawing box and labeling it
// If you have any problem please inform me