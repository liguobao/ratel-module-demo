package ratel.com.shizhuang.duapp;

import android.util.Log;

import com.virjar.ratel.api.inspect.ClassLoadMonitor;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.sekiro.api.ActionHandler;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroResponse;
import com.virjar.sekiro.api.databind.AutoBind;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DewuQueryHandler implements ActionHandler {
    @Override
    public String action() {
        return "search ";
    }

    @AutoBind
    private String url;


    @AutoBind
    private String body;

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, final SekiroResponse sekiroResponse) {
        String searchJavaName = "com.shizhuang.duapp.modules.search.facade.SearchFacade";
        Class<?> searchJava = ClassLoadMonitor.tryLoadClass(searchJavaName);
        if (searchJava != null) {
            Method[] methods =  searchJava.getMethods();
            Class<?> viewHandlerClass = ClassLoadMonitor.tryLoadClass(
                    "com.shizhuang.duapp.common.helper.net.facade.ViewHandler");
            Class<?> hotWordModelClass = ClassLoadMonitor.tryLoadClass(
                    "com.shizhuang.duapp.modules.search.model.HotWordModel");
            if(viewHandlerClass!=null) {
                Log.i(CommonConst.AppTag, viewHandlerClass.toString());
                try{
                    Method l = searchJava.getMethod("l");
                    Class<?>[] parameterTypes = l.getParameterTypes();
                    sekiroResponse.success(parameterTypes);
                }catch (Exception ex){
                    Log.e(CommonConst.AppTag, "get mModel fail", ex);
                    sekiroResponse.success(methods);
                }
            }

        }
        return;

    }
}