package org.bonitasoft.sugarcrm;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public class CreateObjectConnector implements Connector {
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String BASE_URL = "baseURL";
    public static final String OBJECT_TYPE = "objectType";
    String login;
    String password;
    String baseURL;
    String objectType;
    Map<String, String> requestParameters;
    String sessionId;
    SugarCRMUtils sugarCRMUtils;
    private Logger LOGGER = Logger.getLogger(this.getClass().getName());

    @Override
    public void setInputParameters(Map<String, Object> parameters) {
        login = (String) parameters.get(LOGIN);
        LOGGER.info(LOGIN + " " + login);
        password = (String) parameters.get(PASSWORD);
        LOGGER.info(PASSWORD + " ******");
        baseURL = (String) parameters.get(BASE_URL);
        LOGGER.info(BASE_URL + " " + baseURL);
        objectType = (String) parameters.get(OBJECT_TYPE);
        LOGGER.info(OBJECT_TYPE + " " + objectType);
        requestParameters =  SugarCRMUtils.listToMap((List<List<Object>>) parameters.get("parameters"));
        for (String parameterName : requestParameters.keySet()) {
            LOGGER.info("Parameter " + parameterName + " " + requestParameters.get(parameterName));
        }
    }

    @Override
    public void validateInputParameters() throws ConnectorValidationException {

    }

    @Override
    public Map<String, Object> execute() throws ConnectorException {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result.put("jsonObject", sugarCRMUtils.createARecord(sessionId, objectType, requestParameters));
            return result;
        } catch (IOException e) {
            throw new ConnectorException(e);
        }

    }

    @Override
    public void connect() throws ConnectorException {
        sugarCRMUtils = new SugarCRMUtils(baseURL);
        try {
            sessionId = sugarCRMUtils.login(login, password);
        } catch (NoSuchAlgorithmException e) {
            throw new ConnectorException(e);
        } catch (IOException e) {
            throw new ConnectorException(e);
        }
    }

    @Override
    public void disconnect() throws ConnectorException {
        try {
            sessionId = sugarCRMUtils.logout(sessionId);
        } catch (IOException e) {
            throw new ConnectorException(e);
        }
    }
}
