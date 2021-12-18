package ratel.com.shizhuang.duapp;


import android.util.Log;

import com.virjar.ratel.api.extension.superappium.PageTriggerManager;
import com.virjar.ratel.api.extension.superappium.SuperAppium;
import com.virjar.ratel.api.extension.superappium.sekiro.DumpTopActivityHandler;
import com.virjar.ratel.api.extension.superappium.sekiro.DumpTopFragmentHandler;
import com.virjar.ratel.api.extension.superappium.sekiro.ExecuteJsOnWebViewHandler;
import com.virjar.ratel.api.extension.superappium.sekiro.ScreenShotHandler;
import com.virjar.ratel.api.inspect.ClassLoadMonitor;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;
import com.virjar.sekiro.Constants;
import com.virjar.sekiro.api.SekiroClient;
import com.virjar.sekiro.log.SekiroLogger;

import java.net.URL;

import external.com.alibaba.fastjson.JSON;

public class CommonHookEntry {

    public static SekiroClient initSekiroClient(RC_LoadPackage.LoadPackageParam lpparam){
        SekiroLogger.tag = CommonConst.AppTag;
        String clientId =  lpparam.packageName;// for debug
        if(lpparam.packageName.equals(lpparam.processName)) {
            //String clientId =  lpparam.packageName + "."+UUID.randomUUID().toString().substring(0,8);
            SekiroClient sekiroClient = SekiroClient.start("sekiro.virjar.com", Constants.defaultNatServerPort, clientId, CommonConst.sekiroGroup);
            Log.i(SuperAppium.TAG, "start a supperAppium client: " + CommonConst.AppTag+ ",clientId:"+clientId);
            PageTriggerManager.getTopFragment("insureComponentStarted");
            return sekiroClient.registerHandler(CommonConst.dumpTopActivity, new DumpTopActivityHandler())
                    .registerHandler(CommonConst.dumpTopFragment, new DumpTopFragmentHandler())
                    .registerHandler(CommonConst.screenShot, new ScreenShotHandler())
                    .registerHandler(CommonConst.executeJsOnWebView, new ExecuteJsOnWebViewHandler());
        }
        return  null;
    }

    /**
     * 打印所有的类名
     */
    public static void logClassName() {
        ClassLoadMonitor.addClassLoadMonitor(new ClassLoadMonitor.OnClassLoader() {
            @Override
            public void onClassLoad(Class<?> clazz) {
                String name = clazz.getName();
                if(name !=null){
                    Log.i(CommonConst.AppTag,"init class:"+name);
                }
            }
        });
    }

    /**
     * 打印gc.o 这个类的构造函数参数
     */
    public static   void  logGCOIReaderClass(String className){
        Class<?> classType = ClassLoadMonitor.tryLoadClass(className);
        RposedBridge.hookAllConstructors(classType, new RC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(CommonConst.AppTag,"hook "+ param.thisObject+" Constructors, args[0]:" + param.args[0].toString());
                Object arguments =  RposedHelpers.callMethod(param.args[0],"getArguments");
                Log.i(CommonConst.AppTag,"SeeMore arguments:"+ arguments.toString());
            }
        });
    }

    /**
     * 添加 okhttp3 的调用日志
     */
    public static void addOKHTTP3ExecuteLog(RC_LoadPackage.LoadPackageParam lpparam) {
        Class realCall = RposedHelpers.findClass("okhttp3.RealCall", lpparam.classLoader);
        if (realCall != null) {
            RposedHelpers.findAndHookMethod(realCall, "execute", new RC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object req = RposedHelpers.getObjectField(param.thisObject, "originalRequest");
                    if (req == null) {
                        return;
                    }
                    Object headers = RposedHelpers.getObjectField(req, "headers");
                    Object body = RposedHelpers.getObjectField(req, "body");
                    Object url = RposedHelpers.getObjectField(req, "url");
                    String reqJson = JSON.toJSONString(req);
                    String bodyValue = JSON.toJSONString(body);
                    Log.i(CommonConst.AppTag,"okhttp3.RealCall,url:" + url.toString() + ",headers:" + headers.toString()+",body:"+bodyValue);
                }
            });
        }
    }

    /**
     * 添加url 构造函数日志
     */
    public static void addURLConstructorLog() {
        RposedBridge.hookAllConstructors(URL.class, new RC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(CommonConst.AppTag,"access url:" + param.thisObject);
            }
        });
    }


}
