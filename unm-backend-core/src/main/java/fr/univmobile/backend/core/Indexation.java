package fr.univmobile.backend.core;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nullable;

import org.xml.sax.SAXException;

public interface Indexation {

	void indexData(@Nullable IndexationObserver observer) throws SQLException, IOException, SAXException;
}
