package org.bonitasoft.sugarcrm;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SugarCRMUtils {
    String url;

    /**
     * Transform a list of list to a map. If the list is empty or null, it returns an empty map
     * @param list The list to transform
     * @return the corresponding map
     */
    public static Map<String, String> listToMap(List<List<Object>> list) {
        if (list == null) {
            return Collections.emptyMap();
        }

        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

        for (List<Object> line : list) {
            result.put((String) line.get(0), (String) line.get(1));
        }
        return result;
    }

    public SugarCRMUtils(String baseUrl) {
        url = baseUrl+"/service/v4/rest.php";
    }

    public String login(String username, String password) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String passwordHash = new BigInteger(1, md5.digest(password.getBytes())).toString(16);

        // the order is important
        Map<String, String> userCredentials = new LinkedHashMap<String, String>();
        userCredentials.put("user_name", username);
        userCredentials.put("password", passwordHash);

        // the order is important
        Map<String, Object> request = new LinkedHashMap<String, Object>();
        request.put("user_auth", userCredentials);
        request.put("application_name", "RestClient");

        JSONObject parse = callAPI("login", request);
        return (String) parse.get("id");
    }

    public String logout(String sessionId) throws IOException {
        Map<String, Object> request = new LinkedHashMap<String, Object>();
        request.put("session", sessionId);
        callAPI("logout", request);
        return null;
    }

    public JSONObject createARecord(String sessionId, String ObjectType, Map<String, String> parameters) throws IOException {
        Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();

        requestParameters.put("session", sessionId);
        requestParameters.put("module_name", ObjectType);
        requestParameters.put("name_value_list", parameters);

        return callAPI("set_entry", requestParameters);
    }

    public JSONObject updateObject(String sessionId, String objectType, String objectId, Map<String, String> parameters) throws IOException {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        parameters.putAll(requestParameters);
        parameters.put("id", objectId);
        return createARecord(sessionId, objectType, parameters);
    }

    public JSONObject deleteARecord(String sessionId, String objectType, String objectId) throws IOException {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("deleted", "1");
        return updateObject(sessionId, objectType, objectId, parameters);
    }

    public JSONObject retrieveAnObject(String sessionId, String objectType, String objectId) throws IOException {
        Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
        requestParameters.put("session", sessionId);
        requestParameters.put("module_name", objectType);
        requestParameters.put("id", objectId);

        return callAPI("get_entry", requestParameters);  //To change body of created methods use File | Settings | File Templates.
    }

    public void deleteRecords(String sessionId, String objectType, List<String> ids) throws IOException {
        for (String id : ids) {
            deleteARecord(sessionId, objectType, id);
        }
    }

    public JSONObject retrieveSeveralObjects(String sessionId, String objectType, List<String> ids, List<String> fields) throws IOException {
        Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
        requestParameters.put("session", sessionId);
        requestParameters.put("module_name", objectType);
        requestParameters.put("ids", ids);
        requestParameters.put("select_fields", fields);
        requestParameters.put("link_name_to_fields_array", new ArrayList<String>());
        return callAPI("get_entries", requestParameters);
    }

    public JSONObject query(String sessionId, String objectType, String query, String offset, List<String> select_fields, String max_results)
            throws IOException {
        Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
        requestParameters.put("session", sessionId);
        requestParameters.put("module_name", objectType);
        requestParameters.put("query", query);
        requestParameters.put("order_by", "");
        requestParameters.put("offset", offset);
        requestParameters.put("select_fields", select_fields);
        requestParameters.put("link_name_to_fields_array", new ArrayList<String>());
        requestParameters.put("max_results", max_results);
        requestParameters.put("deleted", "0");
        requestParameters.put("Favorites", "false");
        return callAPI("get_entry_list", requestParameters);
    }


    public JSONObject callAPI(String method, Map<String, Object> request) throws IOException {
        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("method", new StringBody(method));

        // define request encoding
        multipartEntity.addPart("input_type", new StringBody("JSON"));
        // define response encoding
        multipartEntity.addPart("response_type", new StringBody("JSON"));
        multipartEntity.addPart("rest_data", new StringBody(JSONObject.toJSONString(request)));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(multipartEntity);

        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpResponse execute = defaultHttpClient.execute(httpPost);

        HttpEntity entity = execute.getEntity();

        return (JSONObject) JSONValue.parse(new InputStreamReader(
                entity.getContent()));
    }
}
