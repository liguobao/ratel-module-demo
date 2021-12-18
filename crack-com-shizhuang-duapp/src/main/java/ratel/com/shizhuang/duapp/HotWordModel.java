package ratel.com.shizhuang.duapp;

import android.os.Parcel;
import android.os.Parcelable;

/* compiled from: HotWordModel.kt */
public final class HotWordModel implements android.os.Parcelable {
    public static android.os.Parcelable.Creator<Object> CREATOR = new Parcelable.Creator<Object>() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return null;
        }

        @Override
        public Object[] newArray(int i) {
            return new Object[0];
        }
    };

    private java.util.ArrayList<Object> list;
    private java.lang.String searchWord;

    private java.util.ArrayList<Object> wordMappings;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public HotWordModel(
            java.util.ArrayList<Object> arrayList,
            java.lang.String str,
            java.util.ArrayList<Object> arrayList2) {
        this.list = arrayList;
        this.searchWord = str;
        this.wordMappings = arrayList2;
    }
}
