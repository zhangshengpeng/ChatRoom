package cn.edu.zjicm.Permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionUtils {
    public static final int REQUEST_GRANT_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS = 100;
    public static String[] checkPermission(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return null;
        }
        // sdk需要申请以下权限组
        ArrayList<String> list = new ArrayList<String>();
        for (String permission : permissions) {
            if (!(activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)) {
                list.add(permission);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        String[] permissionsNeedGrant = new String[list.size()];
        list.toArray(permissionsNeedGrant);
        return permissionsNeedGrant;
    }
    public static void grantPermission(Activity activity, String[] permissionsNeedGrant, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissionsNeedGrant, requestCode);
    }
    // 是否全部授权
    public static boolean isGrantedAllPermissions(String[] permissions, int[] grantResults){
        for(int i = 0; i < permissions.length; i++){
            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
