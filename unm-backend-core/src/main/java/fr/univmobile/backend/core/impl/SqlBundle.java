package fr.univmobile.backend.core.impl;

import net.avcompris.binding.annotation.XPath;

/**
 * YAML-Java binder for the "sql.yaml" bundle file. SQL queries are organized
 * by queryIds, then by dbType. E.g.:
 * <pre>
 * bundle:
 *   createTable_categories:
 *     mysql: CREATE TABLE unm_categories (etc.)
 *     h2: CREATE TABLE unm_categories (etc.)
 *   clean_categories:
 *     mysql: DELETE FROM unm_categories (etc.)
 *     h2: DELETE FROM unm_categories (etc.)
 *   ...
 * </pre>
 * If several dbTypes share the same SQL query for a given queryId, you can
 * declare them, separated by a dot:
 * <pre>
 * bundle:
 *   clean_categories:
 *     mysql.h2: DELETE FROM unm_categories (etc.)
 *   ...
 * </pre>
 */
@XPath("/bundle")
interface SqlBundle {

	/**
	 * For instance with dbType = \"mysql\",
	 * get all the /bundle/(...)/@mysql nodes.
	 */
	@XPath("*/@*[contains(concat(name(), '.'), concat($arg0, '.'))]")
	SqlQueries getQueriesByDbType(String dbType);

	boolean isNullQueriesByDbType(String dbType);
	
	int sizeOfQueriesByDbType(String dbType);
	
	interface SqlQueries {

		@XPath(value="parent::*", function="name()")
		String[] getQueryIds();
		
		/**
		 * For instance with dbType = \"mysql\"
		 * and queryId = \"createTable_categories\",
		 * get the corresponding SQL query.
		 */
		@XPath("self::node()[name(parent::*) = $arg0]")
		String getQuery(String queryId);
	}
	
	@XPath("*[name() = $arg0]/@*[contains(concat(name(), '.'), concat($arg1, '.'))]")
	String getQuery(String queryId, String dbType);
	
	boolean isNullQuery(String queryId, String dbType);
}
