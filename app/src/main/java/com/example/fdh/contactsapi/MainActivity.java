package com.example.fdh.contactsapi;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fdh.contactsapi.utils.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private String id;
    private String name;
    private String mPhoneNumber;
    private String vCard;

    private Button btn_exp;

    private TextView textView_path;

    private StringBuffer output = new StringBuffer();

    private String _ID = ContactsContract.Contacts._ID;
    private String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private String CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;

    private Uri CONTENT_VCARD_URI = ContactsContract.Contacts.CONTENT_VCARD_URI;
    private Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    private Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_exp = (Button) findViewById(R.id.btn_exp);

        textView_path = (TextView) findViewById(R.id.textView_path);
        textView_path.setText("Путь к файлам: \n" + Constant.ROOT_DIRECTORY + Constant.FOLDER_DIRECTORY);
    }

    public void btn_Click(View v){
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Пожалуйста, подождите", "Экспортируем...", true);
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    exportContacts();
                } catch (Exception e){
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();
    }

    public void exportContacts(){
        if (isExternalStorageWritable()) {
            try{
                SaveFileTxt(Constant.mPathTxt, getContactsTxt());
                output.setLength(0);
                SaveFileVcf(Constant.mPathVcf);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public String getContactsTxt(){
        output.setLength(0);
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(CONTENT_URI, null, null, null, null);
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                id = cursor.getString(cursor.getColumnIndex(_ID));
                name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if(hasPhoneNumber > 0){
                    output.append("\n Имя: " + name);
                    Cursor phoneCursor = cr.query(PHONE_CONTENT_URI,
                            null,
                            CONTACT_ID + " = ?",
                            new String[] {cursor.getString(cursor.getColumnIndex(_ID))},
                            null);
                    while (phoneCursor.moveToNext()){
                        mPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Телефон: " + mPhoneNumber + "\n");
                    }
                    phoneCursor.close();
                }
            }
        }
        return output.toString();
    }

    public boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }

    public void SaveFileTxt(String mPathTxt, String FileContent)
    {
        File fhandle = new File(mPathTxt);
        try
        {
            if (!fhandle.getParentFile().exists())
                fhandle.getParentFile().mkdirs();
            fhandle.createNewFile();
            FileOutputStream fOut = new FileOutputStream(fhandle);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(FileContent);
            osw.close();
            fOut.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void SaveFileVcf(String mPathVcf){
        File fhandle = new File(mPathVcf);
        if (!fhandle.getParentFile().exists())
            fhandle.getParentFile().mkdirs();
        fhandle.delete();

        Cursor phones = getApplicationContext().getContentResolver().query(PHONE_CONTENT_URI, null, null, null, null);
        phones.moveToFirst();

        for(int i = 0; i<phones.getCount(); i++){
            String lookupKey = phones.getString(phones.getColumnIndex(LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(CONTENT_VCARD_URI, lookupKey);

            AssetFileDescriptor fd;
            try{
                fd = getApplicationContext().getContentResolver().openAssetFileDescriptor(uri, "r");
                FileInputStream fis = fd.createInputStream();
                byte[] buf = new byte[(int) fd.getDeclaredLength()];
                fis.read(buf);
                vCard = new String(buf);
                FileOutputStream mFileOutputStream = new FileOutputStream(fhandle, true);
                mFileOutputStream.write(vCard.toString().getBytes());
                phones.moveToNext();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}