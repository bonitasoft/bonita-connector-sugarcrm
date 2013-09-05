package org.bonitasoft.sugarcrm;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bonitasoft.engine.connector.ConnectorException;
import org.junit.Test;

public class CreateObjectConnectorTest {
	
	@Test
	public void testCreateObject() throws IOException, ConnectorException{
		Properties sugarCrmProperties = new Properties();
		sugarCrmProperties.load(CreateObjectConnectorTest.class.getClassLoader().getResourceAsStream("sugar_crm.properties"));
		CreateObjectConnector connector = new CreateObjectConnector();
		Map<String, Object> inputParameters = new HashMap<String, Object>();
		inputParameters.put(CreateObjectConnector.LOGIN, sugarCrmProperties.get(CreateObjectConnector.LOGIN));
		inputParameters.put(CreateObjectConnector.PASSWORD, sugarCrmProperties.get(CreateObjectConnector.PASSWORD));
		inputParameters.put(CreateObjectConnector.BASE_URL, sugarCrmProperties.get(CreateObjectConnector.BASE_URL));
		inputParameters.put(CreateObjectConnector.OBJECT_TYPE, "Accounts");
		List<List<String>> parameters = new ArrayList<List<String>>();
		List<String> id = new ArrayList<String>();
		id.add("name");
		id.add("CreateObjectConnectorTest");
		parameters.add(id);
		inputParameters.put(CreateObjectConnector.PARAMETERS, parameters);
		connector.setInputParameters(inputParameters);
		connector.connect();
		Map<String, Object> result = connector.execute();
		connector.disconnect();
		assertNotNull(result);
	}
	
}
