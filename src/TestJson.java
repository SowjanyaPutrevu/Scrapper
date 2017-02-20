import com.icecat.callaway.CallawayAccessories;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Sowji on 07/02/2017.
 */
public class TestJson {

    public static void main(String[] args) {
        String jsonTest = "{\"a\":true,\"b\":1,\"c\":\"f\",\"d\":{ \"e\":{ \"a2\": { \"b3\": [ {\"4\": 4},{\"2\":2},{\"3\":3}] }  } }}";
        System.out.println(jsonTest);

        CallawayAccessories callawayAccessories = new CallawayAccessories();
        String json = callawayAccessories.get_html("http://www.callawaygolf.com/on/demandware.store/Sites-CG-Site/en_US/ProductConfigurator-FilteredAttributes?format=json&pid=bags-2017-great-big-bertha-epic-stand-staff&vid=spr4689686&cgid=staff-bags&qty=2&condition=BNW&a1=1");
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray  = jsonObject.getJSONArray("attributes");
        JSONObject atvalue = jsonArray.getJSONObject(0);
        String name = atvalue.getString("name");
        JSONArray jsonArray1 = atvalue.getJSONArray("values");
        JSONObject displayObject = jsonArray1.getJSONObject(0);
        String displayValue = displayObject.getString("displayValue");
    }
}
