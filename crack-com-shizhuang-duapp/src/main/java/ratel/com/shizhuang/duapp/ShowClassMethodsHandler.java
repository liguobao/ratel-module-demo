package ratel.com.shizhuang.duapp;
import com.virjar.ratel.api.inspect.ClassLoadMonitor;
import com.virjar.sekiro.api.ActionHandler;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroResponse;
import com.virjar.sekiro.api.databind.AutoBind;

public class ShowClassMethodsHandler implements ActionHandler {
    @Override
    public String action() {
        return "showClassMethods";
    }

    @AutoBind
    private String className;

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, final SekiroResponse sekiroResponse) {
        String searchJavaName = className !=null && className.length()>0 ? className : "com.shizhuang.duapp.modules.search.facade.SearchFacade";
        Class<?> searchJava = ClassLoadMonitor.tryLoadClass(searchJavaName);
        if (searchJava != null) {
            sekiroResponse.success(searchJava.getMethods());
        }else{
            sekiroResponse.failed(searchJavaName + " not found.");
        }
    }
}