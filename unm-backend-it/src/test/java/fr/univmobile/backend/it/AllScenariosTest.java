package fr.univmobile.backend.it;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.univmobile.it.commons.AbstractScenariosTest;
import fr.univmobile.it.commons.ScenarioContext;

@RunWith(Parameterized.class)
public class AllScenariosTest extends AbstractScenariosTest {

	@Parameters(name = "{0}")
	public static Iterable<Object[]> parameters() throws Exception {

		return AbstractScenariosTest.loadParametersForScenarioClasses( //
				Scenarios001.class);
	}

	public AllScenariosTest(final ScenarioContext context) {

		super(context);
	}
}
