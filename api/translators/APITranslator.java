package api.translators;

/**
 * APITranslator class for facilitating communications between the computer and external APIs.
 * Authors: Preston Williamson
 * Last Updated Date: 04-MAY-2020
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

public class APITranslator {
    protected final int API_SUCCESS_CODE = 200;
    protected String apiReturn, parameterString, requestMethod, urlSite, userKey;
    protected LinkedHashMap <String, String> config = new LinkedHashMap <> ();
    protected URL url;
    protected HttpURLConnection connect;
    protected JSONObject apiParse;
    protected final char START_INIDCATOR = '{';

    /**
     * formatAPIResultString: a helper method that ensures the prospect API return string matches the APIResult formatting requirements.
     * i.e., a APIResult return value must start with an opening curly bracket ('{').
     */
    public void formatAPIResultString () {
        //continue chopping off leading and trailing characters until the expected first char is obtained.
        final int FIRST_CHAR = 0;
        String apiRet = this.getAPIResultString ();
        while (apiRet.charAt (FIRST_CHAR) != this.START_INIDCATOR) {
            apiRet = apiRet.substring (1, apiRet.length () - 1);
        }

        this.setAPIResultString (apiRet);
    }
    
    
    /**
     * convertAPIConfigParameter: a helper method that converts a map into a delimited parameter String.
     * @param _key: unique key in the map
     * @param _map: map to be converted
     * @param _delimiter: delimiter that will separate each instance in the parameter string.
     */
    public void convertAPIConfigParameter (String _key, TreeMap <Integer, String> _map, String _delimiter) {
        final int FIRST_CHAR = 0;
        String ret = "";
        
        //convert map to string, separated by delimiter.
        for (Object key : _map.keySet ()) {
            ret = ret.concat (String.valueOf (key) + _delimiter);
        }
        
        int delimiterLen = _delimiter.length ();
        int resultLen = ret.length ();
        
        //remove the trailing delimiter in the string.
        int removeChar = (delimiterLen < resultLen) ? (resultLen - delimiterLen) : delimiterLen;
        
        //set API parameter.
        this.setAPIConfigParameter (_key, ret.substring (FIRST_CHAR, removeChar));
    }    

    /**
     * submitAPIRequest: a method that handles parsing from an API POST/GET request.
     * @param _requestMethod: Request Method ("GET", "POST")
     * @param _attributes: a String representation of the desired attribute from which to parse.
     */
    public void submitAPIRequest (String _requestMethod, String _attributes) {
        //set request method.
        this.setRequestMethod (_requestMethod);
        try {
            //set up connection parameters.
            this.configureConnectionProperties ();
            
            //parse API results from the connection input stream.
            this.parseAPIResults (_attributes);

            //disconnect system resources when finished.
            this.closeConnection ();
        }
        catch (Exception ex) {
            //throw exception if caught.
            Logger.getLogger (this.getClass ().getName ()).log (Level.SEVERE, null, ex);
        }
    }
    
    /**
     * configureConnectionProperties: method that handles setting up the connection object model.
     * @throws MalformedURLException
     * @throws IOException
     */
    public void configureConnectionProperties () throws MalformedURLException, IOException, Exception {
        String apiKey = this.getUserKey ();
        String reqMethod = this.getRequestMethod ();
        this.setURLObject ();
        this.setConnectionObject ();

        //get connection object with pre-configured URL.
        HttpURLConnection connection = this.getConnectionObject ();

        //only set the user key if it is not null.
        if (apiKey != null) {
            connection.setRequestProperty ("user-key", apiKey);
        }

        //set request method.
        this.setRequestMethod (reqMethod);

        //switch statement dedicated to dictating next steps based on request method.
        switch (reqMethod.toLowerCase ()) {
            case "post":
                //enable output.
                connection.setDoOutput (true);

                //POST results to server.
                DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
                wr.writeUTF (this.getAPIConfigParameters ());
                break;

            //no further action defined for "GET" request methods.
            case "get":
                break;
            default:
                throw new Exception ("[" + reqMethod + "] is not a valid API request method.");
        }
    }

    /**
     * parseAPIResults: a method that extracts the API result string from connection input stream.
     * @param _attributes: A String representation of the nested attribute from which to set the API return string.
     * @throws IOException
     * @throws Exception
     */
    private void parseAPIResults (String _attributes) throws IOException, Exception {
        String ret = "";
        String inputLine;
        HttpURLConnection connection = this.getConnectionObject ();

        //verify response code is 200, indicating that request was successful. Otherwise, throw exception.
        if (connection.getResponseCode () != this.API_SUCCESS_CODE)
            throw new Exception ("API response code " + connection.getResponseCode () + ": " + connection.getResponseMessage ());
        else {
            //set up buffer reader with the connection input stream.
            BufferedReader response = new BufferedReader (new InputStreamReader (connection.getInputStream ()));
            //strBuff = new StringBuffer ();

            //continue appension until end of the buffer is reached.
            while ((inputLine = response.readLine ()) != null) {
                ret = ret.concat (inputLine);
            }

            //set the API return variable and format accordingly.
            this.setAPIResultString (ret);

            String [] attributes = _attributes.split (";");
            for (String attribute : attributes) {
                //parse desired attribute.
                this.formatAPIResultString ();
                this.setAPIParseObject ();
                this.setAPIResultString(this.getAPIParseObject ().getString (attribute));
            }
            this.formatAPIResultString ();
        }
    }

    /**
     * closeConnection: a method to close the dedicated connection.
     */
    public void closeConnection () {
        this.getConnectionObject ().disconnect ();
    }
    
    public LinkedHashMap<String, Object> toMap () throws JSONException {  
        this.setAPIParseObject ();
        return this.toMap (this.getAPIParseObject ());
    }

    /**
     * toMap: a method dedicated to converting the API result string into a LinkedHashMap.
     * @param _apiParse: JSONObject containing the API result string.
     * @return
     * @throws JSONException
     */
    public LinkedHashMap <String, Object> toMap (JSONObject _apiParse) throws JSONException {
        LinkedHashMap <String, Object> map = new LinkedHashMap <> ();

        //continue iteration until end of the key set has been reached.
        Iterator <String> keyIterator = _apiParse.keys ();
        while (keyIterator.hasNext ()) {
            String key = keyIterator.next ();
            Object value = _apiParse.get (key);

            //determine next steps based on data type of value Object.
            if (value instanceof JSONArray)
                value = this.toArrayList ((JSONArray) value);
            else if (value instanceof JSONObject)
                value = this.toMap ((JSONObject) value);

            //enter key-value pair into map.
            map.put (key, value);
        }
        return map;
    }

    /**
     * toArrayList: a method dedicated to converting a JSON attribute (which contains a nested structure of child nodes) to an ArrayList.
     * @param _array: JSONArray.
     * @return ArrayList (Object): an ArrayList object with the attributes converted and stored.
     * @throws JSONException
     */
    public ArrayList <Object> toArrayList (JSONArray _array) throws JSONException {
        ArrayList<Object> list = new ArrayList<>();

        for(int i = 0; i < _array.length(); i++) {
            Object value = _array.get(i);

            //determine next steps based on data type.
            if(value instanceof JSONArray)
                value = this.toArrayList((JSONArray) value);
            else if(value instanceof JSONObject)
                value = this.toMap((JSONObject) value);

            //add value to ArrayList.
            list.add(value);
        }
        return list;
    }

    //================= GETTERS ===============

    /**
     * getAPIConfigParameters: a method which converts the internal LinkedHashMap of API parameters into a String representation.
     * @return String: delimited list of combined API parameters.
     */
    public String getAPIConfigParameters () {
        this.parameterString = "";
        char delimiter;
        Set <String> set = this.getConfigObject ().keySet();

        for (String key : set) {
            //if the parameter set is empty, the first parameter must have the '?' indicator; otherwise, set indicator to '&'.
            delimiter = (this.parameterString.isEmpty () ? '?' : '&');
            this.parameterString = this.parameterString.concat ( delimiter + key + "=" + this.getConfigObject ().get (key));
        }
        return this.parameterString;
    }

    /**
     * getAPIResultString: method that returns the internal API result.
     * @return String
     */
    public String getAPIResultString () {            
        return this.apiReturn;
    }

    /**
     * getConfigObject: a method that returns the configuration map.
     * @return LinkedHashMap <String, String>
     */
    public LinkedHashMap <String, String> getConfigObject () {
        return this.config;
    }

    /**
     * getConnectionObject: a method that returns the connection object.
     * @return HttpURLConnection
     */
    public HttpURLConnection getConnectionObject () {
        return this.connect;
    }
    
    public int getAPIResponseCode () throws IOException {
        return this.getConnectionObject().getResponseCode();
    }

    /**
     * getAPIParseObject: method that returns the internal API parse object model.
     * @return JSONObject
     */
    public JSONObject getAPIParseObject () {
        return this.apiParse;
    }

    /**
     * getRequestMethod: a method dedicated to retrieving the API request method.
     * @return String
     */
    public String getRequestMethod () {
        return this.requestMethod;
    }

    /**
     * getURLObject: a method that handles retrieving the internal URL object model.
     * @return URL
     */
    public URL getURLObject () {
        return this.url;
    }

    /**
     * getURLString: a method that handles retrieving the internal URL string.
     * @return String
     */
    public String getURLString () {
        return this.urlSite;
    }

    /**
     * getUserKey: a method that handles retrieving the internal user key string.
     * @return String
     */
    public String getUserKey () {
        return this.userKey;
    }

    //================= SETTERS ===============

    /**
     * setAPIConfigParameter: method that adds key-value pairs into the internal API config map.
     * @param _key: String representation of the API parameter tag.
     * @param _val: String representation of the API value indicator.
     */
    public void setAPIConfigParameter (String _key, String _val) {
        this.getConfigObject ().put(_key, _val);
    }

    /**
     * setAPIResultString: method that updates the internal API result string.
     * @param _setString: String representation of the  API result string to which the internal variable will be set.
     */
    public void setAPIResultString (String _setString) {
        this.apiReturn = _setString;
    }

    /**
     * setConnectionObject: method that instantiates the HTTPURLConnection object model.
     * @throws IOException
     */
    public void setConnectionObject () throws IOException {
        this.connect = (HttpURLConnection) this.getURLObject().openConnection();
    }

    /**
     * setAPIParseObject: method dedicated to initializing the internal API parse object model, based on internal API result string.
     * @throws JSONException
     */
    public void setAPIParseObject () throws JSONException {
        this.apiParse = new JSONObject (this.getAPIResultString());
    }

    /**
     * setRequestMethod: method that handles setting internal request Method parameter.
     * @param _requestMethod: Request Method ("GET", "POST")
     */
    public void setRequestMethod (String _requestMethod) {
        this.requestMethod = _requestMethod;
    }

    /**
     * setURLObject: method that instantiates the internal URL object model, derived from url and parameter strings.
     * @throws MalformedURLException
     */
    public void setURLObject () throws MalformedURLException {
        this.url = new URL (this.getURLString() + this.getAPIConfigParameters());
    }

    /**
     * setURLString: method that modifies the internal URL string.
     * @param _urlSite: String representation of the URL that will be used for object definition.
     */
    public void setURLString (String _urlSite) {
        this.urlSite = _urlSite;
    }

    /**
     * setUserKey: method that updates the internal user key, dedicated for API authentication.
     * @param _userKey: String representation of the API key.
     */
    public void setUserKey (String _userKey) {
        this.userKey = _userKey;
    }
}
