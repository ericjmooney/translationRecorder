package wycliffeassociates.recordingapp.ProjectManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import wycliffeassociates.recordingapp.SettingsPage.Settings;

/**
 * Created by sarabiaj on 5/10/2016.
 */
public class Project implements Parcelable{

    public static final String PROJECT_EXTRA = "project_extra";

    String mTargetLang;
    String mSrcLang;
    String mSlug;
    String mSource;
    String mMode;
    String mProject;
    String mContributors;
    String mBookNum;
    String mSourceAudioPath;

    public Project(){
    }

    public Project(String tLang, String sLang, String bookNum, String slug, String src, String mode, String project, String contributors, String sourceAudioPath){
        mTargetLang = tLang;
        mSrcLang = sLang;
        mSlug = slug;
        mBookNum = bookNum;
        mSource = src;
        mMode = mode;
        mProject = project;
        mContributors = contributors;
        mSourceAudioPath = sourceAudioPath;
    }

    public Project(String tLang, String sLang, int bookNum, String slug, String src, String mode, String project, String contributors, String sourceAudioPath){
        mTargetLang = tLang;
        mSrcLang = sLang;
        mSlug = slug;
        mBookNum = String.valueOf(bookNum);
        mSource = src;
        mMode = mode;
        mProject = project;
        mContributors = contributors;
        mSourceAudioPath = sourceAudioPath;
    }

    public static Project getProjectFromPreferences(Context ctx){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String tLang = pref.getString(Settings.KEY_PREF_LANG, "");
        String sLang = pref.getString(Settings.KEY_PREF_LANG_SRC, "");
        String bookNum = pref.getString(Settings.KEY_PREF_BOOK_NUM, "");
        String slug = pref.getString(Settings.KEY_PREF_BOOK, "");
        String src = pref.getString(Settings.KEY_PREF_SOURCE, "");
        String mode = pref.getString(Settings.KEY_PREF_CHUNK_VERSE, "");
        String project = pref.getString(Settings.KEY_PREF_PROJECT, "");
        String contributors = pref.getString(Settings.KEY_PROFILE, "");
        String sourceAudioPath = pref.getString(Settings.KEY_PREF_SRC_LOC, "");
        return new Project(tLang, sLang, bookNum, slug, src, mode, project, contributors, sourceAudioPath);
    }

    public String getTargetLang(){
        return (mTargetLang == null)? "" : mTargetLang;
    }

    public String getSrcLang(){
        return (mSrcLang == null)? "" : mSrcLang;
    }

    public String getSlug(){
        return (mSlug == null)? "" : mSlug;
    }

    public String getSource(){
        return (mSource == null)? "" : mSource;
    }

    public String getMode(){
        return (mMode == null)? "" : mMode;
    }

    public String getContributors(){
        return (mContributors == null)? "" : mContributors;
    }

    public String getBookNumber(){
        return (mBookNum == null)? "" : mBookNum;
    }

    public String getProject(){
        return (mProject == null)? "" : mProject;
    }

    public String getSourceAudioPath(){
        return (mSourceAudioPath == null)? "" : mSourceAudioPath;
    }

    public void setTargetLanguage(String target){
        mTargetLang = target;
    }

    public void setSourceLanguage(String source){
        mSrcLang = source;
    }

    public void setSource(String source){
        mSource = source;
    }

    public void setSlug(String slug){
        mSlug = slug;
    }

    public void setMode(String mode){
        mMode = mode;
    }

    public void setProject(String project){
        mProject = project;
    }

    public void setContributors(String contributors){
        mContributors = contributors;
    }

    public void setBookNumber(String bookNumber){
        mBookNum = bookNumber;
    }

    public void setBookNumber(int bookNumber){
        mBookNum = String.valueOf(bookNumber);
    }

    public void setSourceAudioPath(String sourceAudioPath){
        mSourceAudioPath = sourceAudioPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTargetLang);
        dest.writeString(mSrcLang);
        dest.writeString(mSlug);
        dest.writeString(mSource);
        dest.writeString(mMode);
        dest.writeString(mProject);
        dest.writeString(mContributors);
        dest.writeString(mBookNum);
        dest.writeString(mSourceAudioPath);
    }

    public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
        public Project createFromParcel(Parcel in){
            return new Project(in);
        }
        public Project[] newArray(int size){
            return new Project[size];
        }
    };

    public Project(Parcel in){
        mTargetLang = in.readString();
        mSrcLang = in.readString();
        mSlug = in.readString();
        mSource = in.readString();
        mMode = in.readString();
        mProject = in.readString();
        mContributors = in.readString();
        mBookNum = in.readString();
        mSourceAudioPath = in.readString();
    }
}
