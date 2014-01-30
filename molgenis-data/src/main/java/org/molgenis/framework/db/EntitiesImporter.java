package org.molgenis.framework.db;

import java.io.File;
import java.io.IOException;

import org.molgenis.data.DatabaseAction;
import org.molgenis.data.EntitySource;
import org.molgenis.data.Repository;

/**
 * Import entities into the database
 */
public interface EntitiesImporter
{
	/**
	 * Import entities from a {@link java.io.File}
	 * 
	 * @param file
	 * @param dbAction
	 * @return
	 * @throws IOException
	 */
	public EntityImportReport importEntities(File file, DatabaseAction dbAction) throws IOException;

/**
	 * Import entities from a {@link org.molgenis.data.Repository
	 * 
	 * @param tupleReader
	 * @param entityName
	 * @param dbAction
	 * @return
	 * @throws IOException
	 */
	public EntityImportReport importEntities(Repository repository, String entityName, DatabaseAction dbAction)
			throws IOException;

	/**
	 * Import entities from a {@link org.molgenis.data.EntitySource}
	 * 
	 * @param tupleReader
	 * @param entityName
	 * @param dbAction
	 * @return
	 * @throws IOException
	 */
	public EntityImportReport importEntities(EntitySource entitySource, DatabaseAction dbAction) throws IOException;
}
