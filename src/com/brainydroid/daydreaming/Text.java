package com.brainydroid.daydreaming;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;

public class Text {

public static String readTxt(int id, Activity a){

	   InputStream inputStream = a.getResources().openRawResource(id);

	   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	   int i;
	   try {
		   i = inputStream.read();
		   while (i != -1)
		   {
			   byteArrayOutputStream.write(i);
			   i = inputStream.read();
		   }
		   inputStream.close();
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }

	   return byteArrayOutputStream.toString();
}



}
