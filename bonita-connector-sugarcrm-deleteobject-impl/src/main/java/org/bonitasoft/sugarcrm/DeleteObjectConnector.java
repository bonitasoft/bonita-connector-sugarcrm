package org.bonitasoft.sugarcrm;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public class DeleteObjectConnector implements Connector {
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String BASE_URL = "baseURL";
    public static final String OBJECT_TYPE = "objectType";
    public static final String OBJECT_ID = "objectId";

    String login;
    String password;
    String baseURL;
    String objectType;
    String objectId;
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
        objectId = (String) parameters.get(OBJECT_ID);
        LOGGER.info(OBJECT_ID + " " + objectId);
    }

    @Override
    public void validateInputParameters() throws ConnectorValidationException {

    }

    @Override
    public Map<String, Object> execute() throws ConnectorException {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            sugarCRMUtils.deleteARecord(sessionId, objectType, objectId);
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
