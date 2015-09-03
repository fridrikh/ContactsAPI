package com.example.fdh.contactsapi;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fdh.contactsapi.utils.Constant;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private String id;
    private String name;
    private String mPhoneNumber;

    private TextView mContacts;

    private StringBuffer output = new StringBuffer();

    private String _ID = ContactsContract.Contacts._ID;
    private String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private String CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    private Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    private Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    private FileOutputStream fileOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContacts = (TextView) findViewById(R.id.textView1);
        getContactsAPI();
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
            mContacts.setText(output);
            writeToFile();
        }
    }

    public void writeToFile(){
        try {
            fileOut = openFileOutput(Constant.FILE_NAME_TXT, MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.write(output.toString());
            outputWriter.close();

            Toast.makeText(getBaseContext(), "Файл создан!",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
