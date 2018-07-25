package com.example.android.miwok.models;

public class Word {

    private static final int NO_IMAGE = -1;
    private static final int NO_AUDIO = -1;

    private String mMiwokTranslation;
    private String mDefaultTranslation;
    private int mImageID;
    private int mAudioID;

    public Word(String miwokTranslation, String defaultTranslation){
        mMiwokTranslation = miwokTranslation;
        mDefaultTranslation = defaultTranslation;
        mImageID = NO_IMAGE;
        mAudioID = NO_AUDIO;
    }

    public Word(String mMiwokTranslation, String mDefaultTranslation, int audioID) {
        this(mMiwokTranslation, mDefaultTranslation);
        mAudioID = audioID;
    }

    public Word(String mMiwokTranslation, String mDefaultTranslation, int imageID, int audioID) {
        this(mMiwokTranslation, mDefaultTranslation);
        mImageID = imageID;
        mAudioID = audioID;
    }



    public String getMiwokTranslation() {
        return mMiwokTranslation;
    }

    public String getDefaultTranslation() {
        return mDefaultTranslation;
    }

    public int getImageID(){
        return mImageID;
    }

    public int getAudioID() {
        return mAudioID;
    }

    public boolean hasImageID() {
        return mImageID != NO_IMAGE;
    }

    public boolean hasAudioID() {
        return mAudioID != NO_AUDIO;
    }
}
