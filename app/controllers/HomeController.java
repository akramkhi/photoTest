package controllers;
import services.*;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import play.cache.SyncCacheApi;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import scala.util.Random;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.libs.ws.WSResponse;
import play.libs.ws.WSBodyReadables;
import java.util.List;
import java.util.ArrayList;
import play.api.Configuration;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller  implements  WSBodyReadables{

    private final Config config;
    private final SyncCacheApi cache;
    private final WSClient wsClient;
    private SyncCacheApi cacheApi;
    private JsonNode resultOfGet ;
    private ContentAPIService service;
    private String url;




    @Inject
    public HomeController(ContentAPIService service ,Config config, SyncCacheApi cache, WSClient wsClient) {
        this.service = service;
        this.config = config;
        this.cache = cache;
        this.wsClient = wsClient;
    }

    public void setResultOfGet(JsonNode a){
        this.resultOfGet = a;
    }
    public Config getConfig() {
        return config;
    }


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }


    /**
     * the point of test
     * @return Result
     */

   /*
    check if there is a random number in cache
    if yes --> send with random nbr of cache
    if no --> he create a random nbr for 5 sec and send it
    */
    public Result test(){
        int randomNumber;

        if(cache.get("randomNbr") == null){
            randomNumber = service.getRandm(0,1084);
            cache.set("randomNbr", randomNumber, Integer.parseInt(getConfig().getString("TimeToRefresh")));
            System.out.println("there is not cache and the new is " +randomNumber);
        }else{
            randomNumber = (int)(cache.get("randomNbr"));
            System.out.println("there is cache" +randomNumber);
        }
         this.url = (getConfig().getString("URLPhoto")+"/id/"+randomNumber+"/200/300");

        List<String> l = new ArrayList<String>() ;
        return ok(views.html.test.render(url,l));

    }



    public Result  testMultiple(){
        String root ;

        //CompletionStage<JsonNode> responsePromise  =
       // final String aaa ;
      // wsClient.url("https://picsum.photos/v2/list").get().thenApply(  r ->{   System.out.println(r.getBody(json())) ; return ok("ok"); });
        CompletionStage<JsonNode> jss = wsClient.url("https://picsum.photos/v2/list").get().whenComplete((r, exception) -> {
            if (exception != null) {
                System.out.println("exception occurs");
                System.err.println(exception);
            } else {
               setResultOfGet(r.getBody(json()));
                //System.out.println("no exception, got result: " + r.getBody());
            }
        }).thenApply(  r -> r.getBody(json()));
        try {
            Thread.sleep(2000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        List<String> listOfUrl = new ArrayList<String>();

        for(int i=0; i<resultOfGet.size(); i++){
           if(((resultOfGet.get(i).get("id").asInt()) % 2)==0){
              // System.out.println(this.resultOfGet.get(i));
               listOfUrl.add(resultOfGet.get(i).get("download_url").asText());
           }
        }
        //System.out.println("the rslllt "+listOfUrl);

        return ok(views.html.test.render(url,listOfUrl));
    }
}
