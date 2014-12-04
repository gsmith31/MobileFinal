package edu.elon.cs.camera;
/**
 * Copyright George W. Smith 2014
 * AlphaIT v 1.0
 */
import android.app.Activity;
import android.app.Dialog;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private SurfaceView SurView;
	private SurfaceHolder camHolder;
	private boolean previewRunning;
	final Context context = this;
	public static Camera camera = null;
	private SearchWolfram search;
	private static String path;
	private ImageButton capture;
	private Dialog currentDialog;
	private ImageButton info;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SurView = (SurfaceView) findViewById(R.id.sview);
		camHolder = SurView.getHolder();
		camHolder.addCallback(this);

		capture = (ImageButton) findViewById(R.id.capture);
		capture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				camera.takePicture(null, null, mPicture);
			}

		});
		info = (ImageButton) findViewById(R.id.infoButton);
		info.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showInformationDialog();
				
			}
			
		});
		search = null;
	}
	
	public boolean showInformationDialog(){
		currentDialog = new Dialog(this);
		currentDialog.setContentView(R.layout.information_dialog);
		currentDialog.setTitle(R.string.appInfo);
		TextView textInfo = (TextView) currentDialog.findViewById(R.id.infoView);
		textInfo.setText(getMessageInfo());
		TextView textHow = (TextView) currentDialog.findViewById(R.id.howTo);
		textHow.setText(getMessageHow());
		currentDialog.show();
		return true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG)
					.show();
			finish();
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (previewRunning) {
			camera.stopPreview();
		}
		Camera.Parameters camParams = camera.getParameters();
		Camera.Size size = camParams.getSupportedPreviewSizes().get(0);
		camParams.setPreviewSize(size.width, size.height);
		camera.setParameters(camParams);
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			previewRunning = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				return;
			}
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("Error accessing file: " + e.getMessage());
			}
			Bitmap bitmap = configureBitmap();
			TessBaseAPI baseApi = new TessBaseAPI();
			baseApi.init("/storage/emulated/0", "eng");
			baseApi.setImage(bitmap);
			String string = baseApi.getUTF8Text();
			ProcessResult process = new ProcessResult(string);
			string = process.process();
			Log.d("WORD", "Word from Image: " + string);
			searchWolfram(string);
			boolean deleted = pictureFile.delete();
			camera.stopPreview();
			camera.startPreview();
		}
	};

	private static File getOutputMediaFileUri(int type) {
		return getOutputMediaFile(type);
	}

	
	private static File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			System.out
					.println("IMAGE PATH:------>" + mediaStorageDir.getPath());
			path = mediaStorageDir.getPath() + File.separator + "IMG_"
					+ timeStamp + ".jpg";
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	public void searchWolfram(String input) {
		search = new SearchWolfram(input);
		search.executeQuery();
		boolean done = search.getIsDone();
		while (done == false) {
			done = search.getIsDone();
		}
		String result = "";
		result = search.getResult();
		System.out.println(""+result);
		if(result == null){
			result = "Word or problem was not interpreted correctly. Please try again.";
		}
		Toast toast = Toast.makeText(context, result, Toast.LENGTH_LONG);
		toast.show();
	}
	
	
	public Bitmap configureBitmap(){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		// System.out.println(bitmap.getHeight());
		try {
			ExifInterface exif = new ExifInterface(path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			int rotate = 0;
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}
			if (rotate != 0) {
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				bitmap = Bitmap
						.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}
			return bitmap.copy(Bitmap.Config.ARGB_8888, true);
		} catch (IOException e) {
			Log.d("OCR", "Could not find path OR Image roatation broke");
		}
		return null;
	}
	
	public String getMessageInfo(){
		String message = "AlphaIT utilizes the Wolfram|Alpha API so that "
				+ "it can connect and recieve information based on queries. "
				+ "AlphaIT also uses an API that is a fork of Tesseract Android "
				+ "Tools by Robert Theis called Tess Two.";
		return message;
	}
	
	public String getMessageHow(){
		String message = "How to use:\n"
				+ "Place the text or equation you wish to search in the center of the screen. "
				+ "Then hit the camera icon and wait you receive the anser. If the image processing"
				+ "software does no interpret the image correctly then you will be prompted to try again.";
		return message;
	}
	

}
