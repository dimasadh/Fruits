package com.example.fruits;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class AnswerCameraFragment extends Fragment {

//    Context gameActivityCtx = ((GameActivity)getContext());
    String nmFile;
    Button btnAmbilGambar;
    ImageView imgGambar;
    TextView tvHasil;
    ProgressDialog dialog = null;

    int serverResponseCode = 0;
    private static final int kodekamera = 222;
    private static final String upLoadServerUri = "http://192.168.100.186:5000/";

    public AnswerCameraFragment() {
        super(R.layout.fragment_answer_camera);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_answer_camera, container, false);

        btnAmbilGambar = v.findViewById(R.id.btnAmbilGambar);
        imgGambar = v.findViewById(R.id.imgGambar);
        tvHasil = v.findViewById(R.id.tvHasil);
        btnAmbilGambar.setOnClickListener(btnOperasi);

        return v;
    }

    View.OnClickListener btnOperasi = view -> {
        switch (view.getId()) {
            case R.id.btnAmbilGambar:
                openCamera();
                break;
        }
    };

    private void openCamera() {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new
                File(String.valueOf(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        imagesFolder.mkdirs();
        Date d = new Date();
        CharSequence s  = DateFormat.format("yyyyMMdd-hh-mm-ss", d.getTime());
        nmFile = imagesFolder + File.separator+  s.toString() + ".jpg";
        File image = new File(nmFile);

//        Uri uriSavedImage = Uri.fromFile(image);
        Uri uri = FileProvider.getUriForFile(getActivity().getBaseContext(), "com.example.fruits.example.provider", image);
        it.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(it, kodekamera);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {   super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode) {
                case (kodekamera):
                    prosesKamera(data);
                    uploadImage();
                    break;
            }
        }
    }

    private void prosesKamera(Intent datanya)
    {
        Bitmap bm;
        //bm = (Bitmap) datanya.getExtras().get("data");
        BitmapFactory.Options options;
        options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        bm = BitmapFactory.decodeFile(nmFile,options);
        imgGambar.setImageBitmap(bm);
    }

    private void uploadImage(){
        if (nmFile != null){
            dialog = ProgressDialog.show(getContext(), "", "Predicting image...", true);
            new Thread(() -> {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        tvHasil.setText("uploading started.....");
                    }
                });
//                        uploadFile(uploadFilePath + "" + uploadFileName);
                uploadFile(nmFile);
            }).start();
        }
        else {
            Toast.makeText(getContext(), "Please capture the image first", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("LongLogTag")
    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :"
                    + sourceFileUri);

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    tvHasil.setText("Source File not exist :"
                            + sourceFileUri);
                }
            });
            return 0;
        }
        else
        {
            try {
                // open a URL connection to the Server
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=" + '"' + "uploaded_file" + '"' + ";filename="
                        + fileName + '"' + lineEnd);

                dos.writeBytes(lineEnd);

                // preparing file image
                FileInputStream fileInputStream = new FileInputStream(sourceFile);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    getResponseMessage(conn);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                dialog.dismiss();
                ex.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        tvHasil.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(getContext(), "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                dialog.dismiss();
                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        tvHasil.setText("Got Exception : see logcat ");
                        Toast.makeText(getContext(), "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    private void getResponseMessage(HttpURLConnection conn) throws IOException {
        BufferedReader in=new BufferedReader(new
                InputStreamReader(
                conn.getInputStream()));

        StringBuffer sb = new StringBuffer("");
        String line="";
        while((line = in.readLine()) != null) {
            sb.append(line);
            break;
        }
        String response=sb.toString();
        Log.d("Response",sb.toString());
        in.close();

        getActivity().runOnUiThread(new Runnable() {
            public void run() {

//                String msg = "File Uploaded To Server.\n";

                tvHasil.setText("Prediksi:\n" + response);
                ((GameActivity)getContext()).setAnswer(response);
                Toast.makeText(getContext(), "File Upload Complete.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}