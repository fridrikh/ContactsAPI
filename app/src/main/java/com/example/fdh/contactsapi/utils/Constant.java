package com.example.fdh.contactsapi.utils;

import android.os.Environment;

/**
 * Created by fdh on 03.09.15.
 */
public class Constant {

    public static final String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FOLDER_DIRECTORY = "/contactsAPI";

    public static final String FILE_NAME_TXT = "Contacts.txt";
    public static final String FILE_NAME_VCF = "Contacts.vcf";

    public static final String mPathTxt = ROOT_DIRECTORY
            + "/" + FOLDER_DIRECTORY
            + "/" + FILE_NAME_TXT;

    public static final String mPathVcf = ROOT_DIRECTORY
            + "/" + FOLDER_DIRECTORY
            + "/" + FILE_NAME_VCF;

}
