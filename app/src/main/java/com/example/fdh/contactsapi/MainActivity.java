package com.example.fdh.contactsapi;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fdh.contactsapi.utils.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private String id;
    private String name;
    private String mPhoneNumber;
    private String mFullPath;

    private Button btn_exp;

    private StringBuffer output = new StringBuffer();

    private String _ID = ContactsContract.Contacts._ID;
    private String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private String CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    private Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    private Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_exp = (Button) findViewById(R.id.btn_exp);
    }

    public void btn_Click(View v){
        getContactsAPI();

        mFullPath = Constant.ROOT_DIRECTORY
                + "/" + Constant.FOLDER_DIRECTORY
                + "/" + Constant.FILE_NAME_TXT;
        if (isExternalStorageWritable())
        {
            SaveFile(mFullPath, output.toString());
        }
    }

    public void getContactsAPI(){
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(CONTENT_URI, null, null, null, null);
        if(cur.getCount() > 0){
            while(cur.moveToNext()){
                id = cur.getString(cur.getColumnIndex(_ID));
                name = cur.getString(cur.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cur.getString(cur.getColumnIndex(HAS_PHONE_NUMBER)));

                if(hasPhoneNumber > 0){
                    output.append("\n Имя: " + name);
                    Cursor phoneCursor = cr.query(PHONE_CONTENT_URI,
                            null,
                            CONTACT_ID + " = ?",
                            new String[] {cur.getString(cur.getColumnIndex(_ID))},
                            null);
                    while (phoneCursor.moveToNext()){
                        mPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Телефон: " + mPhoneNumber + "\n");
                    }
                    phoneCursor.close();
                }
            }
        }
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

    public void SaveFile (String filePath, String FileContent)
    {
        File fhandle = new File(filePath);
        try
        {
            if (!fhandle.getParentFile().exists())
                fhandle.getParentFile().mkdirs();
            fhandle.delete();
            fhandle.createNewFile();
            FileOutputStream fOut = new FileOutputStream(fhandle);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(FileContent);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(getApplicationContext(), "Контакты экспортированы", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Произошла ошибка при записи файла", Toast.LENGTH_SHORT).show();
        }
    }


}
