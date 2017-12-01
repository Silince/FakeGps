package name.caiyao.fakegps.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AppInfoProvider extends ContentProvider {
    public static final String AUTHRITY = "name.caiyao.fakegps.data.AppInfoProvider";
    public static final Uri APP_CONTENT_URI = Uri.parse("content://" + AUTHRITY + "/app");
    public static final int APP_URI_CODE = 0;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHRITY, "app", APP_URI_CODE);
    }

    private SQLiteDatabase mSQLiteDatabase;

    @Override
    public boolean onCreate() {
        DbHelper dbHelper = new DbHelper(getContext());
        mSQLiteDatabase = dbHelper.getWritableDatabase();
        dbHelper.onCreate(mSQLiteDatabase);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table = getTableName(uri);
        if (table == null)
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        return mSQLiteDatabase.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String table = getTableName(uri);
        if (table == null)
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        mSQLiteDatabase.insert(table, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        String table = getTableName(uri);
        if (table == null)
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        int count = mSQLiteDatabase.delete(table, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table = getTableName(uri);
        if (table == null)
            throw new IllegalArgumentException("Unsupported URI:" + uri);
        int row = mSQLiteDatabase.update(table,values,selection,selectionArgs);
        if (row>0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return row;
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case APP_URI_CODE:
                tableName = DbHelper.APP_TEMP_NAME;
                break;
        }
        return tableName;
    }
}
