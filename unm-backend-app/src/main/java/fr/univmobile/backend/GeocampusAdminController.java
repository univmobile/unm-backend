package fr.univmobile.backend;

import java.io.IOException;

import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;
import fr.univmobile.backend.AbstractBackendController;

@Paths({ "geocampus/admin", "geocampus/admin/" })
public class GeocampusAdminController extends AbstractBackendController {

	public GeocampusAdminController() {

	}

	@Override
	public View action() throws IOException, TransactionException {
		return new View("geocampus_admin.jsp");
	}
}
