package me.wcy.weather.utils.permission;

public interface PermissionResult {
    void onGranted();

    void onDenied();
}
