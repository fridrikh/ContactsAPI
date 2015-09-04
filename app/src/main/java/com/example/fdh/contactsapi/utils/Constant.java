package com.example.fdh.contactsapi.utils;

import android.os.Environment;

/**
 * Created by fdh on 03.09.15.
 */
public class Constant {

    public static final String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FOLDER_DIRECTORY = "/contactsAPI";

    public static final String FILE_NAME_TXT = "ContactsAPI.txt";
    public static final String FILE_NAME_VCF = "Contacts_vCard.vcf";

}
